package shakram02.ahmed.splat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import shakram02.ahmed.shapelibrary.gl_internals.FrustumManager;
import shakram02.ahmed.shapelibrary.gl_internals.memory.GLProgram;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Circle;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Point;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Triangle;
import shakram02.ahmed.splat.game.LocationTracker;
import shakram02.ahmed.splat.utils.MedianFilter;
import shakram02.ahmed.splat.utils.TextResourceReader;
import shakram02.ahmed.splat.utils.ValueConstrain;


/**
 * Created by ahmed on 11/19/17.
 * <p>
 * OpenGL lesson 1
 */

public class BasicRenderer implements GLSurfaceView.Renderer, SensorEventListener {
    private final Context context;
    private Circle sunCircle;
    private Triangle enemy;

    private boolean surfaceReady = false;

    private static final int MEDIAN_ARRAY_LENGTH = 7;
    private float slideMin = -0.9f;
    private float slideMax = 0.9f;
    private final MedianFilter filter = new MedianFilter(MEDIAN_ARRAY_LENGTH);
    private final ValueConstrain valueConstrain = new ValueConstrain(slideMin, slideMax);
    private final LocationTracker locationTracker = new LocationTracker(0.009f);

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


        sunCircle = new Circle(0, -0.67f, 0.12f, mViewMatrix,
                mvpHandle, verticesHandle, colorHandle, sunColor);

        enemy = new Triangle(0f, 0f, 0.06f,
                mvpHandle, mViewMatrix, verticesHandle, colorHandle, earthColor);

        locationTracker.addEnemy(0.5f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Store the projection matrix. This is used to project the scene onto a 2D viewport.
        float[] mProjectionMatrix = FrustumManager.createFrustum(0, 0, width, height);

        surfaceReady = true;
        sunCircle.setProjectionMatrix(mProjectionMatrix);
        enemy.setProjectionMatrix(mProjectionMatrix);
    }

    private AtomicBoolean drawingLocked = new AtomicBoolean(true);

    @Override
    public void onDrawFrame(GL10 gl) {
        if (drawingLocked.get()) {
            return;
        }
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        long time = System.currentTimeMillis() % 1000000L;

        // Do a complete rotation every 10 seconds.
        sunCircle.draw();

        while (!locationTracker.isFrameDone() && locationTracker.hasEnemies()) {
            Point enemyLoc = locationTracker.getNextEnemyLocation();

            enemy.resetModelMatrix();
            enemy.translate(enemyLoc.getX(), enemyLoc.getY());
            enemy.draw();
        }
    }


    void handleTouchPress(float normalizedX, float normalizedY) {
        Log.w("TTTTT", "Draged at:" + normalizedX + ", " + normalizedY);
        locationTracker.addEnemy(normalizedX);
    }

    void handleTouchDrag(float normalizedX, float normalizedY) {
        Log.w("TTTTT", "Draged at:" + normalizedX + ", " + normalizedY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || !surfaceReady) {
            return;
        }

        float xAcceleration = event.values[0];
        filter.addValue(xAcceleration);
        float medianReading = filter.getFilteredValue();

        // The range is changed to let the tilting less harsh
        float delta = mapFloat(medianReading, -8, 8, -1, 1);
        float clampedValue = valueConstrain.clamp(sunCircle.getX() - delta);

        // Atomic ball movement, sometimes draw is called after resetModel() which causes
        // the ball to move to the middle
        drawingLocked.set(true);
        sunCircle.moveTo(clampedValue, sunCircle.getY());
        drawingLocked.set(false);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private float mapFloat(float x, float inMin,
                           float inMax, float outMin, float outMax) {
        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
}
