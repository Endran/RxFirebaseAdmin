/*
 * Copyright 2017 David Hardy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.endran.rxfirebaseadmin;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import nl.endran.rxfirebaseadmin.exceptions.RxFirebaseDataCastException;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class DataSnapshotMapper<T, U> implements Func1<T, U> {

    private DataSnapshotMapper() {
    }

    public static <U> DataSnapshotMapper<DataSnapshot, U> of(Class<U> clazz) {
        return new TypedDataSnapshotMapper<U>(clazz);
    }

    public static <U> DataSnapshotMapper<DataSnapshot, List<U>> listOf(Class<U> clazz) {
        return new TypedListDataSnapshotMapper<>(clazz);
    }

    public static <U> DataSnapshotMapper<DataSnapshot, LinkedHashMap<String, U>> mapOf(Class<U> clazz) {
        return new TypedMapDataSnapshotMapper<>(clazz);
    }

    public static <U> DataSnapshotMapper<DataSnapshot, U> of(GenericTypeIndicator<U> genericTypeIndicator) {
        return new GenericTypedDataSnapshotMapper<U>(genericTypeIndicator);
    }

    public static <U> DataSnapshotMapper<RxFirebaseChildEvent<DataSnapshot>, RxFirebaseChildEvent<U>> ofChildEvent(Class<U> clazz) {
        return new ChildEventDataSnapshotMapper<U>(clazz);
    }

    private static <U> U getDataSnapshotTypedValue(DataSnapshot dataSnapshot, Class<U> clazz) {
        U value = dataSnapshot.getValue(clazz);
        if (value == null) {
            throw Exceptions.propagate(new RxFirebaseDataCastException(
                    "unable to cast firebase data response to " + clazz.getSimpleName()));
        }
        return value;
    }

    private static class TypedDataSnapshotMapper<U> extends DataSnapshotMapper<DataSnapshot, U> {

        private final Class<U> clazz;

        public TypedDataSnapshotMapper(final Class<U> clazz) {
            this.clazz = clazz;
        }

        @Override
        public U call(final DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                return getDataSnapshotTypedValue(dataSnapshot, clazz);
            } else {
                return null;
            }
        }
    }

    private static class TypedListDataSnapshotMapper<U> extends DataSnapshotMapper<DataSnapshot, List<U>> {

        private final Class<U> clazz;

        public TypedListDataSnapshotMapper(final Class<U> clazz) {
            this.clazz = clazz;
        }

        @Override
        public List<U> call(final DataSnapshot dataSnapshot) {
            List<U> items = new ArrayList<>();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                items.add(getDataSnapshotTypedValue(childSnapshot, clazz));
            }
            return items;
        }
    }

    private static class TypedMapDataSnapshotMapper<U> extends DataSnapshotMapper<DataSnapshot, LinkedHashMap<String, U>> {

        private final Class<U> clazz;

        public TypedMapDataSnapshotMapper(final Class<U> clazz) {
            this.clazz = clazz;
        }

        @Override
        public LinkedHashMap<String, U> call(final DataSnapshot dataSnapshot) {
            LinkedHashMap<String, U> items = new LinkedHashMap<>();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                items.put(childSnapshot.getKey(), getDataSnapshotTypedValue(childSnapshot, clazz));
            }
            return items;
        }
    }

    private static class GenericTypedDataSnapshotMapper<U> extends DataSnapshotMapper<DataSnapshot, U> {

        private final GenericTypeIndicator<U> genericTypeIndicator;

        public GenericTypedDataSnapshotMapper(GenericTypeIndicator<U> genericTypeIndicator) {
            this.genericTypeIndicator = genericTypeIndicator;
        }

        @Override
        public U call(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                U value = dataSnapshot.getValue(genericTypeIndicator);
                if (value == null) {
                    throw Exceptions.propagate(new RxFirebaseDataCastException(
                            "unable to cast firebase data response to generic type"));
                }
                return value;
            } else {
                return null;
            }
        }
    }

    private static class ChildEventDataSnapshotMapper<U>
            extends DataSnapshotMapper<RxFirebaseChildEvent<DataSnapshot>, RxFirebaseChildEvent<U>> {

        private final Class<U> clazz;

        public ChildEventDataSnapshotMapper(final Class<U> clazz) {
            this.clazz = clazz;
        }

        @Override
        public RxFirebaseChildEvent<U> call(final RxFirebaseChildEvent<DataSnapshot> rxFirebaseChildEvent) {
            DataSnapshot dataSnapshot = rxFirebaseChildEvent.getValue();
            if (dataSnapshot.exists()) {
                return new RxFirebaseChildEvent<U>(
                        dataSnapshot.getKey(),
                        getDataSnapshotTypedValue(dataSnapshot, clazz),
                        rxFirebaseChildEvent.getPreviousChildName(),
                        rxFirebaseChildEvent.getEventType());
            } else {
                throw Exceptions.propagate(new RuntimeException("child dataSnapshot doesn't exist"));
            }
        }
    }
}
