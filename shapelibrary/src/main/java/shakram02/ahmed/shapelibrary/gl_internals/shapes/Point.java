package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by ahmed on 12/11/17.
 */

public class Point {

    private final float x;
    private final float y;
    private final float z;
    private static final int COORDINATE_COUNT = 3;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Point(float x, float y, float z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(float x, float y) {

        this.x = x;
        this.y = y;
        this.z = 0;
    }

    private float[] flatten() {
        return new float[]{x, y, z};
    }

    public static float[] toArray(Collection<Point> points) {
        float[] result = new float[points.size() * COORDINATE_COUNT];
        Iterator<Point> iter = points.iterator();

        for (int i = 0; iter.hasNext(); i += 3) {
            float[] flattened = iter.next().flatten();
            result[i] = flattened[i];
            result[i + 1] = flattened[i + 1];
            result[i + 2] = flattened[i + 2];
        }

        // TODO check this out
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %s]", x, y, z);
    }
}
