package com.trebogeer.klop.game.util;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author dimav
 * Date: 2/22/12
 * Time: 8:14 PM
 */
public final class GLEUtils {
    private GLEUtils() {
    }

    /**
     * Our own MipMap generation implementation.
     * Scale the original bitmap down, always by factor two,
     * and set it as new mipmap level.
     * <p/>
     * Thanks to Mike Miller (with minor changes)!
     *
     * @param gl     - The GL Context
     * @param bitmap - The bitmap to mipmap
     */
    public static void buildMipmap(GL10 gl, Bitmap bitmap) {
        //
        int level = 0;
        //
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        //
        while (height >= 1 || width >= 1) {
            //First of all, generate the texture from our bitmap and set it to the according level
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);

            //
            if (height == 1 || width == 1) {
                break;
            }

            //Increase the mipmap level
            level++;

            //
            height /= 2;
            width /= 2;
            Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);

            //Clean up
            bitmap.recycle();
            bitmap = bitmap2;
        }
    }
}
