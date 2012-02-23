package com.trebogeer.klop.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import com.trebogeer.klop.game.util.GLEUtils;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author dimav
 *         Date: 2/22/12
 *         Time: 10:19 AM
 */
public class CubeA implements IShape {

    /**
     * The buffer holding the vertices
     */
    private FloatBuffer vertexBuffer;
    /**
     * The buffer holding the texture coordinates
     */
    private FloatBuffer textureBuffer;
    /**
     * The buffer holding the indices
     */
    private ByteBuffer indexBuffer;
    /**
     * The buffer holding the normals
     */
    private FloatBuffer normalBuffer;

    /**
     * Our texture pointer
     */
    private int[] textures = new int[3];

    /**
     * The initial vertex definition
     */
    private float vertices[] = {
            // Vertices according to faces
            -1.0f, -1.0f, 1.0f, //v0
            1.0f, -1.0f, 1.0f,     //v1
            -1.0f, 1.0f, 1.0f,     //v2
            1.0f, 1.0f, 1.0f,     //v3

            1.0f, -1.0f, 1.0f,     //...
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,

            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,

            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,

            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
    };

    /**
     * The initial normals for the lighting calculations
     */
    private float normals[] = {
            //Normals
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
    };

    /**
     * The initial texture coordinates (u, v)
     */
    private float texture[] = {
            //Mapping coordinates for the vertices
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    /**
     * The initial indices definition
     */
    private byte indices[] = {
            // Faces definition
            0, 1, 3, 0, 3, 2,         // Face front
            4, 5, 7, 4, 7, 6,         // Face right
            8, 9, 11, 8, 11, 10,     // ...
            12, 13, 15, 12, 15, 14,
            16, 17, 19, 16, 19, 18,
            20, 21, 23, 20, 23, 22,
    };

    /**
     * The Cube constructor.
     * <p/>
     * Initiate the buffers.
     */
    public CubeA() {
        //
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuf.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        //
        byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuf.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);

        //
        byteBuf = ByteBuffer.allocateDirect(normals.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        normalBuffer = byteBuf.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);

        //
        indexBuffer = ByteBuffer.allocateDirect(indices.length);
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    /**
     * The object own drawing function.
     * Called from the renderer to redraw this instance
     * with possible changes in values.
     *
     * @param gl     - The GL Context
     * @param filter - Which texture filter to be used
     */
    public void draw(GL10 gl, int filter) {
        //Bind the texture according to the set texture filter
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[filter]);

        //Enable the vertex, texture and normal state
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        //Set the face rotation
        gl.glFrontFace(GL10.GL_CCW);

        //Point to our buffers
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

        //Draw the vertices as triangles, based on the Index Buffer information
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);

        //Disable the client state before leaving
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
    }

    /**
     * Load the textures
     *
     * @param gl      - The GL Context
     * @param context - The Activity context
     */
    public void loadGLTexture(GL10 gl, Context context) {
        //Get the texture from the Android resource directory
        InputStream is1 = context.getResources().openRawResource(R.drawable.blue);
        InputStream is2 = context.getResources().openRawResource(R.drawable.green);
        InputStream is3 = context.getResources().openRawResource(R.drawable.red);
        InputStream is4 = context.getResources().openRawResource(R.drawable.purple);
        InputStream is5 = context.getResources().openRawResource(R.drawable.white);
        InputStream is6 = context.getResources().openRawResource(R.drawable.yellow);
        Bitmap bitmap1 = null;
        Bitmap bitmap2 = null;
        Bitmap bitmap3 = null;
        Bitmap bitmap4 = null;
        Bitmap bitmap5 = null;
        Bitmap bitmap6 = null;
        try {
            //BitmapFactory is an Android graphics utility for images
            bitmap1 = BitmapFactory.decodeStream(is1);
            bitmap2 = BitmapFactory.decodeStream(is2);
            bitmap3 = BitmapFactory.decodeStream(is3);
            bitmap4 = BitmapFactory.decodeStream(is4);
            bitmap5 = BitmapFactory.decodeStream(is5);
            bitmap6 = BitmapFactory.decodeStream(is6);

        } finally {
            //Always clear and close
            try {
                is1.close();
                is1 = null;
                is2.close();
                is2 = null;
                is3.close();
                is3 = null;
                is4.close();
                is4 = null;
                is5.close();
                is5 = null;
                is6.close();
                is6 = null;
            } catch (IOException e) {
            }
        }

        //Generate there texture pointer
        gl.glGenTextures(3, textures, 0);

        //Create Nearest Filtered Texture and bind it to texture 0
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap1, 0);

        //Create Linear Filtered Texture and bind it to texture 1
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap1, 0);

        //Create mipmapped textures and bind it to texture 2
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
        /*
           * This is a change to the original tutorial, as buildMipMap does not exist anymore
           * in the Android SDK.
           *
           * We check if the GL context is version 1.1 and generate MipMaps by flag.
           * Otherwise we call our own buildMipMap implementation
           */
        if (gl instanceof GL11) {
            gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap1, 0);

            //
        } else {
            GLEUtils.buildMipmap(gl, bitmap1);
        }

        //Clean up
        bitmap1.recycle();
        bitmap2.recycle();
        bitmap3.recycle();
    }

}
