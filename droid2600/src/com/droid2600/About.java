package com.droid2600;

import android.app.Activity;

import android.os.Bundle;

import android.text.method.LinkMovementMethod;

import android.widget.TextView;



/** 
 * Simple activity for showing information about this application
 */
public class About extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView t1 = (TextView)findViewById(R.id.about_about_textview);
        t1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView t2 = (TextView)findViewById(R.id.about_credits_stella_textview);
        t2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView t3 = (TextView)findViewById(R.id.about_credits_sdl_textview);
        t3.setMovementMethod(LinkMovementMethod.getInstance());

        TextView t4 = (TextView)findViewById(R.id.about_credits_image_textview);
        t4.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
