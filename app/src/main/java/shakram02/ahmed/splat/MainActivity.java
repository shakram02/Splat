package shakram02.ahmed.splat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private CustomSurfaceView mGLSurfaceView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        if (!supportsEs2) {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        mGLSurfaceView = new CustomSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        // Request an OpenGL ES 2.0 compatible context.
        mGLSurfaceView.setRenderer(new BasicRenderer(this));
        mGLSurfaceView.setOnTouchListener(new GLSurfaceTouchListener(mGLSurfaceView));

        setContentView(mGLSurfaceView);
    }

    static class GLSurfaceTouchListener implements View.OnTouchListener {
        CustomSurfaceView mGLSurfaceView;

        GLSurfaceTouchListener(CustomSurfaceView mGLSurfaceView) {
            this.mGLSurfaceView = mGLSurfaceView;
        }

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
                mGLSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mGLSurfaceView.handleTouchPress(normalizedX, normalizedY);
                    }
                });
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mGLSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mGLSurfaceView.handleTouchDrag(normalizedX, normalizedY);
                    }
                });
            }
            return true;
        }
    }
}
