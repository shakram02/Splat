package shakram02.ahmed.splat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import shakram02.ahmed.shapelibrary.gl_internals.FrustumManager;
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

public class BasicRenderer implements GLSurfaceView.Renderer, SensorEventListener {
    private final Context context;
    private Circle moonCircle;
    private Circle earthCircle;
    private Circle sunCircle;
    private Rectangle rectangle;

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


        sunCircle = new Circle(0, 0, 0.38f, mViewMatrix,
                mvpHandle, verticesHandle, colorHandle, sunColor);
        earthCircle = new Circle(0, 0, 0.32f, mViewMatrix,
                mvpHandle, verticesHandle, colorHandle, earthColor);
        moonCircle = new Circle(0, 0, 0.27f, mViewMatrix,
                mvpHandle, verticesHandle, colorHandle, moonColor);

        rectangle = new Rectangle(0f, 0f, 0.4f, 0.2f,
                mViewMatrix, mvpHandle, verticesHandle, colorHandle, sunColor);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Store the projection matrix. This is used to project the scene onto a 2D viewport.
        float[] mProjectionMatrix = FrustumManager.createFrustum(0, 0, width, height);

        moonCircle.setProjectionMatrix(mProjectionMatrix);
        earthCircle.setProjectionMatrix(mProjectionMatrix);
        sunCircle.setProjectionMatrix(mProjectionMatrix);
        rectangle.setProjectionMatrix(mProjectionMatrix);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        sunCircle.draw();
    }


    void handleTouchPress(float normalizedX, float normalizedY) {
        Log.w("TTTTT", "Touched at:" + normalizedX);

    }

    void handleTouchDrag(float normalizedX, float normalizedY) {
        Log.w("TTTTT", "Draged at:" + normalizedX);
    }

    private int medianCounter = 0;
    private static final int MEDIAN_ARRAY_LENGTH = 7;
    private float[] readings = new float[MEDIAN_ARRAY_LENGTH];

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float xAcceleration = event.values[0];

        readings[medianCounter++ % MEDIAN_ARRAY_LENGTH] = xAcceleration;
        Arrays.sort(readings);
        medianCounter = medianCounter >= MEDIAN_ARRAY_LENGTH ? 0 : medianCounter;

        sunCircle.resetModelMatrix();

        float delta = mapf(readings[MEDIAN_ARRAY_LENGTH / 2], -10, 10, -1, 1);
        sunCircle.moveTo(sunCircle.getX() - delta, 0f);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float mapf(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
