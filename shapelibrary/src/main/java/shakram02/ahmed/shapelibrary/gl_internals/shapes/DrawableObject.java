package shakram02.ahmed.shapelibrary.gl_internals.shapes;

import android.opengl.GLES20;

/**
 * A shape that can be drawn, this class wraps the draw functions
 * of different shapes in a convenient interface
 */

public abstract class DrawableObject extends Transform {

    private final int mvpHandle;
    final Raster raster;
    private final float cx;
    private final float cy;
    private final float radius;

    DrawableObject(float cx, float cy, float radius, int mvpHandle, float[] viewMatrix, int verticesHandle, float[] vertices,
                   int colorHandle, float[] colorPoints) {
        super(viewMatrix);
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.raster = new Raster(verticesHandle, vertices, colorHandle, colorPoints);
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

    public float getX() {
        return cx;
    }

    public float getY() {
        return cy;
    }

    public float getRadius() {
        return radius;
    }

    public final void moveTo(float x, float y) {
        this.resetModelMatrix();
        this.translate(x - this.getX(), y - this.getY());
    }
}
