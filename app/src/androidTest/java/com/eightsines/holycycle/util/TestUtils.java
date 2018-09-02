package com.eightsines.holycycle.util;

import android.text.TextUtils;
import junit.framework.Assert;

public final class TestUtils {
    private TestUtils() {
    }

    public static <T> void assertEqualsAny(T[] expectedList, T actual) {
        if (expectedList.length == 0) {
            throw new RuntimeException("expectedList must be non-empty");
        }

        for (T expected : expectedList) {
            if ((expected == null && actual == null)
                    || (expected != null && actual != null && expected.equals(actual))) {

                return;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Expecting [");
        sb.append(TextUtils.join("] or [", expectedList));
        sb.append("] but received ");

        if (actual == null) {
            sb.append("<NULL>");
        } else {
            sb.append('[');
            sb.append(actual);
            sb.append(']');
        }

        Assert.fail(sb.toString());
    }
}
