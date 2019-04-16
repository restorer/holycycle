package com.eightsines.holycycle.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eightsines.holycycle.ViewController;
import com.eightsines.holycycle.ViewControllerFragmentDelegate;

/**
 * This class should be used instead of {@link android.support.v4.app.DialogFragment} (android.support.v4.app.DialogFragment).
 */
public abstract class ViewControllerDialogFragment extends DialogFragment implements ViewController {
    private ViewControllerFragmentDelegate controllerDelegate;

    public ViewControllerDialogFragment() {
        super();
        controllerDelegate = new ViewControllerFragmentDelegate(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        controllerDelegate.onAttach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controllerDelegate.onCreate(savedInstanceState, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return controllerDelegate.onCreateView(inflater, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controllerDelegate.onViewCreated();
    }

    @Override
    public void onStart() {
        super.onStart();
        controllerDelegate.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        controllerDelegate.onResume();
    }

    @Override
    public void onPause() {
        controllerDelegate.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        controllerDelegate.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        controllerDelegate.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        controllerDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        controllerDelegate.onDetach();
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        controllerDelegate.onSaveInstanceState(outState);
    }

    @Override
    public void onControllerCreate(@Nullable Bundle extras) {
    }

    @Override
    public void onControllerRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    }

    @Override
    public int onControllerGetContentLayoutId() {
        return 0;
    }

    @Override
    public void onControllerContentViewCreated() {
    }

    @Override
    public void onControllerStart() {
    }

    @Override
    public void onControllerResume() {
    }

    @Override
    public void onControllerFocus() {
    }

    @Override
    public void onControllerBlur() {
    }

    @Override
    public void onControllerPause() {
    }

    @Override
    public void onControllerPersistUserData() {
    }

    @Override
    public void onControllerStop() {
    }

    @Override
    public void onControllerSaveInstanceState(@NonNull Bundle outState) {
    }

    @Nullable
    @Override
    public <T extends View> T findViewById(int id) {
        return controllerDelegate.findViewById(id);
    }
}
