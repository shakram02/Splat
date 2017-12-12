package shakram02.ahmed.splat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import shakram02.ahmed.shapelibrary.gl_internals.FrustumManager;
import shakram02.ahmed.shapelibrary.gl_internals.memory.GLProgram;
import shakram02.ahmed.shapelibrary.gl_internals.motion.LowPassFilter;
import shakram02.ahmed.shapelibrary.gl_internals.motion.MedianFilter;
import shakram02.ahmed.shapelibrary.gl_internals.motion.NumericHelpers;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Circle;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Point;
import shakram02.ahmed.shapelibrary.gl_internals.shapes.Triangle;
import shakram02.ahmed.splat.game.CollisionDetector;
import shakram02.ahmed.splat.game.LocationTracker;
import shakram02.ahmed.splat.game.MissileSummoner;
import shakram02.ahmed.splat.utils.TextResourceReader;


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
    private static final int MISSILE_MAX_DELAY_MS = 2400;
    private static final int MISSILE_MIN_DELAY_MS = 150;
    private static final float PLAYER_RADIUS = 0.06f;
    private static final float PLAYER_Y_LOCATION = -0.87f;
    private static final float ALPHA = 0.8f;

    private final MedianFilter medianFilter = new MedianFilter(MEDIAN_ARRAY_LENGTH);
    private final LowPassFilter lowPassFilter = new LowPassFilter(ALPHA);

    private final LocationTracker locationTracker;
    private final CollisionDetector collisionDetector;
    private final MissileSummoner missileSummoner;

    private final Object lock = new Object();

    BasicRenderer(Context context) {
        this.context = context;
        collisionDetector = new CollisionDetector(2 * PLAYER_RADIUS);
        locationTracker = new LocationTracker(0.019f);
        missileSummoner = new MissileSummoner(MISSILE_MIN_DELAY_MS, MISSILE_MAX_DELAY_MS, locationTracker);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background clear earthColor to gray.
        GLES20.glClearColor(0.15f, 0.15f, 0.15f, 1f);
        float[] mViewMatrix = new float[16];

        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.0001f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        float playerColor[] = {0.83671875f, 0.26953125f, 0.62265625f, 1.0f};
        float missileColor[] = {0.83671875f, 0.76953125f, 0.12265625f, 1.0f};

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


        sunCircle = new Circle(0.12f, mViewMatrix,
                mvpHandle, verticesHandle, colorHandle, playerColor);
        sunCircle.setLocation(0f, PLAYER_Y_LOCATION);

        enemy = new Triangle(PLAYER_RADIUS,
                mvpHandle, mViewMatrix, verticesHandle, colorHandle, missileColor);

        locationTracker.addEnemy(0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        //Store the projection matrix. This is used to project the scene onto a 2D viewport.
        float[] mProjectionMatrix = FrustumManager.createFrustum(0, 0, width, height);
        surfaceReady = true;
        sunCircle.setProjectionMatrix(mProjectionMatrix);
        enemy.setProjectionMatrix(mProjectionMatrix);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        synchronized (lock) {
            sunCircle.draw();

            // Update enemy locations and draw them
            while (!locationTracker.isFrameDone() && locationTracker.hasEnemies()) {
                Point enemyLoc = locationTracker.getNextEnemyLocation();
                enemy.resetModelMatrix();

                if (collisionDetector.collidesWith(new Point(sunCircle.getX(), sunCircle.getY()), enemyLoc)) {
                    continue;   // Don't render while colliding
                }

                enemy.setLocation(enemyLoc.getX(), enemyLoc.getY());
                enemy.draw();
            }
        }
    }


    void handleTouchPress(float normalizedX, float normalizedY) {
        //        Log.w("TTTTT", "Draged at:" + normalizedX + ", " + normalizedY);
        locationTracker.addEnemy(normalizedX);
    }

    void handleTouchDrag(float normalizedX, float normalizedY) {
        //        Log.w("TTTTT", "Draged at:" + normalizedX + ", " + normalizedY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER
                || !surfaceReady) {
            return;
        }

        float xAcceleration = lowPassFilter.filter(event.values[0]);

        // The range is changed to let the tilting less harsh
        float medianReading = medianFilter.insertAndGet(xAcceleration);
        medianReading = NumericHelpers.mapFloat(medianReading, -10, 10, -1, 1);

        // Atomic ball movement, sometimes draw is called after resetModel() which causes
        // the ball to move to the middle
        synchronized (lock) {
            sunCircle.setLocation(-1 * medianReading, sunCircle.getY());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    void onPause() {
        missileSummoner.stop();
    }

    void onResume() {
        missileSummoner.run();
    }
}
