package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import android.opengl.GLES20;

import shakram02.ahmed.shapelibrary.gl_internals.ShapeMaker;

/**
 * Created by ahmed on 12/11/17.
 */

public class Triangle extends DrawableObject {
    public Triangle(float radius, int mvpHandle, float[] viewMatrix, int verticesHandle,
                    int colorHandle, float[] colorPoints) {

        super(radius, mvpHandle, viewMatrix, verticesHandle,
                ShapeMaker.createTriangle(0, 0, radius),
                colorHandle, colorPoints);
    }

    @Override
    protected void issueDraw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, raster.getItemCount());
    }
}
