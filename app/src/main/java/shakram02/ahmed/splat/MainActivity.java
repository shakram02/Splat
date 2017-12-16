package shakram02.ahmed.splat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private BasicRenderer renderer;
    private SensorManager sensorManager;
    private Sensor sensor;
    GLSurfaceView mGLSurfaceView;
    Timer scoreUpdater;

    private Integer score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        if (!supportsEs2) {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        mGLSurfaceView = this.findViewById(R.id.game_view_port);

        mGLSurfaceView.setEGLContextClientVersion(2);
        renderer = new BasicRenderer(this);
        // Request an OpenGL ES 2.0 compatible context.
        mGLSurfaceView.setRenderer(renderer);
        mGLSurfaceView.setOnTouchListener(new GLSurfaceTouchListener(mGLSurfaceView, renderer));

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        TextView scoreTextView = this.findViewById(R.id.scoreTextView);
        scoreUpdater = new Timer();
        scoreUpdater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    MainActivity.this.score = renderer.getScore();
                    runOnUiThread(() -> scoreTextView.setText("Score:" + score));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 200);


        // Wait for death
        Thread onDeathThread = new Thread(() -> {
            Looper.prepare();
            while (!Thread.interrupted()) {
                try {
                    renderer.getDead();
                    assert vibrator != null;
                    vibrator.vibrate(220);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                runOnUiThread(() ->
                {
                    Toast.makeText(this,
                            "Opps!, your score was " + score
                                    + ", Restarting..."
                            , Toast.LENGTH_SHORT).show();
                    score = 0;
                });
                try {
                    renderer.onPause();
                    Thread.sleep(1500);
                    renderer.onResume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.resetGame();

            }
        });

        onDeathThread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(renderer,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
        renderer.onResume();
        sensorManager.registerListener(renderer,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
        renderer.onPause();
        sensorManager.unregisterListener(renderer, sensor);
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(renderer);
        super.onStop();
    }

    static class GLSurfaceTouchListener implements View.OnTouchListener {
        private final GLSurfaceView view;
        BasicRenderer renderer;

        GLSurfaceTouchListener(GLSurfaceView view, BasicRenderer renderer) {
            this.view = view;
            this.renderer = renderer;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event == null) {
                return false;
            }

            // Convert touch coordinates into normalized device
            // coordinates, keeping in mind that Android's Y
            // coordinates are inverted.
            final float normalizedX =
                    (event.getX() / (float) v.getWidth()) * 2 - 1;
            final float normalizedY =
                    -((event.getY() / (float) v.getHeight()) * 2 - 1);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.queueEvent(() -> renderer.handleTouchPress(normalizedX, normalizedY));
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                view.queueEvent(() -> renderer.handleTouchDrag(normalizedX, normalizedY));
            }
            return true;
        }
    }

    private AlertDialog createGameOverDialog() {
        // Now it's dead, Do some vibration
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Game Over, your score is: " + score);
        dlgAlert.setTitle("Opps!");

        dlgAlert.setPositiveButton("OK", (dialog, which) -> {
            this.resetGame();
        });

        dlgAlert.setCancelable(false);
        return dlgAlert.create();
    }

    private void resetGame() {
        this.score = 0;
        renderer.resetGame();
    }
}
