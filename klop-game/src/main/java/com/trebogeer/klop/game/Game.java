package com.trebogeer.klop.game;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * @author dimav
 *         Date: 2/21/12
 *         Time: 5:28 PM
 */
public class Game extends Activity {

    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mGLView = new NiceCubeGL(this);
        mGLView = new SixFaceCubeRenderer(this);
        // setContentView(R.layout.main);
        // mGLView = new ImageSwitcher()
        // mGLView = new TouchSurfaceView(this);
        // mGLView = new GLSurface(this);
        // setContentView(mGLView);
        // mGLView.requestFocus();
        // mGLView.setFocusableInTouchMode(true);
        // startActivity(new Intent(this, GameAnimalImages.class));
        // startActivity(new Intent(this, NiceCubeGL.class));
      //  mGLView.setRenderer(new SixFaceCubeRenderer(this));
        this.setContentView(mGLView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }

}
