package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import android.opengl.GLES20;

/**
 * A shape that can be drawn, this class wraps the draw functions
 * of different shapes in a convenient interface
 */

public abstract class DrawableObject extends Transform {

    private final int mvpHandle;
    final Raster raster;

    DrawableObject(int mvpHandle, float[] viewMatrix, Raster raster) {
        super(viewMatrix);
        this.raster = raster;
        this.mvpHandle = mvpHandle;
    }

    public final void draw() {
        raster.startDraw();
        GLES20.glUniformMatrix4fv(this.mvpHandle, 1,
                false, super.getMvpMatrix(), 0);
        this.issueDraw();
        raster.endDraw();
    }

    protected abstract void issueDraw();
}
