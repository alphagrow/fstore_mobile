package com.growit.posapp.fstore.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Common wrapper for logging function
 */

public class Logging {
    /**
     *  Logs at verbose level
     * @param tag logging tag
     * @param classContainingMethod the class containing this log message
     * @param method the method in which this log message is generated
     * @param message log message or null
     */
    public static void logVerbose(@NonNull String tag, @NonNull String classContainingMethod,
                                  @NonNull String method, @Nullable String message) {
        String msg = classContainingMethod + "." + method +
                ((message != null) ? " " + message : "");
        Log.v(tag, msg);
    }

    /**
     *  Logs at debug level
     * @param tag logging tag
     * @param classContainingMethod the class containing this log message
     * @param method the method in which this log message is generated
     * @param message log message or null
     */
    public static void logDebug(@NonNull String tag, @NonNull String classContainingMethod,
                                @NonNull String method, @Nullable String message) {
        String msg = classContainingMethod + "." + method +
                ((message != null) ? " " + message : "");
        Log.d(tag, msg);
    }

    /**
     *  Logs at info level
     * @param tag logging tag
     * @param classContainingMethod the class containing this log message
     * @param method the method in which this log message is generated
     * @param message log message or null
     */
    public static void logInfo(@NonNull String tag, @NonNull String classContainingMethod,
                               @NonNull String method, @Nullable String message) {
        String msg = classContainingMethod + "." + method +
                ((message != null) ? " " + message : "");
        Log.i(tag, msg);
    }

    /**
     *  Logs at warn level
     * @param tag logging tag
     * @param classContainingMethod the class containing this log message
     * @param method the method in which this log message is generated
     * @param message log message or null
     */
    public static void logWarn(@NonNull String tag, @NonNull String classContainingMethod,
                               @NonNull String method, @Nullable String message) {
        String msg = classContainingMethod + "." + method +
                ((message != null) ? " " + message : "");
        Log.w(tag, msg);
    }

    /**
     *  Logs at error level
     * @param tag logging tag
     * @param classContainingMethod the class containing this log message
     * @param method the method in which this log message is generated
     * @param message log message or null
     */
    public static void logError(@NonNull String tag, @NonNull String classContainingMethod,
                                @NonNull String method, @Nullable String message) {
        String msg = classContainingMethod + "." + method +
                ((message != null) ? " " + message : "");
        Log.e(tag, msg);
    }

    /**
     *  Logs at "What a Terrible Failure" level (always enabled)
     * @param tag logging tag
     * @param classContainingMethod the class containing this log message
     * @param method the method in which this log message is generated
     * @param message log message or null
     */
    public static void logWtf(@NonNull String tag, @NonNull String classContainingMethod,
                              @NonNull String method, @Nullable String message) {
        String msg = classContainingMethod + "." + method +
                ((message != null) ? " " + message : "");
        Log.wtf(tag, msg);
    }

    // TODO: 4/3/18 Remove the functions below this comment once all logs are
    // converted to use the four input parameter functions, above.
    public static void logVerbose(String tag, String message) {
        logVerbose(tag, "FIX", "ME", message);
    }
    public static void logDebug(String tag, String message) {
        logDebug(tag, "FIX", "ME", message);
    }
    public static void logInfo(String tag, String message) {
        logInfo(tag, "FIX", "ME", message);
    }
    public static void logWarn(String tag, String message) {
        logWarn(tag, "FIX", "ME", message);
    }
    public static void logError(String tag, String message) {
        logError(tag, "FIX", "ME", message);
    }
    public static void logWtf(String tag, String message) {
        logWtf(tag, "FIX", "ME", message);
    }
}
