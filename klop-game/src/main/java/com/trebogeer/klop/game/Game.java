package com.trebogeer.klop.game;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author dimav
 *         Date: 2/21/12
 *         Time: 5:28 PM
 */
public class Game extends Activity {

    private GLSurfaceView mGLView;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mGLView = new NiceCubeGL(this);

        textView = new TextView(this);
        textView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        textView.setTextSize(50);
        textView.setGravity(Gravity.RIGHT);
        // textView.setText(R.string.bottom_text);
        textView.setTextColor(Color.WHITE);
        textView.setText("Shake Me!");
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
        FrameLayout fl = new FrameLayout(this);
        fl.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));

        mGLView = new SixFaceCubeRenderer(this, textView);
        mGLView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT));
        fl.addView(mGLView);
        fl.addView(textView);
        this.setContentView(fl);

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
