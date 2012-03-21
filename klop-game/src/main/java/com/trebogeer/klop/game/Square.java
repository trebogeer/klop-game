package com.trebogeer.klop.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author dimav
 *         Date: 3/11/12
 *         Time: 9:24 AM
 */
public class Square implements IShape {
    
    private int width;
    private int height;

    private FloatBuffer vertexBuffer;    // buffer holding the vertices
    private float vertices[] = {
            -1.0f, -1.0f, 0.0f,        // V1 - bottom left
            -1.0f, 1.0f, 0.0f,        // V2 - top left
            1.0f, -1.0f, 0.0f,        // V3 - bottom right
            1.0f, 1.0f, 0.0f            // V4 - top right
    };

    private FloatBuffer textureBuffer;    // buffer holding the texture coordinates
    private float texture[] = {
            // Mapping coordinates for the vertices
            0.0f, 1.0f,        // top left		(V2)
            0.0f, 0.0f,        // bottom left	(V1)
            1.0f, 1.0f,        // top right	(V4)
            1.0f, 0.0f        // bottom right	(V3)
    };

    /**
     * The texture pointer
     */
    private int[] textures = new int[1];

    public Square(final int width, final int height){
        this();
        this.width = width;
        this.height = height;
    }
    
    public Square() {
        // a float has 4 bytes so we allocate for each coordinate 4 bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    @Override
    public void draw(GL10 gl, int filter) {


        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // Point to our buffers
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // Set the face rotation
        gl.glFrontFace(GL10.GL_CW);

        // Point to our vertex buffer
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

        // Draw the vertices as triangle strip
      //  gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        //Draw the vertices as triangles, based on the Index Buffer information
        // gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
      //  GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, b, 0);
        ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
                GL11Ext.GL_TEXTURE_CROP_RECT_OES,
                new int[] {256, 512, -256, -512}, 0);
        ((GL11Ext) gl).glDrawTexiOES(0, 0, 1, width, height);
        //Disable the client state before leaving
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    @Override
    public void loadGLTexture(GL10 gl, Context context) {

        // loading texture
        InputStream is = context.getResources().openRawResource(R.drawable.bgd03);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            // generate one texture pointer
            gl.glGenTextures(1, textures, 0);
            // ...and bind it to our array
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

            // create nearest filtered texture
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
           // gl.glTexI
//            ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
//                    GL11Ext.GL_TEXTURE_CROP_RECT_OES,
//                    new int[] {0, 0, 256, 512}, 0);
//            ((GL11Ext) gl).glDrawTexiOES(0, 0, 0, width, height);

            // Clean up
            bitmap.recycle();
        } finally {
            try {
                is.close();
                is = null;
            } catch (Exception e) {
            }
        }

    }
}