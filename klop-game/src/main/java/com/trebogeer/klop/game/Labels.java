package com.trebogeer.klop.game;

import android.content.Context;
import android.graphics.Paint;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author dimav
 *         Date: 3/20/12
 *         Time: 6:38 PM
 */
public class Labels implements IShape {

    int[] textures = new int[1];
    private LabelMaker mLabels;
    private Paint mLabelPaint;

    public Labels() {
        mLabelPaint = new Paint();
        mLabelPaint.setTextSize(128);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setARGB(0xff, 0x00, 0x00, 0x00);
    }

    @Override
    public void draw(GL10 gl, int filter) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);

        mLabels.beginDrawing(gl, 400, 400);
        mLabels.draw(gl, filter);
        mLabels.endDrawing(gl);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

    }


    @Override
    public void loadGLTexture(GL10 gl, Context context) {

        if (mLabels != null) {
            mLabels.shutdown(gl);
        } else {
            mLabels = new LabelMaker(true, 512, 1024);
        }

        mLabels.initialize(gl);
        mLabels.beginAdding(gl);
        String[] colors = new String[]{"BLUE", "GREEN", "RED", "PURPLE", "WHITE", "YELLOW"};
        for (int i = 0; i < colors.length; i++) {
            mLabels.add(gl,/*dr,*/ colors[i], mLabelPaint);
        }
        mLabels.endAdding(gl);
    }
}
