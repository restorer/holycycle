package com.eightsines.holycycle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ViewControllerLifecycleRegistryTest {
    private ViewControllerLifecycleTracker tracker;
    private ViewControllerLifecycleObserver observer;
    private ViewControllerLifecycleRegistry registry;
    private ViewControllerLifecycleObserver anotherObserver;

    @Before
    public void setUp() {
        tracker = Mockito.mock(ViewControllerLifecycleTracker.class);
        observer = Mockito.mock(ViewControllerLifecycleObserver.class);
        registry = new ViewControllerLifecycleRegistry(tracker);
    }

    @Test
    public void testIsControllerStateAtLeastStarted() {
        prepareTracker(ViewControllerLifecycle.STATE_RESUMED);

        Assert.assertTrue(registry.isControllerStateAtLeast(ViewControllerLifecycle.STATE_STARTED));
        Assert.assertTrue(registry.isControllerStateAtLeast(ViewControllerLifecycle.STATE_RESUMED));
        Assert.assertFalse(registry.isControllerStateAtLeast(ViewControllerLifecycle.STATE_FOCUSED));

        verifyTracker();
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddControllerLifecycleObserverInitialized() {
        addAndVerifyInitializedObserver();
    }

    @Test
    public void testAddControllerLifecycleObserverStarted() {
        prepareTracker(ViewControllerLifecycle.STATE_STARTED);
        registry.addControllerLifecycleObserver(observer);
        verifyTracker();
        Mockito.verify(observer).onControllerStart();
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddControllerLifecycleObserverResumed() {
        prepareTracker(ViewControllerLifecycle.STATE_RESUMED);
        registry.addControllerLifecycleObserver(observer);
        verifyTracker();
        Mockito.verify(observer).onControllerStart();
        Mockito.verify(observer).onControllerResume();
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddControllerLifecycleObserverFocused() {
        prepareTracker(ViewControllerLifecycle.STATE_FOCUSED);
        registry.addControllerLifecycleObserver(observer);
        verifyTracker();
        Mockito.verify(observer).onControllerStart();
        Mockito.verify(observer).onControllerResume();
        Mockito.verify(observer).onControllerFocus();
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddControllerLifecycleObserverExisting() {
        addAndVerifyInitializedObserver();
        prepareTracker(ViewControllerLifecycle.STATE_FOCUSED);
        registry.addControllerLifecycleObserver(observer);
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveControllerLifecycleObserverInitialized() {
        addAndVerifyInitializedObserver();
        registry.removeControllerLifecycleObserver(observer);
        verifyTracker();
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveControllerLifecycleObserverStarted() {
        addAndVerifyInitializedObserver();
        prepareTracker(ViewControllerLifecycle.STATE_STARTED);
        registry.removeControllerLifecycleObserver(observer);
        verifyTracker();
        Mockito.verify(observer).onControllerStop();
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveControllerLifecycleObserverResumed() {
        addAndVerifyInitializedObserver();
        prepareTracker(ViewControllerLifecycle.STATE_RESUMED);
        registry.removeControllerLifecycleObserver(observer);
        verifyTracker();
        Mockito.verify(observer).onControllerPause();
        Mockito.verify(observer).onControllerStop();
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveControllerLifecycleObserverFocused() {
        addAndVerifyInitializedObserver();
        prepareTracker(ViewControllerLifecycle.STATE_FOCUSED);
        registry.removeControllerLifecycleObserver(observer);
        verifyTracker();
        Mockito.verify(observer).onControllerBlur();
        Mockito.verify(observer).onControllerPause();
        Mockito.verify(observer).onControllerStop();
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveControllerLifecycleObserverNonExisting() {
        prepareTracker(ViewControllerLifecycle.STATE_FOCUSED);
        registry.removeControllerLifecycleObserver(observer);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnControllerStart() {
        addAndVerifyInitializedObserver();
        registry.onControllerStart();
        Mockito.verify(observer).onControllerStart();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnControllerResume() {
        addAndVerifyInitializedObserver();
        registry.onControllerResume();
        Mockito.verify(observer).onControllerResume();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnControllerFocus() {
        addAndVerifyInitializedObserver();
        registry.onControllerFocus();
        Mockito.verify(observer).onControllerFocus();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnControllerBlur() {
        addAndVerifyInitializedObserver();
        registry.onControllerBlur();
        Mockito.verify(observer).onControllerBlur();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnControllerPause() {
        addAndVerifyInitializedObserver();
        registry.onControllerPause();
        Mockito.verify(observer).onControllerPause();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnControllerPersistUserData() {
        addAndVerifyInitializedObserver();
        registry.onControllerPersistUserData();
        Mockito.verify(observer).onControllerPersistUserData();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnControllerStop() {
        addAndVerifyInitializedObserver();
        registry.onControllerStop();
        Mockito.verify(observer).onControllerStop();
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddObserverWhileInStart() {
        addAndVerifyInitializedObserver();
        prepareAnotherObserverForAdditionWhen().onControllerStart();
        prepareTracker(ViewControllerLifecycle.STATE_STARTED);

        registry.onControllerStart();

        verifyTracker();
        Mockito.verify(observer).onControllerStart();
        Mockito.verify(anotherObserver).onControllerStart();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddObserverWhileInResume() {
        addAndVerifyInitializedObserver();
        prepareAnotherObserverForAdditionWhen().onControllerResume();
        prepareTracker(ViewControllerLifecycle.STATE_RESUMED);

        registry.onControllerResume();

        verifyTracker();
        Mockito.verify(observer).onControllerResume();
        Mockito.verify(anotherObserver).onControllerStart();
        Mockito.verify(anotherObserver).onControllerResume();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddObserverWhileInFocus() {
        addAndVerifyInitializedObserver();
        prepareAnotherObserverForAdditionWhen().onControllerFocus();
        prepareTracker(ViewControllerLifecycle.STATE_FOCUSED);

        registry.onControllerFocus();

        verifyTracker();
        Mockito.verify(observer).onControllerFocus();
        Mockito.verify(anotherObserver).onControllerStart();
        Mockito.verify(anotherObserver).onControllerResume();
        Mockito.verify(anotherObserver).onControllerFocus();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddObserverWhileInBlur() {
        addAndVerifyInitializedObserver();
        prepareAnotherObserverForAdditionWhen().onControllerBlur();
        prepareTracker(ViewControllerLifecycle.STATE_RESUMED);

        registry.onControllerBlur();

        verifyTracker();
        Mockito.verify(observer).onControllerBlur();
        Mockito.verify(anotherObserver).onControllerStart();
        Mockito.verify(anotherObserver).onControllerResume();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddObserverWhileInPause() {
        addAndVerifyInitializedObserver();
        prepareAnotherObserverForAdditionWhen().onControllerPause();
        prepareTracker(ViewControllerLifecycle.STATE_STARTED);

        registry.onControllerPause();

        verifyTracker();
        Mockito.verify(observer).onControllerPause();
        Mockito.verify(anotherObserver).onControllerStart();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testAddObserverWhileInStop() {
        addAndVerifyInitializedObserver();
        prepareAnotherObserverForAdditionWhen().onControllerStop();

        registry.onControllerStop();

        verifyTracker();
        Mockito.verify(observer).onControllerStop();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveObserverWhileInStart() {
        addAndVerifyInitializedObserver();
        prepareAndAddAnotherObserverThanRemoveWhen().onControllerStart();
        prepareTracker(ViewControllerLifecycle.STATE_STARTED);

        registry.onControllerStart();

        verifyTracker();
        Mockito.verify(observer).onControllerStart();
        Mockito.verify(anotherObserver).onControllerStart();
        Mockito.verify(anotherObserver).onControllerStop();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveObserverWhileInResume() {
        addAndVerifyInitializedObserver();
        prepareAndAddAnotherObserverThanRemoveWhen().onControllerResume();
        prepareTracker(ViewControllerLifecycle.STATE_RESUMED);

        registry.onControllerResume();

        verifyTracker();
        Mockito.verify(observer).onControllerResume();
        Mockito.verify(anotherObserver).onControllerResume();
        Mockito.verify(anotherObserver).onControllerPause();
        Mockito.verify(anotherObserver).onControllerStop();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveObserverWhileInFocus() {
        addAndVerifyInitializedObserver();
        prepareAndAddAnotherObserverThanRemoveWhen().onControllerFocus();
        prepareTracker(ViewControllerLifecycle.STATE_FOCUSED);

        registry.onControllerFocus();

        verifyTracker();
        Mockito.verify(observer).onControllerFocus();
        Mockito.verify(anotherObserver).onControllerFocus();
        Mockito.verify(anotherObserver).onControllerBlur();
        Mockito.verify(anotherObserver).onControllerPause();
        Mockito.verify(anotherObserver).onControllerStop();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveObserverWhileInBlur() {
        addAndVerifyInitializedObserver();
        prepareAndAddAnotherObserverThanRemoveWhen().onControllerBlur();
        prepareTracker(ViewControllerLifecycle.STATE_RESUMED);

        registry.onControllerBlur();

        verifyTracker();
        Mockito.verify(observer).onControllerBlur();
        Mockito.verify(anotherObserver).onControllerBlur();
        Mockito.verify(anotherObserver).onControllerPause();
        Mockito.verify(anotherObserver).onControllerStop();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveObserverWhileInPause() {
        addAndVerifyInitializedObserver();
        prepareAndAddAnotherObserverThanRemoveWhen().onControllerPause();
        prepareTracker(ViewControllerLifecycle.STATE_STARTED);

        registry.onControllerPause();

        verifyTracker();
        Mockito.verify(observer).onControllerPause();
        Mockito.verify(anotherObserver).onControllerPause();
        Mockito.verify(anotherObserver).onControllerStop();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    @Test
    public void testRemoveObserverWhileInStop() {
        addAndVerifyInitializedObserver();
        prepareAndAddAnotherObserverThanRemoveWhen().onControllerStop();

        registry.onControllerStop();

        verifyTracker();
        Mockito.verify(observer).onControllerStop();
        Mockito.verify(anotherObserver).onControllerStop();
        Mockito.verifyNoMoreInteractions(anotherObserver);
        ensureNoMoreInteractions();
    }

    private void addAndVerifyInitializedObserver() {
        prepareTracker(0);
        registry.addControllerLifecycleObserver(observer);
        verifyTracker();
        ensureNoMoreInteractions();
    }

    private void prepareTracker(int state) {
        Mockito.when(tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_STARTED))
                .thenReturn(state >= ViewControllerLifecycle.STATE_STARTED);

        Mockito.when(tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_RESUMED))
                .thenReturn(state >= ViewControllerLifecycle.STATE_RESUMED);

        Mockito.when(tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_FOCUSED))
                .thenReturn(state >= ViewControllerLifecycle.STATE_FOCUSED);
    }

    private void verifyTracker() {
        Mockito.verify(tracker).isControllerStateAtLeast(ViewControllerLifecycle.STATE_STARTED);
        Mockito.verify(tracker).isControllerStateAtLeast(ViewControllerLifecycle.STATE_RESUMED);
        Mockito.verify(tracker).isControllerStateAtLeast(ViewControllerLifecycle.STATE_FOCUSED);
    }

    private ViewControllerLifecycleObserver prepareAnotherObserverForAdditionWhen() {
        anotherObserver = Mockito.mock(ViewControllerLifecycleObserver.class);

        return Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                registry.addControllerLifecycleObserver(anotherObserver);
                return null;
            }
        }).when(observer);
    }

    private ViewControllerLifecycleObserver prepareAndAddAnotherObserverThanRemoveWhen() {
        anotherObserver = Mockito.mock(ViewControllerLifecycleObserver.class);
        registry.addControllerLifecycleObserver(anotherObserver);
        verifyTracker();
        ensureNoMoreInteractions();

        // do not verify how addControllerLifecycleObserver() works, because it was verified earlier.
        Mockito.reset(anotherObserver);

        return Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                registry.removeControllerLifecycleObserver(anotherObserver);
                return null;
            }
        }).when(observer);
    }

    private void ensureNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(tracker);
        Mockito.verifyNoMoreInteractions(observer);
        Mockito.reset(tracker);
        Mockito.reset(observer);
    }
}
