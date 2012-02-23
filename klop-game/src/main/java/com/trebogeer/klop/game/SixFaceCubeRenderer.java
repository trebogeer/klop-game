package com.trebogeer.klop.game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dimav
 *         Date: 2/22/12
 *         Time: 1:11 PM
 */
public class SixFaceCubeRenderer extends GLSurfaceView implements GLSurfaceView.Renderer, SensorEventListener {

    private IShape cube;

    private SensorManager sensorMgr;
    private long lastUpdate = -1;
    private float x, y, zz;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 400;


    /* Rotation values */
    private float xrot;                    //X Rotation
    private float yrot;                    //Y Rotation

    /* Rotation speed values */
    private AtomicInteger xspeed = new AtomicInteger(0);                //X Rotation Speed
    private AtomicInteger yspeed = new AtomicInteger(0);                //Y Rotation Speed

    private float z = -5.0f;            //Depth Into The Screen

    private int filter = 0;                //Which texture filter?

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Is light enabled
     */
    private boolean light = true;
    /**
     * Is blending enabled ( NEW )
     */
    private boolean blend = false;

    private SoundPool soundPool;

    /* The initial light values */
    private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] lightPosition = {0.0f, 0.0f, 2.0f, 1.0f};

    /* The buffers for our light values */
    private FloatBuffer lightAmbientBuffer;
    private FloatBuffer lightDiffuseBuffer;
    private FloatBuffer lightPositionBuffer;

    /* Variables and factor for the input handler */
    private float oldX;
    private float oldY;
    private final float TOUCH_SCALE = 0.2f;            //Proved to be good for normal rotation

    private int soundId;

    /**
     * The Activity Context
     */
    private Context context;

    public SixFaceCubeRenderer(final Context context) {
        super(context);
        setRenderer(this);

        this.setId(Integer.MAX_VALUE);
        this.requestFocus();
        this.setFocusableInTouchMode(true);

        //
        this.context = context;

        //
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightAmbientBuffer = byteBuf.asFloatBuffer();
        lightAmbientBuffer.put(lightAmbient);
        lightAmbientBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightDiffuseBuffer = byteBuf.asFloatBuffer();
        lightDiffuseBuffer.put(lightDiffuse);
        lightDiffuseBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        lightPositionBuffer = byteBuf.asFloatBuffer();
        lightPositionBuffer.put(lightPosition);
        lightPositionBuffer.position(0);

        // this.cube = new SixFacesCube(context);
        this.cube = new CubeA();

        sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        boolean accelSupported = sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        if (!accelSupported) {
            // on accelerometer on this device
            sensorMgr.unregisterListener(this);
        }

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                float volume = getVolume();
                // Is the sound loaded already?
                // if (loaded) {
                soundPool.play(sampleId, volume, volume, 1, 0, 1f);
                //  Log.e("Test", "Played sound");
                //}
            }
        });
        this.soundId = soundPool.load(context, R.raw.soft_airy_swish, 1);


    }

    private float getVolume() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actualVolume / maxVolume;
    }

    // Call back when the surface is first created or re-created.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i("klop#onSurfaceCreated", "Enterd method");
        //And there'll be light!
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);        //Setup The Ambient Light
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);        //Setup The Diffuse Light
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);    //Position The Light
        gl.glEnable(GL10.GL_LIGHT0);                                            //Enable Light 0

        //Blending
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);                //Full Brightness. 50% Alpha ( NEW )
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);        //Set The Blending Function For Translucency ( NEW )

        //Settings
        gl.glDisable(GL10.GL_DITHER);                //Disable dithering
        gl.glEnable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping
        gl.glShadeModel(GL10.GL_SMOOTH);             //Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);     //Black Background
        gl.glClearDepthf(1.0f);                     //Depth Buffer Setup
        gl.glEnable(GL10.GL_DEPTH_TEST);             //Enables Depth Testing
        gl.glDepthFunc(GL10.GL_LEQUAL);             //The Type Of Depth Testing To Do

        //Really Nice Perspective Calculations
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        //Load the texture for the cube once during Surface creation
        //   cube.loadGLTexture(gl, this.context);
        cube.loadGLTexture(gl, context);
        Log.i("klop#onSurfaceCreated", "exit method");
    }

    // Call back after onSurfaceCreated() or whenever the window's size changes.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) {                         //Prevent A Divide By Zero By
            height = 1;                         //Making Height Equal One
        }

        gl.glViewport(0, 0, width, height);     //Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION);     //Select The Projection Matrix
        gl.glLoadIdentity();                     //Reset The Projection Matrix

        //Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);     //Select The Modelview Matrix
        gl.glLoadIdentity();
    }

    // Call back to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // ----- Render the Cube -----
        gl.glLoadIdentity();                  // Reset the model-view matrix

        //Check if the light flag has been set to enable/disable lighting
        if (light) {
            gl.glEnable(GL10.GL_LIGHTING);
        } else {
            gl.glDisable(GL10.GL_LIGHTING);
        }

        //Check if the blend flag has been set to enable/disable blending
        if (blend) {
            gl.glEnable(GL10.GL_BLEND);            //Turn Blending On ( NEW )
            gl.glDisable(GL10.GL_DEPTH_TEST);    //Turn Depth Testing Off ( NEW )

        } else {
            gl.glDisable(GL10.GL_BLEND);        //Turn Blending On ( NEW )
            gl.glEnable(GL10.GL_DEPTH_TEST);    //Turn Depth Testing Off ( NEW )
        }

        //gl.glTranslatef(0.0f, 0.0f, -6.0f);   // Translate into the screen

        gl.glTranslatef(0.0f, 0.0f, z);
        gl.glScalef(0.8f, 0.8f, 0.8f);


        // gl.glRotatef(angleCube, 0.15f, 1.0f, 0.3f); // Rotate
        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);    //X
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);    //Y

        cube.draw(gl, filter);

        // Update the rotational angle after each refresh.
        // angleCube += speedCube;

        //Change rotation factors
        xrot += (xspeed.floatValue() / 10);
        yrot += (yspeed.floatValue() / 10);

    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis(); // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                x = event.values[0];
                y = event.values[1];
                zz = event.values[2];
                float speed = Math.abs(x + y + zz - last_x - last_y - last_z) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    handleSpeedEvent();
                }
                last_x = x;
                last_y = y;
                last_z = zz;
            }
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {

    }

    /**
     * Override the touch screen listener.
     * <p/>
     * React to moves and presses on the touchscreen.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //
        float x = event.getX();
        float y = event.getY();

        //If a touch is moved on the screen
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //Calculate the change
            float dx = x - oldX;
            float dy = y - oldY;
            //Define an upper area of 10% on the screen
            int upperArea = this.getHeight() / 10;

            //Zoom in/out if the touch move has been made in the upper
            if (y < upperArea) {
                z -= dx * TOUCH_SCALE / 2;

                //Rotate around the axis otherwise
            } else {
                xrot += dy * TOUCH_SCALE;
                yrot += dx * TOUCH_SCALE;
            }

            //A press on the screen
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //Define an upper area of 10% to define a lower area
            int upperArea = this.getHeight() / 10;
            int lowerArea = this.getHeight() - upperArea;

            //
            if (y > lowerArea) {
                //Change the blend setting if the lower area left has been pressed ( NEW )
                if (x < (this.getWidth() / 2)) {
                    if (blend) {
                        blend = false;
                    } else {
                        blend = true;
                    }

                    //Change the light setting if the lower area right has been pressed
                } else {
                    if (light) {
                        light = false;
                    } else {
                        light = true;
                    }
                }
            }
        }

        //Remember the values
        oldX = x;
        oldY = y;

        //We handled the event
        return true;
    }

    /**
     * Override the key listener to receive keyUp events.
     * <p/>
     * Check for the DPad presses left, right, up, down and middle.
     * Change the rotation speed according to the presses
     * or change the texture filter used through the middle press.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            yspeed.decrementAndGet();

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            yspeed.incrementAndGet();

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            xspeed.decrementAndGet();

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            xspeed.incrementAndGet();

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            handleSpeedEvent();
            changeFilter();
        }

        //We handled the event
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorMgr != null) {
            sensorMgr.unregisterListener(this);
            sensorMgr = null;
        }
    }

    private synchronized void changeFilter() {
        filter += 1;
        if (filter > 2) {
            filter = 0;
        }
    }

    private void handleSpeedEvent() {
        if (isRunning.compareAndSet(false, true)) {
            float vol = getVolume();
            soundPool.play(soundId, vol, vol, 1, 0, 1f);
            ((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
            changeFilter();
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    float vol = getVolume();
                    soundPool.play(soundId, vol, vol, 1, 0, 1f);

                    xspeed.set(70);
                    yspeed.set(70);

                    while (xspeed.decrementAndGet() > 0 && yspeed.decrementAndGet() > 0) {
                        try {
                            Thread.sleep(70L);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    isRunning.set(false);
                    xspeed.set(0);
                    yspeed.set(0);


                }
            });
            t.start();
        }
    }

}
