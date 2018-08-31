package com.eightsines.holycycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

// http://d.android.com/tools/testing

public class ViewControllerActivityDelegateTest {
    private static final String NAME_EXTRAS = "extras";
    private static final String NAME_SAVED_INSTANCE_STATE = "savedInstanceState";

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

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
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

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
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

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test
    public void testOnCreateDestroyed() {
        Bundle savedInstanceState = createMockBundle(NAME_SAVED_INSTANCE_STATE);

        controllerDelegate.finish();
        controllerDelegate.onCreate(savedInstanceState);

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
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

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
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
        Mockito.verify(controller).onControllerBlur();
        Mockito.verify(controller).onControllerPause();
        Mockito.verify(controller).onControllerPersistUserData();
        Mockito.verify(controller).onControllerStop();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test
    public void testOnResumeDestroyed() {
        controllerDelegate.finish();
        controllerDelegate.onResume();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnResumeInvalidState() {
        controllerDelegate.onResume();
    }

    @Test
    public void testOnPauseNoFocus() {
        performPause(false);
    }

    @Test
    public void testOnPauseHasFocus() {
        performPause(true);
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

        Mockito.verify(controller).onControllerBlur();
        Mockito.verify(controller).onControllerPause();
        Mockito.verify(controller).onControllerPersistUserData();
        Mockito.verify(controller).onControllerStop();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
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

        Mockito.verify(controller).onControllerBlur();
        Mockito.verify(controller).onControllerPause();
        Mockito.verify(controller).onControllerPersistUserData();
        Mockito.verify(controller).onControllerStop();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test
    public void testOnPauseDestroyed() {
        controllerDelegate.finish();
        controllerDelegate.onPause();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnPauseInvalidState() {
        controllerDelegate.onPause();
    }

    @Test
    public void testOnStop() {
        performStart();

        controllerDelegate.onStop();

        Mockito.verify(controller).onControllerStop();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test
    public void testOnStopDestroyed() {
        controllerDelegate.finish();
        controllerDelegate.onStop();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnStopInvalidState() {
        controllerDelegate.onStop();
    }

    @Test
    public void testOnDestroy() {
        performCreate();

        controllerDelegate.onDestroy();
        controllerDelegate.onCreate(null);

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test
    public void testOnDestroyDestroyed() {
        controllerDelegate.finish();
        controllerDelegate.onDestroy();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnDestroyInvalidState() {
        controllerDelegate.onDestroy();
    }

    // testOnSaveInstanceStateResumed()
    // testOnSaveInstanceStateStarted()
    // testOnSaveInstanceStateCreated()
    // testOnSaveInstanceStateInitialized()
    // testOnSaveInstanceStateDestroyed()

    // testOnWindowFocusChangedNotResumedAndHasFocus()
    // testOnWindowFocusChangedNotResumedAndHasNoFocus()
    // testOnWindowFocusChangedResumedAndHasFocus()
    // testOnWindowFocusChangedResumedAndHasNoFocus()

    // testFinishResumed()
    // testFinishStarted()
    // testFinishCreated()
    // testFinishInitialized()
    // testFinishDestroyed()

    // testGetViewHasContentLayout()
    // testGetViewHasNoContentLayout()

    private void performPause(boolean hasFocus) {
        performResume(hasFocus);

        controllerDelegate.onPause();

        if (hasFocus) {
            Mockito.verify(controller).onControllerBlur();
        }

        Mockito.verify(controller).onControllerPause();
        Mockito.verify(controller).onControllerPersistUserData();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    private void performResume(boolean hasFocus) {
        performStart();

        controllerDelegate.onWindowFocusChanged(hasFocus);
        controllerDelegate.onResume();

        Mockito.verify(controller).onControllerResume();

        if (hasFocus) {
            Mockito.verify(controller).onControllerFocus();
        }

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
    }

    private void performStart() {
        performCreate();

        controllerDelegate.onStart();

        Mockito.verify(controller).onControllerStart();

        Mockito.verifyNoMoreInteractions(activity);
        Mockito.verifyNoMoreInteractions(controller);
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

        Mockito.verifyNoMoreInteractions(activity);
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
