package com.eightsines.holycycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ViewControllerActivityDelegateTest {
    private static final String NAME_EXTRAS = "extras";
    private static final String NAME_SAVED_INSTANCE_STATE = "savedInstanceState";
    private static final String NAME_OUT_STATE = "outState";

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
    public void testOnCreateEverything() {
        performCreate();
    }

    @Test
    public void testOnCreateEverythingButNullExtras() {
        performCreate(false, true, true);
    }

    @Test
    public void testOnCreateEverythingButNullState() {
        performCreate(true, false, true);
    }

    @Test
    public void testOnCreateNoContentLayoutId() {
        performCreate(true, true, false);
    }

    @Test
    public void testOnCreateFinishInGetContentLayoutId() {
        Bundle extras = createMockBundle(NAME_EXTRAS);
        Intent intent = createMockIntent(extras);
        Bundle savedInstanceState = createMockBundle(NAME_SAVED_INSTANCE_STATE);

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
        Bundle extras = createMockBundle(NAME_EXTRAS);
        Intent intent = createMockIntent(extras);
        Bundle savedInstanceState = createMockBundle(NAME_SAVED_INSTANCE_STATE);

        Mockito.when(activity.getIntent()).thenReturn(intent);

        Mockito.doAnswer(new Answer() {
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
    public void testOnCreateFinishInControllerCreate() {
        Bundle extras = createMockBundle(NAME_EXTRAS);
        Intent intent = createMockIntent(extras);
        Bundle savedInstanceState = createMockBundle(NAME_SAVED_INSTANCE_STATE);

        Mockito.when(activity.getIntent()).thenReturn(intent);

        Mockito.doAnswer(new Answer() {
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
    public void testOnCreateDestroyed() {
        Bundle savedInstanceState = createMockBundle(NAME_SAVED_INSTANCE_STATE);

        controllerDelegate.finish();
        controllerDelegate.onCreate(savedInstanceState);

        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnCreateInvalidState() {
        performCreate();
        controllerDelegate.onCreate(null);
    }

    @Test
    public void testOnStart() {
        performStart();
    }

    @Test
    public void testOnStartDestroyed() {
        controllerDelegate.finish();
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
    }

    @Test
    public void testOnResumeHasFocus() {
        performResume(true);
    }

    @Test
    public void testOnResumeFinishInResume() {
        performStart();

        Mockito.doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return null;
            }
        }).when(controller).onControllerResume();

        controllerDelegate.onWindowFocusChanged(true);
        controllerDelegate.onResume();

        Mockito.verify(controller).onControllerResume();
        verifyPause(true);
        verifyStop();

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnResumeDestroyed() {
        controllerDelegate.finish();
        controllerDelegate.onResume();

        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnResumeInvalidState() {
        controllerDelegate.onResume();
    }

    @Test
    public void testOnPauseNoFocus() {
        performPauseAfterResume(false);
    }

    @Test
    public void testOnPauseHasFocus() {
        performPauseAfterResume(true);
    }

    @Test
    public void testOnPauseFinishInPause() {
        performResume(true);

        Mockito.doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return null;
            }
        }).when(controller).onControllerPause();

        controllerDelegate.onPause();

        verifyPause(true);
        verifyStop();

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnPauseFinishInBlur() {
        performResume(true);

        Mockito.doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                controllerDelegate.finish();
                return null;
            }
        }).when(controller).onControllerBlur();

        controllerDelegate.onPause();

        verifyPause(true);
        verifyStop();

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnPauseDestroyed() {
        controllerDelegate.finish();
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
        performStop();
    }

    @Test
    public void testOnStopAfterPause() {
        performPauseAfterResume(false);
        performStop();
    }

    @Test
    public void testOnStopDestroyed() {
        controllerDelegate.finish();
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
        performDestroy();
    }

    @Test
    public void testOnDestroyAfterStop() {
        performStart();
        performStop();
        performDestroy();
    }

    @Test
    public void testOnDestroyDestroyed() {
        controllerDelegate.finish();
        controllerDelegate.onDestroy();

        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnDestroyInvalidState() {
        controllerDelegate.onDestroy();
    }

    @Test
    public void testOnSaveInstanceStateResumed() {
        Bundle outState = createMockBundle(NAME_OUT_STATE);
        performResume(false);

        controllerDelegate.onSaveInstanceState(outState);

        verifyPause(false);
        verifyStop();
        Mockito.verify(controller).onControllerSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateStarted() {
        Bundle outState = createMockBundle(NAME_OUT_STATE);
        performStart();

        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerStop();
        Mockito.verify(controller).onControllerSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateCreated() {
        Bundle outState = createMockBundle(NAME_OUT_STATE);
        performCreate();

        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerSaveInstanceState(outState);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateInitialized() {
        Bundle outState = createMockBundle(NAME_OUT_STATE);

        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerSaveInstanceState(outState);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateDestroyed() {
        Bundle outState = createMockBundle(NAME_OUT_STATE);

        controllerDelegate.finish();
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

        verifyPause(false);
        verifyStop();

        ensureNoMoreInteractions();
    }

    @Test
    public void testFinishStarted() {
        performStart();

        controllerDelegate.finish();

        verifyStop();
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
    public void testFinishDestroyed() {
        controllerDelegate.finish();
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
        controllerDelegate.onDestroy();
        controllerDelegate.onCreate(null);
        ensureNoMoreInteractions();
    }

    private void performStop() {
        controllerDelegate.onStop();
        Mockito.verify(controller).onControllerStop();
        ensureNoMoreInteractions();
    }

    private void performPauseAfterResume(boolean hasFocus) {
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
        Bundle extras = hasExtras ? createMockBundle(NAME_EXTRAS) : null;
        Intent intent = createMockIntent(extras);
        Bundle savedInstanceState = hasSavedInstanceState ? createMockBundle(NAME_SAVED_INSTANCE_STATE) : null;

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
    }

    private Intent createMockIntent(@Nullable Bundle extras) {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.getExtras()).thenReturn(extras);
        return intent;
    }

    private Bundle createMockBundle(@NonNull String name) {
        Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.toString()).thenReturn("Bundle#" + name);
        return bundle;
    }
}
