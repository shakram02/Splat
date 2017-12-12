package shakram02.ahmed.shapelibrary.gl_internals.motion;

import android.util.Log;

/**
 * Clamps a number between two values
 */

public class NumericHelpers {

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float calculateNudge(float mappedSensorVal, float currentX) {
        float deltaMax = Math.abs(mappedSensorVal - currentX);

        if (mappedSensorVal < 0) {
            deltaMax *= -1;
        }
        // The delta added to the center must not exceed the range [-1, 1]
        return clamp(deltaMax, -1 - currentX, 1 - currentX);
    }

    public static float mapFloat(float x, float inMin,
                                 float inMax, float outMin, float outMax) {

        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
}
