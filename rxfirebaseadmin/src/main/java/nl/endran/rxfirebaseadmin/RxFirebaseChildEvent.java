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

public class RxFirebaseChildEvent<T> {

    private EventType eventType;
    private String key;
    private T value;
    private String previousChildName;

    public RxFirebaseChildEvent(String key,
                                T value,
                                String previousChildName,
                                EventType eventType) {
        this.key = key;
        this.value = value;
        this.previousChildName = previousChildName;
        this.eventType = eventType;
    }


    public RxFirebaseChildEvent(String key, T data, EventType eventType) {
        this.key = key;
        this.value = data;
        this.eventType = eventType;
    }


    public String getKey() {
        return key;
    }


    public T getValue() {
        return value;
    }


    public String getPreviousChildName() {
        return previousChildName;
    }


    public EventType getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RxFirebaseChildEvent<?> that = (RxFirebaseChildEvent<?>) o;

        if (eventType != that.eventType) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return previousChildName != null ? previousChildName.equals(that.previousChildName) : that.previousChildName == null;

    }

    @Override
    public int hashCode() {
        int result = eventType != null ? eventType.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (previousChildName != null ? previousChildName.hashCode() : 0);
        return result;
    }

    public enum EventType {
        ADDED,
        CHANGED,
        REMOVED,
        MOVED
    }
}
