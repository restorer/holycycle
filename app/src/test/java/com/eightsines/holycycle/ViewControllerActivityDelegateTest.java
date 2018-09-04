package com.eightsines.holycycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.eightsines.holycycle.util.TestUtils;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ViewControllerActivityDelegateTest {
    private Activity activity;
    private ViewController controller;
    private ViewControllerActivityDelegate controllerDelegate;

    @Before
    public void setUp() {
        activity = Mockito.mock(Activity.class);
        controller = Mockito.mock(ViewController.class);
        controllerDelegate = new ViewControllerActivityDelegate(activity, controller);
    }

    @Test
    public void testOnCreate() {
        performCreate();
        controllerDelegate.onStart();
    }

    @Test
    public void testOnCreateNullExtras() {
        performCreate(false, true, true);
        controllerDelegate.onStart();
    }

    @Test
    public void testOnCreateNullState() {
        performCreate(true, false, true);
        controllerDelegate.onStart();
    }

    @Test
    public void testOnCreateNoContentLayoutId() {
        performCreate(true, true, false);
        controllerDelegate.onStart();
    }

    @Test
    public void testOnCreateFinishInGetContentLayoutId() {
        Bundle extras = TestUtils.createMockBundle(TestUtils.BUNDLE_EXTRAS);
        Intent intent = TestUtils.createMockIntent(extras);
        Bundle savedInstanceState = TestUtils.createMockBundle(TestUtils.BUNDLE_SAVED_INSTANCE_STATE);

        Mockito.when(activity.getIntent()).thenReturn(intent);

        Mockito.when(controller.onControllerGetContentLayoutId()).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return 1;
            }
        });

        controllerDelegate.onCreate(savedInstanceState);

        Mockito.verify(activity).getIntent();
        Mockito.verify(controller).onControllerCreate(extras);
        Mockito.verify(controller).onControllerRestoreInstanceState(savedInstanceState);
        Mockito.verify(controller).onControllerGetContentLayoutId();

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnCreateFinishInRestoreInstanceState() {
        Bundle extras = TestUtils.createMockBundle(TestUtils.BUNDLE_EXTRAS);
        Intent intent = TestUtils.createMockIntent(extras);
        Bundle savedInstanceState = TestUtils.createMockBundle(TestUtils.BUNDLE_SAVED_INSTANCE_STATE);

        Mockito.when(activity.getIntent()).thenReturn(intent);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return null;
            }
        }).when(controller).onControllerRestoreInstanceState(savedInstanceState);

        controllerDelegate.onCreate(savedInstanceState);

        Mockito.verify(activity).getIntent();
        Mockito.verify(controller).onControllerCreate(extras);
        Mockito.verify(controller).onControllerRestoreInstanceState(savedInstanceState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnCreateFinishInCreate() {
        Bundle extras = TestUtils.createMockBundle(TestUtils.BUNDLE_EXTRAS);
        Intent intent = TestUtils.createMockIntent(extras);
        Bundle savedInstanceState = TestUtils.createMockBundle(TestUtils.BUNDLE_SAVED_INSTANCE_STATE);

        Mockito.when(activity.getIntent()).thenReturn(intent);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return null;
            }
        }).when(controller).onControllerCreate(extras);

        controllerDelegate.onCreate(savedInstanceState);

        Mockito.verify(activity).getIntent();
        Mockito.verify(controller).onControllerCreate(extras);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnCreateFinished() {
        Bundle savedInstanceState = TestUtils.createMockBundle(TestUtils.BUNDLE_SAVED_INSTANCE_STATE);

        controllerDelegate.finish();
        controllerDelegate.onCreate(savedInstanceState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnCreateDestroyed() {
        Bundle savedInstanceState = TestUtils.createMockBundle(TestUtils.BUNDLE_SAVED_INSTANCE_STATE);

        performDestroy();
        controllerDelegate.onCreate(savedInstanceState);

        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnCreateInvalidState() {
        performCreate();
        controllerDelegate.onCreate(null);
    }

    @Test
    public void testOnRestart() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);

        controllerDelegate.onSaveInstanceState(outState);
        controllerDelegate.onRestart();
        controllerDelegate.onStart();
    }

    @Test
    public void testOnDestroyed() {
        performDestroy();
        controllerDelegate.onRestart();
        controllerDelegate.onStart();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnStart() {
        performStart();
        controllerDelegate.onResume();
    }

    @Test
    public void testOnStartFinished() {
        controllerDelegate.finish();
        controllerDelegate.onStart();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnStartDestroyed() {
        performDestroy();
        controllerDelegate.onStart();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnStartInvalidState() {
        controllerDelegate.onStart();
    }

    @Test
    public void testOnResumeNoFocus() {
        performResume(false);
        controllerDelegate.onPause();
    }

    @Test
    public void testOnResumeHasFocus() {
        performResume(true);
        controllerDelegate.onPause();
    }

    @Test
    public void testOnResumeFinishInResume() {
        performStart();

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return null;
            }
        }).when(controller).onControllerResume();

        controllerDelegate.onWindowFocusChanged(true);
        controllerDelegate.onResume();

        Mockito.verify(controller).onControllerResume();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnResumeFinished() {
        controllerDelegate.finish();
        controllerDelegate.onResume();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnResumeDestroyed() {
        performDestroy();
        controllerDelegate.onResume();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnResumeInvalidState() {
        controllerDelegate.onResume();
    }

    @Test
    public void testOnPauseNoFocus() {
        performAndVerifyPauseAfterResume(false);
        controllerDelegate.onResume();
    }

    @Test
    public void testOnPauseHasFocus() {
        performAndVerifyPauseAfterResume(true);
        controllerDelegate.onResume();
    }

    @Test
    public void testOnPauseFinishInPause() {
        performResume(true);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return null;
            }
        }).when(controller).onControllerPause();

        controllerDelegate.onPause();

        verifyPause(true);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnPauseFinishInBlur() {
        performResume(true);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return null;
            }
        }).when(controller).onControllerBlur();

        controllerDelegate.onPause();

        verifyPause(true);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnPauseFinished() {
        controllerDelegate.finish();
        controllerDelegate.onPause();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnPauseAfterResumeFinished() {
        performResume(true);
        controllerDelegate.finish();
        controllerDelegate.onPause();
        verifyPause(true);
    }

    @Test
    public void testOnPauseInstanceStateSaved() {
        performSaveInstanceState();
        controllerDelegate.onPause();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnPauseDestroyed() {
        performDestroy();
        controllerDelegate.onPause();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnPauseInvalidState() {
        controllerDelegate.onPause();
    }

    @Test
    public void testOnStop() {
        performStart();
        performAndVerifyStop();
    }

    @Test
    public void testOnStopAfterPause() {
        performAndVerifyPauseAfterResume(false);
        performAndVerifyStop();
    }

    @Test
    public void testOnStopFinished() {
        controllerDelegate.finish();
        controllerDelegate.onStop();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnStopAfterStartFinished() {
        performStart();
        controllerDelegate.finish();
        controllerDelegate.onStop();
        verifyStop();
    }

    @Test
    public void testOnStopInstanceStateSaved() {
        performSaveInstanceState();
        controllerDelegate.onStop();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnStopDestroyed() {
        performDestroy();
        controllerDelegate.onStop();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnStopInvalidState() {
        controllerDelegate.onStop();
    }

    @Test
    public void testOnDestroy() {
        performCreate();
        controllerDelegate.onDestroy();
        verifyDestroy();
    }

    @Test
    public void testOnDestroyAfterStop() {
        performStart();
        performAndVerifyStop();
        controllerDelegate.onDestroy();
        verifyDestroy();
    }

    @Test
    public void testOnDestroyFinished() {
        controllerDelegate.finish();
        controllerDelegate.onDestroy();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDestroyInstanceStateSaved() {
        performSaveInstanceState();
        controllerDelegate.onDestroy();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDestroyDestroyed() {
        performDestroy();
        controllerDelegate.onDestroy();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnDestroyInvalidState() {
        controllerDelegate.onDestroy();
    }

    @Test
    public void testOnSaveInstanceStateResumed() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performResume(false);

        controllerDelegate.onSaveInstanceState(outState);

        verifyPause(false);
        verifyStop();
        Mockito.verify(controller).onControllerSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateStarted() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performStart();

        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerStop();
        Mockito.verify(controller).onControllerSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateCreated() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performCreate();

        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerSaveInstanceState(outState);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateInitialized() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);

        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerSaveInstanceState(outState);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateFinished() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);

        controllerDelegate.finish();
        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerSaveInstanceState(outState);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateInstanceStateSaved() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);

        controllerDelegate.onSaveInstanceState(outState);
        Mockito.reset(controller);

        controllerDelegate.onSaveInstanceState(outState);
        Mockito.verify(controller).onControllerSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateDestroyed() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);

        performDestroy();
        controllerDelegate.onSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangedNotResumedAndHasFocus() {
        controllerDelegate.onWindowFocusChanged(true);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangedNotResumedAndHasNoFocus() {
        controllerDelegate.onWindowFocusChanged(false);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangedResumedWithNoFocusAndHasFocus() {
        performResume(false);

        controllerDelegate.onWindowFocusChanged(true);

        Mockito.verify(controller).onControllerFocus();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangedResumedWithNoFocusAndHasNoFocus() {
        performResume(false);
        controllerDelegate.onWindowFocusChanged(false);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangedResumedWithFocusAndHasFocus() {
        performResume(true);
        controllerDelegate.onWindowFocusChanged(true);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangedResumedWithFocusAndHasNoFocus() {
        performResume(true);

        controllerDelegate.onWindowFocusChanged(false);

        Mockito.verify(controller).onControllerBlur();
        ensureNoMoreInteractions();
    }

    @Test
    public void testFinishResumed() {
        performResume(false);
        controllerDelegate.finish();
        ensureNoMoreInteractions();
    }

    @Test
    public void testFinishStarted() {
        performStart();
        controllerDelegate.finish();
        ensureNoMoreInteractions();
    }

    @Test
    public void testFinishCreated() {
        performCreate();
        controllerDelegate.finish();
        ensureNoMoreInteractions();
    }

    @Test
    public void testFinishInitialized() {
        controllerDelegate.finish();
        ensureNoMoreInteractions();
    }

    @Test
    public void testFinishFinished() {
        controllerDelegate.finish();
        controllerDelegate.finish();
        ensureNoMoreInteractions();
    }

    @Test
    public void testFinishDestroyed() {
        performDestroy();
        controllerDelegate.finish();
        ensureNoMoreInteractions();
    }

    @Test
    public void testGetViewHasContentLayout() {
        View view = Mockito.mock(View.class);
        Mockito.when(activity.findViewById(android.R.id.content)).thenReturn(view);

        performCreate();
        View resultView = controllerDelegate.getView();

        Mockito.verify(activity).findViewById(android.R.id.content);
        Assert.assertSame(view, resultView);

        ensureNoMoreInteractions();
    }

    @Test
    public void testGetViewHasNoContentLayout() {
        View view = Mockito.mock(View.class);
        Mockito.when(activity.findViewById(android.R.id.content)).thenReturn(view);

        performCreate(true, true, false);
        View resultView = controllerDelegate.getView();

        Assert.assertNull(resultView);
        ensureNoMoreInteractions();
    }

    private void performDestroy() {
        controllerDelegate.finish();
        controllerDelegate.onDestroy();
    }

    private void performSaveInstanceState() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        controllerDelegate.onSaveInstanceState(outState);
        Mockito.verify(controller).onControllerSaveInstanceState(outState);
    }

    private void verifyDestroy() {
        controllerDelegate.onCreate(null);
        ensureNoMoreInteractions();
    }

    private void performAndVerifyStop() {
        controllerDelegate.onStop();
        verifyStop();
        ensureNoMoreInteractions();
    }

    private void performAndVerifyPauseAfterResume(boolean hasFocus) {
        performResume(hasFocus);

        controllerDelegate.onPause();

        verifyPause(hasFocus);
        ensureNoMoreInteractions();
    }

    private void verifyStop() {
        Mockito.verify(controller).onControllerStop();
    }

    private void verifyPause(boolean hasFocus) {
        if (hasFocus) {
            Mockito.verify(controller).onControllerBlur();
        }

        Mockito.verify(controller).onControllerPause();
        Mockito.verify(controller).onControllerPersistUserData();
    }

    private void performResume(boolean hasFocus) {
        performStart();

        controllerDelegate.onWindowFocusChanged(hasFocus);
        controllerDelegate.onResume();

        Mockito.verify(controller).onControllerResume();

        if (hasFocus) {
            Mockito.verify(controller).onControllerFocus();
        }

        ensureNoMoreInteractions();
    }

    private void performStart() {
        performCreate();

        controllerDelegate.onStart();

        Mockito.verify(controller).onControllerStart();
        ensureNoMoreInteractions();
    }

    private void performCreate() {
        performCreate(true, true, true);
    }

    private void performCreate(boolean hasExtras, boolean hasSavedInstanceState, boolean hasContentLayout) {
        Bundle extras = hasExtras ? TestUtils.createMockBundle(TestUtils.BUNDLE_EXTRAS) : null;
        Intent intent = TestUtils.createMockIntent(extras);
        Bundle savedInstanceState = hasSavedInstanceState ? TestUtils.createMockBundle(TestUtils.BUNDLE_SAVED_INSTANCE_STATE) : null;

        Mockito.when(activity.getIntent()).thenReturn(intent);
        Mockito.when(controller.onControllerGetContentLayoutId()).thenReturn(hasContentLayout ? 1 : 0);

        controllerDelegate.onCreate(savedInstanceState);

        Mockito.verify(activity).getIntent();
        Mockito.verify(controller).onControllerCreate(extras);

        if (hasSavedInstanceState) {
            Mockito.verify(controller).onControllerRestoreInstanceState(savedInstanceState);
        }

        Mockito.verify(controller).onControllerGetContentLayoutId();

        if (hasContentLayout) {
            Mockito.verify(activity).setContentView(1);
            Mockito.verify(controller).onControllerContentViewCreated();
        }

        ensureNoMoreInteractions();
    }

    private void ensureNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(controller);
        Mockito.reset(controller);
    }
}
