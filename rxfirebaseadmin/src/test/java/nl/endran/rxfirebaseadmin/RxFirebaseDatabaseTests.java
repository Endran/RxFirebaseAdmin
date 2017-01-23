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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.*;

import static org.mockito.Mockito.*;

public class RxFirebaseDatabaseTests {

    @Mock
    private DatabaseReference mockDatabase;

    @Mock
    private DataSnapshot mockFirebaseDataSnapshot;

    private TestData testData = new TestData();
    private List<TestData> testDataList = new ArrayList<>();
    private Map<String, TestData> testDataMap = new HashMap<>();

    private RxFirebaseChildEvent<TestData> testChildEventAdded;
    private RxFirebaseChildEvent<TestData> testChildEventChanged;
    private RxFirebaseChildEvent<TestData> testChildEventRemoved;
    private RxFirebaseChildEvent<TestData> testChildEventMoved;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        testDataList.add(testData);
        testDataMap.put("key", testData);
        testChildEventAdded = new RxFirebaseChildEvent<>("key", testData, "root", RxFirebaseChildEvent.EventType.ADDED);
        testChildEventChanged = new RxFirebaseChildEvent<>("key", testData, "root", RxFirebaseChildEvent.EventType.CHANGED);
        testChildEventRemoved = new RxFirebaseChildEvent<>("key", testData, RxFirebaseChildEvent.EventType.REMOVED);
        testChildEventMoved = new RxFirebaseChildEvent<>("key", testData, "root", RxFirebaseChildEvent.EventType.MOVED);

        when(mockFirebaseDataSnapshot.exists()).thenReturn(true);
        when(mockFirebaseDataSnapshot.getValue(TestData.class)).thenReturn(testData);
        when(mockFirebaseDataSnapshot.getKey()).thenReturn("key");
        when(mockFirebaseDataSnapshot.getChildren()).thenReturn(Arrays.asList(mockFirebaseDataSnapshot));
    }

    @Test
    public void testObserveSingleValue() throws InterruptedException {

        TestSubscriber<TestData> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeSingleValueEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(mockFirebaseDataSnapshot);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testData));
        testSubscriber.assertCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveSingleNoData() throws InterruptedException {

        DataSnapshot mockFirebaseDataSnapshotNoData = mock(DataSnapshot.class);
        when(mockFirebaseDataSnapshotNoData.exists()).thenReturn(false);

        TestSubscriber<TestData> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeSingleValueEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(mockFirebaseDataSnapshotNoData);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveSingleWrongType() throws InterruptedException {

        TestSubscriber<WrongType> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeSingleValueEvent(mockDatabase, WrongType.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(mockFirebaseDataSnapshot);

        testSubscriber.assertError(RuntimeException.class);
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveSingleValue_Disconnected() throws InterruptedException {

        TestSubscriber<TestData> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeSingleValueEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onCancelled(DatabaseError.fromCode(DatabaseError.DISCONNECTED));

        testSubscriber.assertError(RxFirebaseDataException.class);
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveSingleValueEvent_Failed() throws InterruptedException {

        TestSubscriber<List<TestData>> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeSingleValueEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .toList()
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onCancelled(DatabaseError.fromCode(DatabaseError.OPERATION_FAILED));

        testSubscriber.assertError(RxFirebaseDataException.class);
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveValueEvent() throws InterruptedException {

        TestSubscriber<TestData> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeValueEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addValueEventListener(argument.capture());
        argument.getValue().onDataChange(mockFirebaseDataSnapshot);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testData));
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testSingleValueEvent() throws InterruptedException {

        TestSubscriber<TestData> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeSingleValueEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(mockFirebaseDataSnapshot);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testData));
        testSubscriber.assertCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveValueEventList() throws InterruptedException {

        TestSubscriber<List<TestData>> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeSingleValueEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .toList()
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(mockFirebaseDataSnapshot);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testDataList));
        testSubscriber.assertCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveValuesMap() throws InterruptedException {

        TestSubscriber<Map<String, TestData>> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeSingleValueEvent(mockDatabase)
                .subscribeOn(Schedulers.immediate())
                .toMap(new Func1<DataSnapshot, String>() {
                    @Override
                    public String call(DataSnapshot dataSnapshot) {
                        return dataSnapshot.getKey();
                    }
                }, new Func1<DataSnapshot, TestData>() {
                    @Override
                    public TestData call(DataSnapshot dataSnapshot) {
                        return dataSnapshot.getValue(TestData.class);
                    }
                }, new Func0<Map<String, TestData>>() {
                    @Override
                    public Map<String, TestData> call() {
                        return new LinkedHashMap<String, TestData>();
                    }
                })
                .subscribe(testSubscriber);

        ArgumentCaptor<ValueEventListener> argument = ArgumentCaptor.forClass(ValueEventListener.class);
        verify(mockDatabase).addListenerForSingleValueEvent(argument.capture());
        argument.getValue().onDataChange(mockFirebaseDataSnapshot);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testDataMap));
        testSubscriber.assertCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveChildEvent_Added() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<TestData>> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeChildEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(mockDatabase).addChildEventListener(argument.capture());
        argument.getValue().onChildAdded(mockFirebaseDataSnapshot, "root");

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testChildEventAdded));
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveChildEvent_Changed() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<TestData>> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeChildEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(mockDatabase).addChildEventListener(argument.capture());
        argument.getValue().onChildChanged(mockFirebaseDataSnapshot, "root");

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testChildEventChanged));
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveChildEvent_Removed() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<TestData>> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeChildEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(mockDatabase).addChildEventListener(argument.capture());
        argument.getValue().onChildRemoved(mockFirebaseDataSnapshot);

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testChildEventRemoved));
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveChildEvent_Moved() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<TestData>> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeChildEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(mockDatabase).addChildEventListener(argument.capture());
        argument.getValue().onChildMoved(mockFirebaseDataSnapshot, "root");

        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(testChildEventMoved));
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    @Test
    public void testObserveChildEvent_Cancelled() throws InterruptedException {

        TestSubscriber<RxFirebaseChildEvent<TestData>> testSubscriber = new TestSubscriber<>();
        RxFirebaseDatabase.observeChildEvent(mockDatabase, TestData.class)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        ArgumentCaptor<ChildEventListener> argument = ArgumentCaptor.forClass(ChildEventListener.class);
        verify(mockDatabase).addChildEventListener(argument.capture());
        argument.getValue().onCancelled(DatabaseError.fromCode(DatabaseError.DISCONNECTED));

        testSubscriber.assertError(RxFirebaseDataException.class);
        testSubscriber.assertNotCompleted();
        testSubscriber.unsubscribe();
    }

    class TestData {
        int id;
        String str;
    }

    class WrongType {
        String somethingWrong;
        long more;
    }
}
