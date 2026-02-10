package org.firstinspires.ftc.teamcode.utils;

/**
 * Simple logger to aid debugging without using System.out directly.
 */
public final class Logger {
    public static void log(String tag, String message) {
        // TODO: implement logging to telemetry or logcat
        System.out.println("[" + tag + "] " + message);
    }

    private Logger() {}
}
