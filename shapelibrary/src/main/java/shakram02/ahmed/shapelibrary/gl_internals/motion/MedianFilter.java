package shakram02.ahmed.shapelibrary.gl_internals.motion;

import java.util.Arrays;

/**
 * Simple meedian filter
 */

public class MedianFilter {
    private float[] buffer;
    private int width;

    private int medianCounter = 0;

    public MedianFilter(int width) {

        //noinspection unchecked
        buffer = new float[width];
        this.width = width;
    }

    public float insertAndGet(float value) {
        buffer[medianCounter++ % width] = value;
        medianCounter = medianCounter >= width ? 0 : medianCounter;
        Arrays.sort(buffer);

        return buffer[width / 2];
    }
}
