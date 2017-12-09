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
    private BasicRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        GLSurfaceView mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        renderer = new BasicRenderer(this);
        // Request an OpenGL ES 2.0 compatible context.
        mGLSurfaceView.setRenderer(renderer);
        mGLSurfaceView.setOnTouchListener(new GLSurfaceTouchListener(mGLSurfaceView, renderer));

        setContentView(mGLSurfaceView);
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
                view.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        renderer.handleTouchPress(normalizedX, normalizedY);
                    }
                });
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                view.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        renderer.handleTouchDrag(normalizedX, normalizedY);
                    }
                });
            }
            return true;
        }
    }
}
