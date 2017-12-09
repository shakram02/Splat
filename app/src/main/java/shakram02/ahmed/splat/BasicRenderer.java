package shakram02.ahmed.splat;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import shakram02.ahmed.shapelibrary.gl_internals.FrustumManager;
import shakram02.ahmed.shapelibrary.gl_internals.ShapeMaker;
import shakram02.ahmed.shapelibrary.gl_internals.memory.GLProgram;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Axis;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Circle;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Rectangle;
import shakram02.ahmed.splat.utils.TextResourceReader;


/**
 * Created by ahmed on 11/19/17.
 * <p>
 * OpenGL lesson 1
 */

public class BasicRenderer implements GLSurfaceView.Renderer {
    private final int XYZ_POINT_LENGTH = 3;
    private final Context context;
    private Circle moonCircle;
    private Circle earthCircle;
    private Circle sunCircle;
//    private Rectangle rectangle;

    BasicRenderer(Context context) {

        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background clear earthColor to gray.
        GLES20.glClearColor(0.15f, 0.15f, 0.15f, 0.15f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        float earthColor[] = {0.13671875f, 0.26953125f, 0.92265625f, 1.0f};
        float sunColor[] = {0.93671875f, 0.76953125f, 0.12265625f, 1.0f};
        float moonColor[] = {0.93671875f, 0.73671875f, 0.63671875f, 1.0f};
        float[] mViewMatrix = new float[16];

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX,
                lookY, lookZ, upX, upY, upZ);

        String vertexShader = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);

        String fragmentShader = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        GLProgram program = new GLProgram(vertexShader, fragmentShader);
        String positionVariableName = "a_Position";

        program.declareAttribute(positionVariableName);
        String colorVariableName = "v_Color";
        program.declareUniform(colorVariableName);
        String mvpMatrixVariableName = "u_MVPMatrix";
        program.declareUniform(mvpMatrixVariableName);

        // Tell OpenGL to use this program when rendering.
        program.activate();

        Integer colorHandle = program.getVariableHandle(colorVariableName);
        Integer mvpHandle = program.getVariableHandle(mvpMatrixVariableName);
        Integer verticesHandle = program.getVariableHandle(positionVariableName);


        sunCircle = new Circle(0, 0, 0.3f, mViewMatrix,
                mvpHandle, verticesHandle, colorHandle, sunColor);
        moonCircle = new Circle(0, 0, 0.3f, mViewMatrix,
                mvpHandle, verticesHandle, colorHandle, moonColor);
        earthCircle = new Circle(0, 0, 0.3f, mViewMatrix,
                mvpHandle, verticesHandle, colorHandle, earthColor);

//        VertexBufferObject rectVertices = new VertexBufferObject(
//                ShapeMaker.createRectangle(0f, 0f, 0.4f, 0.2f),
//                verticesHandle, 3);
//        rectangle = new Rectangle(mViewMatrix, mvpHandle, rectVertices, sunPainter);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Store the projection matrix. This is used to project the scene onto a 2D viewport.
        float[] mProjectionMatrix = FrustumManager.createFrustum(0, 0, width, height);

        moonCircle.setProjectionMatrix(mProjectionMatrix);
        earthCircle.setProjectionMatrix(mProjectionMatrix);
        sunCircle.setProjectionMatrix(mProjectionMatrix);
//        rectangle.setProjectionMatrix(mProjectionMatrix);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 1000000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        float rotationRatio = time / 2500.0f;

        // Draw the triangle facing straight on.
        float deltaX = (float) (2 * Math.sin(rotationRatio * Math.PI));
        float deltaY = (float) (2 * Math.cos(rotationRatio * Math.PI));

        earthCircle.resetModelMatrix();
        earthCircle.scale(0.5f, 0.5f);
        earthCircle.rotate(angleInDegrees, Axis.Z);
        earthCircle.translate(deltaX, deltaY);
        earthCircle.draw();

        // MOON
        // Draw the triangle facing straight on.
        float deltaXMoon = (float) (deltaX / Math.sin(rotationRatio * Math.PI));
        float deltaYMoon = (float) (deltaY / Math.cos(rotationRatio * Math.PI));

        // Coupling the model matrix is required by the relative motion
        moonCircle.setModelMatrix(earthCircle.getModelMatrix());
        moonCircle.scale(0.3f, 0.3f);
        moonCircle.translate(deltaXMoon / 2f, deltaYMoon);
        moonCircle.draw();

        sunCircle.draw();
//        rectangle.draw();
    }


    void handleTouchPress(float normalizedX, float normalizedY) {
        Log.w("TTTTT", "Touched at:" + normalizedX);
    }

    void handleTouchDrag(float normalizedX, float normalizedY) {

    }
}
