package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import shakram02.ahmed.shapelibrary.gl_internals.Painter;
import shakram02.ahmed.shapelibrary.gl_internals.UniformPainter;
import shakram02.ahmed.shapelibrary.gl_internals.memory.VertexBufferObject;

/**
 * Created by ahmed on 12/9/17.
 */

class Raster {
    private static final int XYZ_POINT_LENGTH = 3;
    private final Painter painter;
    private final VertexBufferObject circleVertices;

    Raster(int verticesHandle, float[] vertices,
           int colorHandle, float[] colorPoints) {
        painter = new UniformPainter(colorHandle, colorPoints);
        circleVertices = new VertexBufferObject(vertices,
                verticesHandle, XYZ_POINT_LENGTH);
    }

    void startDraw() {
        this.painter.paint();
        this.circleVertices.startDraw();
    }

    void endDraw() {
        this.circleVertices.endDraw();
    }

    int getItemCount() {
        return this.circleVertices.getItemCount();
    }
}
