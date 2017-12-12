package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import android.opengl.GLES20;

import shakram02.ahmed.shapelibrary.gl_internals.Painter;
import shakram02.ahmed.shapelibrary.gl_internals.ShapeMaker;
import shakram02.ahmed.shapelibrary.gl_internals.memory.VertexBufferObject;

/**
 * Created by ahmed on 12/7/17.
 */

public class Rectangle extends DrawableObject {
    public Rectangle(float width, float height, float[] viewMatrix, int mvpHandle,
                     int verticesHandle,
                     int colorHandle, float[] colorPoints) {

        super(Math.max(width, height), mvpHandle, viewMatrix, verticesHandle,
                ShapeMaker.createRectangle(0, 0, width, height),
                colorHandle, colorPoints
        );
    }

    @Override
    protected void issueDraw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, raster.getItemCount());
    }
}
