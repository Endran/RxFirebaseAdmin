/*
 * Copyright 2017 David Hardy
 * Copyright 2016 Nick Moskalenko
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

package nl.endran.rxfirebaseadmin.exceptions;

public class RxFirebaseDataCastException extends Exception {

    public RxFirebaseDataCastException() {
    }

    public RxFirebaseDataCastException(String detailMessage) {
        super(detailMessage);
    }

    public RxFirebaseDataCastException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RxFirebaseDataCastException(Throwable throwable) {
        super(throwable);
    }
}
