package org.firstinspires.ftc.teamcode.utils;

/**
 * Utility math functions commonly used across subsystems.
 */
public final class MathUtil {
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private MathUtil() {}
}
