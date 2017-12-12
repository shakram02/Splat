package shakram02.ahmed.shapelibrary.gl_internals;

public class ShapeMaker {
    private static final int COORDINATE_COUNT = 3;

    public static float[] CreateCirclePoints(float cx, float cy, float r, int num_segments) {

        double theta = (2 * 3.1415926) / num_segments;
        double c = Math.cos(theta);
        double s = Math.sin(theta);
        double t;

        float x = r;//we start at angle = 0
        float y = 0;

        float[] points = new float[COORDINATE_COUNT * num_segments];

        for (int i = 0; i < (COORDINATE_COUNT * num_segments); i += COORDINATE_COUNT) {
            // X, Y, Z
            points[i] = (x + cx);
            points[i + 1] = (y + cy);
            points[i + 2] = 0f;

            //apply the rotation matrix
            t = x;
            x = (float) (c * x - s * y);
            y = (float) (s * t + c * y);
        }

        return points;
    }

    public static float[] createCircle(int pointCount, float radius) {
        float circleVertices[] = new float[pointCount * COORDINATE_COUNT];
        final float TWO_PI = (float) (Math.PI * 2f);

        for (int i = 0; i < pointCount; i++) {
            float angleRadians = ((float) (i) / (float) pointCount) * TWO_PI;
            circleVertices[(i * 3)] = (float) (radius * Math.sin(angleRadians));
            circleVertices[(i * 3) + 1] = (float) (radius * Math.cos(angleRadians));
            circleVertices[(i * 3) + 2] = 0;
        }

        return circleVertices;
    }

    public static float[] createRectangle(float cx, float cy, float width, float height) {
        float[] points = new float[6 * COORDINATE_COUNT];
        // Top right, ccw 1 2 3 4
        // triangles 1 2 4, 2 3 4
        int index = 0;

        points[index++] = cx + width;
        points[index++] = cy + height;
        points[index++] = 0;

        points[index++] = cx + width;
        points[index++] = cy - height;
        points[index++] = 0;

        points[index++] = cx - width;
        points[index++] = cy + height;
        points[index++] = 0;

        // -----
        points[index++] = cx + width;
        points[index++] = cy - height;
        points[index++] = 0;

        points[index++] = cx - width;
        points[index++] = cy - height;
        points[index++] = 0;

        points[index++] = cx - width;
        points[index++] = cy + height;
        points[index] = 0;

        return points;
    }

    public static float[] createTriangle(float cx, float cy, float radius) {
        float[] points = new float[3 * COORDINATE_COUNT];
        int index = 0;

        points[index++] = cx;
        points[index++] = cy + radius;
        points[index++] = 0;

        points[index++] = cx + radius;
        points[index++] = cy - radius;
        points[index++] = 0;

        points[index++] = cx - radius;
        points[index++] = cy - radius;
        points[index++] = 0;

        return points;
    }
}
