package com.eightsines.holycycle;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.eightsines.holycycle.util.TestUtils;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ViewControllerFragmentDelegateTest {
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
        performAndVerifyAttach();
        controllerDelegate.onCreate(null, null);
    }

    @Test
    public void testOnAttachDestroyed() {
        performAndVerifyDestroyAfterCreate();
        controllerDelegate.onAttach();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnAttachTwice() {
        performAndVerifyAttach();
        performAndVerifyAttach();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnAttachInvalidState() {
        performAndVerifyCreate();
        controllerDelegate.onAttach();
    }

    @Test
    public void testOnCreate() {
        performAndVerifyCreate();
        controllerDelegate.onDestroy();
    }

    @Test
    public void testOnCreateNullState() {
        performAndVerifyCreate(false, true);
        controllerDelegate.onDestroy();
    }

    @Test
    public void testOnCreateNullExtras() {
        performAndVerifyCreate(true, false);
        controllerDelegate.onDestroy();
    }

    @Test
    public void testOnCreateDestroyed() {
        performAndVerifyDestroyAfterCreate();
        controllerDelegate.onCreate(null, null);
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnCreateInvalidState() {
        controllerDelegate.onCreate(null, null);
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
        performAndVerifyDestroyAfterCreate();

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
        performAndVerifyViewCreatedAfterCreateView();
    }

    @Test
    public void testOnViewCreatedDestroyed() {
        performAndVerifyDestroyAfterCreate();
        controllerDelegate.onViewCreated();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnViewCreatedInvalidState() {
        controllerDelegate.onViewCreated();
    }

    @Test
    public void testOnStart() {
        performAndVerifyStartAfterViewCreated();
        controllerDelegate.onResume();
    }

    @Test
    public void testOnStartViewCreated() {
        performAndVerifyViewCreatedAfterCreateView();
        controllerDelegate.onStart();
        verifyStart();
        controllerDelegate.onResume();
    }

    @Test
    public void testOnStartAfterStop() {
        performAndVerifyStartAfterViewCreated();
        controllerDelegate.onStop();
        verifyStop();
        controllerDelegate.onStart();
        verifyStart();
        controllerDelegate.onResume();
    }

    @Test
    public void testOnStartDestroyed() {
        performAndVerifyDestroyAfterCreate();
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
    public void testOnResumeDestroyed() {
        performAndVerifyDestroyAfterCreate();
        controllerDelegate.onResume();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnResumeInstanceStateSaved() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performResume(false);

        controllerDelegate.onSaveInstanceState(outState);

        verifyPause(false);
        verifyStop(false);
        Mockito.verify(controller).onControllerSaveInstanceState(outState);
        ensureNoMoreInteractions();

        controllerDelegate.onResume();

        Mockito.verify(controller).onControllerStart();
        Mockito.verify(controller).onControllerResume();
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
    public void testOnPauseDestroyed() {
        performAndVerifyDestroyAfterCreate();
        controllerDelegate.onPause();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnPauseInvalidState() {
        controllerDelegate.onPause();
    }

    @Test
    public void testOnStop() {
        performAndVerifyStartAfterViewCreated();
        performAndVerifyStop();
        controllerDelegate.onStart();
    }

    @Test
    public void testOnStopAfterPause() {
        performAndVerifyPauseAfterResume(false);
        performAndVerifyStop();
        controllerDelegate.onStart();
    }

    @Test
    public void testOnStopDestroyed() {
        performAndVerifyDestroyAfterCreate();
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

        TestUtils.runWithMockedBuildVersion(Build.VERSION_CODES.JELLY_BEAN_MR2, new Runnable() {
            @Override
            public void run() {
                controllerDelegate.onDestroyView();
                ensureNoMoreInteractions();
            }
        });
    }

    @Test
    public void testOnDestroyViewHasContentLayout() {
        performCreateView();

        assert controllerDelegate.getView() != null;
        final ViewTreeObserver viewTreeObserver = controllerDelegate.getView().getViewTreeObserver();

        TestUtils.runWithMockedBuildVersion(Build.VERSION_CODES.JELLY_BEAN_MR2, new Runnable() {
            @Override
            public void run() {
                controllerDelegate.onDestroyView();

                Mockito.verify(viewTreeObserver).removeOnWindowFocusChangeListener(lastWindowFocusChangeListener);
                Assert.assertNull(controllerDelegate.getView());

                ensureNoMoreInteractions();
            }
        });
    }

    @Test
    public void testOnDestroyViewHasContentLayoutOldSdk() {
        performCreateView();

        assert controllerDelegate.getView() != null;
        ViewTreeObserver viewTreeObserver = controllerDelegate.getView().getViewTreeObserver();

        controllerDelegate.onDestroyView();

        Mockito.verifyNoMoreInteractions(viewTreeObserver);
        Assert.assertNull(controllerDelegate.getView());

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDestroyViewAfterStop() {
        performAndVerifyStartAfterViewCreated();
        performAndVerifyStop();

        assert controllerDelegate.getView() != null;
        final ViewTreeObserver viewTreeObserver = controllerDelegate.getView().getViewTreeObserver();

        TestUtils.runWithMockedBuildVersion(Build.VERSION_CODES.JELLY_BEAN_MR2, new Runnable() {
            @Override
            public void run() {
                controllerDelegate.onDestroyView();

                Mockito.verify(viewTreeObserver).removeOnWindowFocusChangeListener(lastWindowFocusChangeListener);
                Assert.assertNull(controllerDelegate.getView());

                ensureNoMoreInteractions();
            }
        });
    }

    @Test
    public void testOnDestroyViewDestroyed() {
        performAndVerifyDestroyAfterCreate();
        controllerDelegate.onDestroyView();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnDestroyViewInvalidState() {
        controllerDelegate.onDestroyView();
    }

    @Test
    public void testOnDestroy() {
        performAndVerifyDestroyAfterCreate();
    }

    @Test
    public void testOnDestroyAfterDestroyView() {
        performCreateViewNoContentLayout();

        TestUtils.runWithMockedBuildVersion(Build.VERSION_CODES.JELLY_BEAN_MR2, new Runnable() {
            @Override
            public void run() {
                controllerDelegate.onDestroyView();
                controllerDelegate.onDestroy();

                ensureNoMoreInteractions();
            }
        });
    }

    @Test
    public void testOnDestroyDestroyed() {
        performAndVerifyDestroyAfterCreate();
        controllerDelegate.onDestroy();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnDestroyInvalidState() {
        controllerDelegate.onDestroy();
    }

    @Test
    public void testOnDetach() {
        performAndVerifyCreate();
        controllerDelegate.onDetach();
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnDetachAfterDestroy() {
        performAndVerifyDestroyAfterCreate();
        controllerDelegate.onDetach();
        ensureNoMoreInteractions();
    }

    @Test(expected = IllegalStateException.class)
    public void testOnDetachInvalidState() {
        controllerDelegate.onDetach();
    }

    @Test
    public void testOnSaveInstanceStateResumed() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performResume(false);

        controllerDelegate.onSaveInstanceState(outState);

        verifyPause(false);
        verifyStop(false);
        Mockito.verify(controller).onControllerSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateStarted() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performAndVerifyStartAfterViewCreated();

        controllerDelegate.onSaveInstanceState(outState);

        verifyStop(false);
        Mockito.verify(controller).onControllerSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateViewCreated() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performCreateViewNoContentLayout();

        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerSaveInstanceState(outState);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateCreated() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performAndVerifyCreate();

        controllerDelegate.onSaveInstanceState(outState);

        Mockito.verify(controller).onControllerSaveInstanceState(outState);
        ensureNoMoreInteractions();
    }

    @Test
    public void testOnSaveInstanceStateAttached() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performAndVerifyAttach();

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
    public void testOnSaveInstanceStateDestroyed() {
        Bundle outState = TestUtils.createMockBundle(TestUtils.BUNDLE_OUT_STATE);
        performAndVerifyDestroyAfterCreate();

        controllerDelegate.onSaveInstanceState(outState);

        ensureNoMoreInteractions();
    }

    @Test
    public void testOnWindowFocusChangeListenerNotResumedAndHasFocus() {
        performAndVerifyStartAfterViewCreated();
        lastWindowFocusChangeListener.onWindowFocusChanged(true);
        controllerDelegate.onResume();
        verifyResume(true);
    }

    @Test
    public void testOnWindowFocusChangeListenerNotResumedAndHasNoFocus() {
        performAndVerifyStartAfterViewCreated();
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

        assert controllerDelegate.getView() != null;
        Mockito.when(controllerDelegate.getView().findViewById(1)).thenReturn(view);

        Assert.assertSame(view, controllerDelegate.findViewById(1));
    }

    @Test
    public void testFindViewByIdNoContentView() {
        performCreateViewNoContentLayout();
        Assert.assertNull(controllerDelegate.findViewById(1));
    }

    private void performAndVerifyDestroyAfterCreate() {
        performAndVerifyCreate(false, false);
        controllerDelegate.onDestroy();
        ensureNoMoreInteractions();
    }

    private void performAndVerifyStop() {
        controllerDelegate.onStop();
        verifyStop();
    }

    private void verifyStop() {
        verifyStop(true);
    }

    private void verifyStop(boolean shouldEnsureNoMoreInteractions) {
        Mockito.verify(controller).onControllerStop();

        if (shouldEnsureNoMoreInteractions) {
            ensureNoMoreInteractions();
        }
    }

    private void performAndVerifyPauseAfterResume(boolean hasFocus) {
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
        performAndVerifyStartAfterViewCreated();

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

    private void performAndVerifyStartAfterViewCreated() {
        performAndVerifyViewCreatedAfterCreateView();
        controllerDelegate.onStart();
        verifyStart();
    }

    private void verifyStart() {
        Mockito.verify(controller).onControllerStart();
        ensureNoMoreInteractions();
    }

    private void performAndVerifyViewCreatedAfterCreateView() {
        performCreateView();
        controllerDelegate.onViewCreated();
        Mockito.verify(controller).onControllerContentViewCreated();
        ensureNoMoreInteractions();
    }

    private void checkFocusAfterCreateView(boolean shouldHasWindowFocus) {
        controllerDelegate.onViewCreated();
        Mockito.verify(controller).onControllerContentViewCreated();

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

    private void performCreateView(final boolean hasContentLayout,
            final boolean hasPreviousContentView,
            final boolean isPlatformFragment,
            final int sdkInt,
            final boolean shouldPerformCreate) {

        if (shouldPerformCreate) {
            performAndVerifyCreate();
        }

        TestUtils.runWithMockedBuildVersion(sdkInt, new Runnable() {
            @Override
            public void run() {
                ViewTreeObserver.OnWindowFocusChangeListener currentWindowFocusChangeListener = lastWindowFocusChangeListener;
                lastWindowFocusChangeListener = null;

                ViewGroup container = Mockito.mock(ViewGroup.class);
                LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
                View contentView = hasPreviousContentView ? controllerDelegate.getView() : Mockito.mock(View.class);

                assert contentView != null;

                ViewTreeObserver viewTreeObserver = hasPreviousContentView
                        ? contentView.getViewTreeObserver()
                        : Mockito.mock(ViewTreeObserver.class);

                if (hasPreviousContentView) {
                    Mockito.reset(contentView);
                    Mockito.reset(viewTreeObserver);
                }

                if (sdkInt >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Mockito.doAnswer(new Answer<Void>() {
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

                if (isPlatformFragment) {
                    controllerDelegate.onCreateView(inflater, container, true);
                } else {
                    controllerDelegate.onCreateView(inflater, container);
                }

                int getViewTreeObserverInvocationTimes = 0;

                if (hasPreviousContentView && sdkInt >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    getViewTreeObserverInvocationTimes++;

                    Mockito.verify(viewTreeObserver)
                            .removeOnWindowFocusChangeListener(currentWindowFocusChangeListener);
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
                    Mockito.verify(contentView, Mockito.times(getViewTreeObserverInvocationTimes))
                            .getViewTreeObserver();
                }

                ensureNoMoreInteractions();
            }
        });
    }

    private void performAndVerifyCreate() {
        performAndVerifyCreate(true, true);
    }

    private void performAndVerifyCreate(boolean hasSavedInstanceState, boolean hasExtras) {
        Bundle savedInstanceState = hasSavedInstanceState
                ? TestUtils.createMockBundle(TestUtils.BUNDLE_SAVED_INSTANCE_STATE)
                : null;

        Bundle extras = hasExtras ? TestUtils.createMockBundle(TestUtils.BUNDLE_EXTRAS) : null;

        performAndVerifyAttach();
        controllerDelegate.onCreate(savedInstanceState, extras);

        Mockito.verify(controller).onControllerCreate(extras);

        if (hasSavedInstanceState) {
            Mockito.verify(controller).onControllerRestoreInstanceState(savedInstanceState);
        }

        ensureNoMoreInteractions();
    }

    private void performAndVerifyAttach() {
        controllerDelegate.onAttach();
        ensureNoMoreInteractions();
    }

    private void ensureNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(controller);
        Mockito.reset(controller);
    }
}
