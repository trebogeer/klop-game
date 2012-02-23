package com.trebogeer.klop.game;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author dimav
 * Date: 2/22/12
 * Time: 8:12 PM
 */
public interface IShape {

    public void draw(GL10 gl, int filter);
    public void loadGLTexture(GL10 gl, Context context);

}
