package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import android.opengl.GLES20;

import shakram02.ahmed.shapelibrary.gl_internals.ShapeMaker;

/**
 * This is a circle, it can be drawn. the VBO is supplied from outside so that
 * it's coupled with its location in the GL program
 */

public class Circle extends DrawableObject {
    public Circle(float cx, float cy, float radius, float[] viewMatrix, int mvpHandle,
                  int verticesHandle,
                  int colorHandle, float[] colorPoints) {
        super(cx, cy, radius, mvpHandle, viewMatrix, verticesHandle,
                ShapeMaker.CreateCirclePoints(cx, cy, radius, 45),
                colorHandle, colorPoints);
    }

    @Override
    public void issueDraw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, raster.getItemCount());
    }
}
