package shakram02.ahmed.splat.utils;

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

    public float getFilteredValue() {
        return buffer[width / 2];
    }

    public void addValue(float value) {
        buffer[medianCounter++ % width] = value;
        medianCounter = medianCounter >= width ? 0 : medianCounter;
        Arrays.sort(buffer);
    }
}
