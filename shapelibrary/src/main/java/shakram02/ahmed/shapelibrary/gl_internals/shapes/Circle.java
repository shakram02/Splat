package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import android.opengl.GLES20;

import shakram02.ahmed.shapelibrary.gl_internals.ShapeMaker;

/**
 * This is a circle, it can be drawn. the VBO is supplied from outside so that
 * it's coupled with its location in the GL program
 */

public class Circle extends DrawableObject {
    private final float cx;
    private final float cy;
    private final float radius;

    public Circle(float cx, float cy, float radius, float[] viewMatrix, int mvpHandle,
                  int verticesHandle,
                  int colorHandle, float[] colorPoints) {
        super(mvpHandle, viewMatrix,
                new Raster(
                        verticesHandle, ShapeMaker.CreateCirclePoints(cx, cy, radius, 45),
                        colorHandle, colorPoints
                ));
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
    }

    @Override
    public void issueDraw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, raster.getItemCount());
    }
}
