package com.wladischlau.vlt.core.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.util.concurrent.Callable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MdcUtil {

    public static <T> T withMdc(Callable<T> callable, Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new RuntimeException("number of keyValues args should be divisible by 2");
        }
        for (int i = 0; i < keyValues.length / 2; ++i) {
            var key = keyValues[2 * i];
            var val = keyValues[2 * i + 1];
            MDC.put(String.valueOf(key), String.valueOf(val));
        }
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            for (int i = 0; i < keyValues.length / 2; ++i) {
                var key = keyValues[2 * i];
                MDC.remove(String.valueOf(key));
            }
        }
    }

    public static void withMdc(Runnable runnable, Object... keyValues) {
        withMdc(() -> {
            runnable.run();
            return null;
        }, keyValues);
    }
}