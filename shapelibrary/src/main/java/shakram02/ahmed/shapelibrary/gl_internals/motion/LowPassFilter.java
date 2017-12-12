package shakram02.ahmed.shapelibrary.gl_internals.motion;

/**
 * Created by ahmed on 12/12/17.
 */

public class LowPassFilter {
    private final float alpha;
    float oldVal;

    public LowPassFilter(float alpha) {
        this.alpha = alpha;
    }

    public float filter(float newVal) {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass medianFilter's time-constant
        // and dT, the event delivery rate

        float value = alpha * oldVal + (1 - alpha) * newVal;
        oldVal = value;

        return value;
    }
}
