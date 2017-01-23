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

import com.google.firebase.database.*;
import nl.endran.rxfirebaseadmin.exceptions.RxFirebaseDataException;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public class RxFirebaseDatabase {


    public static Observable<DataSnapshot> observeValueEvent(final Query query) {
        return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
            @Override
            public void call(final Subscriber<? super DataSnapshot> subscriber) {
                final ValueEventListener valueEventListener = query.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext(dataSnapshot);
                                }
                            }

                            @Override
                            public void onCancelled(final DatabaseError error) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onError(new RxFirebaseDataException(error));
                                }
                            }
                        });

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        query.removeEventListener(valueEventListener);
                    }
                }));
            }
        });
    }


    public static Observable<DataSnapshot> observeSingleValueEvent(final Query query) {
        return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
            @Override
            public void call(final Subscriber<? super DataSnapshot> subscriber) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(dataSnapshot);
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(new RxFirebaseDataException(error));
                        }
                    }
                });
            }
        });
    }


    public static Observable<RxFirebaseChildEvent<DataSnapshot>> observeChildEvent(
            final Query query) {
        return Observable.create(new Observable.OnSubscribe<RxFirebaseChildEvent<DataSnapshot>>() {
            @Override
            public void call(final Subscriber<? super RxFirebaseChildEvent<DataSnapshot>> subscriber) {
                final ChildEventListener childEventListener = query.addChildEventListener(
                        new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext(
                                            new RxFirebaseChildEvent<DataSnapshot>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
                                                    RxFirebaseChildEvent.EventType.ADDED));
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext(
                                            new RxFirebaseChildEvent<DataSnapshot>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
                                                    RxFirebaseChildEvent.EventType.CHANGED));
                                }
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext(new RxFirebaseChildEvent<DataSnapshot>(dataSnapshot.getKey(), dataSnapshot,
                                            RxFirebaseChildEvent.EventType.REMOVED));
                                }
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext(
                                            new RxFirebaseChildEvent<DataSnapshot>(dataSnapshot.getKey(), dataSnapshot, previousChildName,
                                                    RxFirebaseChildEvent.EventType.MOVED));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onError(new RxFirebaseDataException(error));
                                }
                            }
                        });

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        query.removeEventListener(childEventListener);
                    }
                }));
            }
        });
    }


    public static <T> Observable<T> observeValueEvent(final Query query,
                                                      final Class<T> clazz) {
        return observeValueEvent(query, DataSnapshotMapper.of(clazz));
    }


    public static <T> Observable<T> observeSingleValueEvent(final Query query,
                                                            final Class<T> clazz) {
        return observeSingleValueEvent(query, DataSnapshotMapper.of(clazz));
    }


    public static <T> Observable<RxFirebaseChildEvent<T>> observeChildEvent(
            final Query query, final Class<T> clazz) {
        return observeChildEvent(query, DataSnapshotMapper.ofChildEvent(clazz));
    }


    public static <T> Observable<T> observeValueEvent(final Query query,
                                                      final Func1<? super DataSnapshot, ? extends T> mapper) {
        return observeValueEvent(query).map(mapper);
    }


    public static <T> Observable<T> observeSingleValueEvent(final Query query,
                                                            final Func1<? super DataSnapshot, ? extends T> mapper) {
        return observeSingleValueEvent(query).map(mapper);
    }


    public static <T> Observable<RxFirebaseChildEvent<T>> observeChildEvent(
            final Query query, final Func1<? super RxFirebaseChildEvent<DataSnapshot>, ? extends RxFirebaseChildEvent<T>> mapper) {
        return observeChildEvent(query).map(mapper);
    }
}
