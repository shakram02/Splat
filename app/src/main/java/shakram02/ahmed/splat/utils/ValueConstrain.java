package shakram02.ahmed.splat.utils;

/**
 * Clamps a number between two values
 */

public class ValueConstrain {
    private final float min;
    private final float max;

    public ValueConstrain(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float clamp(float value) {
        return Math.max(min, Math.min(max, value));
    }
}
