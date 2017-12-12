package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import android.opengl.GLES20;
import android.util.Log;

/**
 * A shape that can be drawn, this class wraps the draw functions
 * of different shapes in a convenient interface
 */

public abstract class DrawableObject extends Transform {

    private final int mvpHandle;
    final Raster raster;
    private final float radius;
    private float cx;
    private float cy;

    DrawableObject(float radius, int mvpHandle, float[] viewMatrix, int verticesHandle, float[] vertices,
                   int colorHandle, float[] colorPoints) {
        super(viewMatrix);
        this.radius = radius;
        this.raster = new Raster(verticesHandle, vertices, colorHandle, colorPoints);
        this.mvpHandle = mvpHandle;
    }

    public void setLocation(float x, float y) {
        synchronized (this.getModelMatrix()) {
            this.resetModelMatrix();
            this.cx = x;
            this.cy = y;
            this.translate(cx, cy);
        }
    }

    public final void draw() {
        synchronized (this.getModelMatrix()) {
            raster.startDraw();
            GLES20.glUniformMatrix4fv(this.mvpHandle, 1,
                    false, super.getMvpMatrix(), 0);
            this.issueDraw();
            raster.endDraw();
        }
    }

    protected abstract void issueDraw();

    public float getX() {
        return cx;
    }

    public float getY() {
        return cy;
    }

    public float getRadius() {
        return radius;
    }
}
