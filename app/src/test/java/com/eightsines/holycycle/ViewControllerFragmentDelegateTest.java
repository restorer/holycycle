package com.eightsines.holycycle;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ViewControllerFragmentDelegateTest {
    private static final String NAME_SAVED_INSTANCE_STATE = "savedInstanceState";
    private static final String NAME_EXTRAS = "extras";
    private static final String NAME_OUT_STATE = "outState";

    private ViewController controller;
    private ViewControllerFragmentDelegate controllerDelegate;
    private ViewTreeObserver.OnWindowFocusChangeListener lastWindowFocusChangeListener;

    @Before
    public void setUp() {
        controller = Mockito.mock(ViewController.class);
        controllerDelegate = new ViewControllerFragmentDelegate(controller);
    }

    @Test
    public void testOnAttach() {
        performAttach();
    }

    @Test
    public void testOnAttachDestroyed() {
        performDestroy();
        controllerDelegate.onAttach();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnAttachInvalidState() {
        performAttach();
        controllerDelegate.onAttach();
    }

    @Test
    public void testOnCreateEverything() {
        performCreate();
    }

    @Test
    public void testOnCreateEverythingButNullState() {
        performCreate(false, true);
    }

    @Test
    public void testOnCreateEverythingButNullExtras() {
        performCreate(true, false);
    }

    @Test
    public void testSupportOnCreateView() {
        performCreateView();
        checkFocusAfterCreateView(false);
    }

    @Test
    public void testSupportOnCreateViewOldSdk() {
        performCreateView(true, false, false, 0, true);
        checkFocusAfterCreateView(true);
    }

    @Test
    public void testSupportOnCreateViewNoContentLayout() {
        performCreateViewNoContentLayout();
        checkFocusAfterCreateView(true);
    }

    @Test
    public void testSupportOnCreateViewHasPreviousContentView() {
        performCreateView();

        controllerDelegate.onStart();
        controllerDelegate.onStop();

        Mockito.reset(controller);
        performCreateView(true, true, false, Build.VERSION_CODES.JELLY_BEAN_MR2, false);
    }

    @Test
    public void testPlatformOnCreateView() {
        performCreateView(true, false, true, Build.VERSION_CODES.JELLY_BEAN_MR2, true);
        checkFocusAfterCreateView(false);
    }

    @Test
    public void testPlatformOnCreateViewOldSdk() {
        performCreateView(true, false, true, 0, true);
        checkFocusAfterCreateView(true);
    }

    @Test
    public void testPlatformOnCreateViewNoContentLayout() {
        performCreateView(false, false, true, Build.VERSION_CODES.JELLY_BEAN_MR2, true);
        checkFocusAfterCreateView(true);
    }

    @Test
    public void testOnCreateViewDestroyed() {
        performDestroy();

        ViewGroup container = Mockito.mock(ViewGroup.class);
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);

        controllerDelegate.onCreateView(inflater, container);

        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnCreateViewInvalidState() {
        performCreateView(true, false, false, Build.VERSION_CODES.JELLY_BEAN_MR2, false);
    }

    @Test
    public void testOnViewCreated() {
        performViewCreated();
    }

    @Test
    public void testOnViewCreatedDestroyed() {
        performDestroy();
        controllerDelegate.onViewCreated();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnViewCreatedInvalidState() {
        controllerDelegate.onViewCreated();
    }

    @Test
    public void testOnStart() {
        performStart();
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
    }

    @Test
    public void testOnResumeHasFocus() {
        performResume(true);
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
        performPauseAfterResume(false);
    }

    @Test
    public void testOnPauseHasFocus() {
        performPauseAfterResume(true);
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
        performStop();
    }

    @Test
    public void testOnStopAfterPause() {
        performPauseAfterResume(false);
        performStop();
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
    public void testOnDestroyViewNoContentLayout() {
        performCreateViewNoContentLayout();
        mockSdkInt(Build.VERSION_CODES.JELLY_BEAN_MR2);
        controllerDelegate.onDestroyView();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDestroyViewHasContentLayout() {
        performCreateView();
        ViewTreeObserver viewTreeObserver = controllerDelegate.getView().getViewTreeObserver();

        mockSdkInt(Build.VERSION_CODES.JELLY_BEAN_MR2);
        controllerDelegate.onDestroyView();

        Mockito.verify(viewTreeObserver).removeOnWindowFocusChangeListener(lastWindowFocusChangeListener);
        Assert.assertNull(controllerDelegate.getView());

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDestroyViewHasContentLayoutOldSdk() {
        performCreateView();
        ViewTreeObserver viewTreeObserver = controllerDelegate.getView().getViewTreeObserver();

        mockSdkInt(0);
        controllerDelegate.onDestroyView();

        Mockito.verifyNoMoreInteractions(viewTreeObserver);
        Assert.assertNull(controllerDelegate.getView());

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDestroyViewAfterStop() {
        performStart();
        performStop();

        ViewTreeObserver viewTreeObserver = controllerDelegate.getView().getViewTreeObserver();

        mockSdkInt(Build.VERSION_CODES.JELLY_BEAN_MR2);
        controllerDelegate.onDestroyView();

        Mockito.verify(viewTreeObserver).removeOnWindowFocusChangeListener(lastWindowFocusChangeListener);
        Assert.assertNull(controllerDelegate.getView());

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDestroyViewDestroyed() {
        performDestroy();
        controllerDelegate.onDestroyView();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnDestroyViewInvalidState() {
        controllerDelegate.onDestroyView();
    }

    @Test
    public void testOnDestroy() {
        performDestroy();
    }

    @Test
    public void testOnDestroyAfterDestroyView() {
        performCreateViewNoContentLayout();

        mockSdkInt(Build.VERSION_CODES.JELLY_BEAN_MR2);
        controllerDelegate.onDestroyView();
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
    public void testOnDetach() {
        performCreate();
        controllerDelegate.onDetach();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDetachAfterDestroy() {
        performDestroy();
        controllerDelegate.onDetach();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnDetachInvalidState() {
        controllerDelegate.onDetach();
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

        verifyStop();
        Mockito.verify(controller).onControllerSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateViewCreated() {
        Bundle outState = createMockBundle(NAME_OUT_STATE);
        performCreateViewNoContentLayout();

        controllerDelegate.onSaveInstanceState(outState);

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
    public void testOnSaveInstanceStateAttached() {
        Bundle outState = createMockBundle(NAME_OUT_STATE);
        performAttach();

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
        performDestroy();

        controllerDelegate.onSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangeListenerNotResumedAndHasFocus() {
        performStart();
        lastWindowFocusChangeListener.onWindowFocusChanged(true);
        controllerDelegate.onResume();
        verifyResume(true);
    }

    @Test
    public void testOnWindowFocusChangeListenerNotResumedAndHasNoFocus() {
        performStart();
        lastWindowFocusChangeListener.onWindowFocusChanged(false);
        controllerDelegate.onResume();
        verifyResume(false);
    }

    @Test
    public void testOnWindowFocusChangeListenerResumedWithNoFocusAndHasFocus() {
        performResume(false);

        lastWindowFocusChangeListener.onWindowFocusChanged(true);

        Mockito.verify(controller).onControllerFocus();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangeListenerResumedWithNoFocusAndHasNoFocus() {
        performResume(false);
        lastWindowFocusChangeListener.onWindowFocusChanged(false);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangeListenerResumedWithFocusAndHasFocus() {
        performResume(true);
        lastWindowFocusChangeListener.onWindowFocusChanged(true);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangeListenerResumedWithFocusAndHasNoFocus() {
        performResume(true);

        lastWindowFocusChangeListener.onWindowFocusChanged(false);

        Mockito.verify(controller).onControllerBlur();
        ensureNoMoreInteractions();
    }

    @Test
    public void testGetViewHasContentView() {
        performCreateView();
        Assert.assertNotNull(controllerDelegate.getView());
    }

    @Test
    public void testGetViewNoContentView() {
        performCreateViewNoContentLayout();
        Assert.assertNull(controllerDelegate.getView());
    }

    @Test
    public void testFindViewByIdHasContentView() {
        performCreateView();

        View view = Mockito.mock(View.class);
        Mockito.when(controllerDelegate.getView().findViewById(1)).thenReturn(view);

        Assert.assertSame(view, controllerDelegate.findViewById(1));
    }

    @Test
    public void testFindViewByIdNoContentView() {
        performCreateViewNoContentLayout();
        Assert.assertNull(controllerDelegate.findViewById(1));
    }

    private void performDestroy() {
        performCreate(false, false);
        controllerDelegate.onDestroy();
        ensureNoMoreInteractions();
    }

    private void performStop() {
        controllerDelegate.onStop();
        verifyStop();
        ensureNoMoreInteractions();
    }

    private void verifyStop() {
        Mockito.verify(controller).onControllerStop();
    }

    private void performPauseAfterResume(boolean hasFocus) {
        performResume(hasFocus);
        controllerDelegate.onPause();
        verifyPause(hasFocus);
        ensureNoMoreInteractions();
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

        lastWindowFocusChangeListener.onWindowFocusChanged(hasFocus);
        controllerDelegate.onResume();

        verifyResume(hasFocus);
    }

    private void verifyResume(boolean hasFocus) {
        Mockito.verify(controller).onControllerResume();

        if (hasFocus) {
            Mockito.verify(controller).onControllerFocus();
        }

        ensureNoMoreInteractions();
    }

    private void performStart() {
        performViewCreated();
        controllerDelegate.onStart();
        Mockito.verify(controller).onControllerStart();
        ensureNoMoreInteractions();
    }

    private void performViewCreated() {
        performCreateView();
        controllerDelegate.onViewCreated();
        Mockito.verify(controller).onControllerContentViewCreated();
        ensureNoMoreInteractions();
    }

    private void checkFocusAfterCreateView(boolean shouldHasWindowFocus) {
        controllerDelegate.onStart();
        Mockito.verify(controller).onControllerStart();
        ensureNoMoreInteractions();

        controllerDelegate.onResume();
        Mockito.verify(controller).onControllerResume();

        if (shouldHasWindowFocus) {
            Mockito.verify(controller).onControllerFocus();
        }

        ensureNoMoreInteractions();
    }

    private void performCreateView() {
        performCreateView(true, false, false, Build.VERSION_CODES.JELLY_BEAN_MR2, true);
    }

    private void performCreateViewNoContentLayout() {
        performCreateView(false, false, false, Build.VERSION_CODES.JELLY_BEAN_MR2, true);
    }

    private void performCreateView(boolean hasContentLayout,
            boolean hasPreviousContentView,
            boolean isPlatformFragment,
            int sdkInt,
            boolean shouldPerformCreate) {

        mockSdkInt(sdkInt);

        ViewTreeObserver.OnWindowFocusChangeListener currentWindowFocusChangeListener = lastWindowFocusChangeListener;
        lastWindowFocusChangeListener = null;

        ViewGroup container = Mockito.mock(ViewGroup.class);
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
        View contentView = hasPreviousContentView ? controllerDelegate.getView() : Mockito.mock(View.class);

        ViewTreeObserver viewTreeObserver = hasPreviousContentView
                ? contentView.getViewTreeObserver()
                : Mockito.mock(ViewTreeObserver.class);

        if (hasPreviousContentView) {
            Mockito.reset(contentView);
            Mockito.reset(viewTreeObserver);
        }

        if (sdkInt >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Mockito.doAnswer(new Answer() {
                @Override
                public Void answer(InvocationOnMock invocation) {
                    lastWindowFocusChangeListener = invocation.getArgument(0);
                    return null;
                }
            })
                    .when(viewTreeObserver)
                    .addOnWindowFocusChangeListener(Mockito.any(ViewTreeObserver.OnWindowFocusChangeListener.class));

            Mockito.when(contentView.getViewTreeObserver()).thenReturn(viewTreeObserver);
        }

        Mockito.when(inflater.inflate(1, container, false)).thenReturn(contentView);
        Mockito.when(controller.onControllerGetContentLayoutId()).thenReturn(hasContentLayout ? 1 : 0);

        if (shouldPerformCreate) {
            performCreate();
        }

        if (isPlatformFragment) {
            controllerDelegate.onCreateView(inflater, container, true);
        } else {
            controllerDelegate.onCreateView(inflater, container);
        }

        int getViewTreeObserverInvocationTimes = 0;

        if (hasPreviousContentView && sdkInt >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getViewTreeObserverInvocationTimes++;
            Mockito.verify(viewTreeObserver).removeOnWindowFocusChangeListener(currentWindowFocusChangeListener);
        }

        Mockito.verify(controller).onControllerGetContentLayoutId();

        if (hasContentLayout) {
            Mockito.verify(inflater).inflate(1, container, false);
        }

        if (isPlatformFragment && sdkInt < Build.VERSION_CODES.HONEYCOMB_MR2) {
            Mockito.verify(controller).onControllerContentViewCreated();
        }

        if (hasContentLayout && sdkInt >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getViewTreeObserverInvocationTimes++;

            Mockito.verify(viewTreeObserver)
                    .addOnWindowFocusChangeListener(Mockito.any(ViewTreeObserver.OnWindowFocusChangeListener.class));

            Assert.assertNotNull(lastWindowFocusChangeListener);
        } else {
            Assert.assertNull(lastWindowFocusChangeListener);
        }

        if (getViewTreeObserverInvocationTimes > 0) {
            Mockito.verify(contentView, Mockito.times(getViewTreeObserverInvocationTimes)).getViewTreeObserver();
        }

        ensureNoMoreInteractions();
    }

    private void performCreate() {
        performCreate(true, true);
    }

    private void performCreate(boolean hasSavedInstanceState, boolean hasExtras) {
        Bundle savedInstanceState = hasSavedInstanceState ? createMockBundle(NAME_SAVED_INSTANCE_STATE) : null;
        Bundle extras = hasExtras ? createMockBundle(NAME_EXTRAS) : null;

        performAttach();
        controllerDelegate.onCreate(savedInstanceState, extras);

        Mockito.verify(controller).onControllerCreate(extras);

        if (hasSavedInstanceState) {
            Mockito.verify(controller).onControllerRestoreInstanceState(savedInstanceState);
        }

        ensureNoMoreInteractions();
    }

    private void performAttach() {
        controllerDelegate.onAttach();
        ensureNoMoreInteractions();
    }

    private void ensureNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(controller);
    }

    private Bundle createMockBundle(@NonNull String name) {
        Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.toString()).thenReturn("Bundle#" + name);
        return bundle;
    }

    private static void mockSdkInt(int newValue) {
        try {
            mockStaticField(Build.VERSION.class.getField("SDK_INT"), newValue);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static void mockStaticField(Field field, Object newValue) {
        field.setAccessible(true);

        try {
            //noinspection JavaReflectionMemberAccess
            Field modifiersField = Field.class.getDeclaredField("modifiers");

            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, newValue);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
