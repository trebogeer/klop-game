package com.trebogeer.klop.game.test;

import android.test.ActivityInstrumentationTestCase2;
import com.trebogeer.klop.game.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<Game> {

    public HelloAndroidActivityTest() {
        super(Game.class);
    }

    public void testActivity() {
        Game activity = getActivity();
        assertNotNull(activity);
    }
}

