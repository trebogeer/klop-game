package com.trebogeer.klop.game;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static com.trebogeer.klop.game.util.SysUtils.getWakeLock;

/**
 * @author dimav
 *         Date: 2/21/12
 *         Time: 5:28 PM
 */
public class Game extends Activity {

    private GLSurfaceView mGLView;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGLView = new SixFaceCubeRenderer(this);
        mGLView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        //  mGLView.setZOrderOnTop(true);
        //  mGLView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setContentView(mGLView);
        wakeLock = getWakeLock(this, "klop-game");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        wakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
        wakeLock.acquire();
    }

}
