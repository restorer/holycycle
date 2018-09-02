package com.eightsines.holycycle.util;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.mockito.Mockito;

public final class TestUtils {
    public static final String BUNDLE_EXTRAS = "extras";
    public static final String BUNDLE_SAVED_INSTANCE_STATE = "savedInstanceState";
    public static final String BUNDLE_OUT_STATE = "outState";

    private TestUtils() {
    }

    public static Bundle createMockBundle(@NonNull String name) {
        Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.toString()).thenReturn("Bundle#" + name);
        return bundle;
    }

    public static Intent createMockIntent(@Nullable Bundle extras) {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.getExtras()).thenReturn(extras);
        return intent;
    }

    public static void runWithMockedSdkInt(int newValue, Runnable runnable) {
        try {
            mockStaticField(Build.VERSION.class.getField("SDK_INT"), newValue);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        runnable.run();

        try {
            mockStaticField(Build.VERSION.class.getField("SDK_INT"), 0);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void mockStaticField(Field field, Object newValue) {
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
