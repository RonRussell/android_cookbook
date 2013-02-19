/*
 * Copyright (c) 2013 Two Chips Off, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package com.tco.examples.formfactorresolver;

import com.tco.examples.formfactorresolver.util.SystemUiHider;
import com.tco.utils.FormFactorResolver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and navigation/system bar) with
 * user interaction.
 * 
 * @see SystemUiHider
 */
public class ExampleDriverActivity extends Activity
{
    /**
     * Whether or not the system UI should be auto-hidden after {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user interaction before hiding the system
     * UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the system UI visibility upon
     * interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        

        setContentView(R.layout.activity_example_driver);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        
        FormFactorResolver.dump(this);
        
        if (FormFactorResolver.isPhoneFormFactor(this))
            ((TextView)contentView).setText("Screen form factor : PHONE");
        else if (FormFactorResolver.isMediumTabletFormFactor(this))
            ((TextView)contentView).setText("Screen form factor : MEDIUM TABLET");
        else if (FormFactorResolver.isLargeTabletFormFactor(this))
            ((TextView)contentView).setText("Screen form factor : LARGE TABLET");
        else
            ((TextView)contentView).setText("Screen form factor : UNKNOWN");
        

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener()
            {
                // Cached values.
                int mControlsHeight;
                int mShortAnimTime;

                @Override
                @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                public void onVisibilityChange(boolean visible)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
                    {
                        // If the ViewPropertyAnimator API is available
                        // (Honeycomb MR2 and later), use it to animate the
                        // in-layout UI controls at the bottom of the
                        // screen.
                        if (mControlsHeight == 0)
                        {
                            mControlsHeight = controlsView.getHeight();
                        }
                        if (mShortAnimTime == 0)
                        {
                            mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                        }
                        controlsView.animate().translationY(visible ? 0 : mControlsHeight).setDuration(mShortAnimTime);
                    }
                    else
                    {
                        // If the ViewPropertyAnimator APIs aren't
                        // available, simply show or hide the in-layout UI
                        // controls.
                        controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                    }

                    if (visible && AUTO_HIDE)
                    {
                        // Schedule a hide().
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                }
            });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (TOGGLE_ON_CLICK)
                    {
                        mSystemUiHider.toggle();
                    }
                    else
                    {
                        mSystemUiHider.show();
                    }
                }
            });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the system UI. This is to prevent the jarring
     * behavior of controls going away while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if (AUTO_HIDE)
                {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
                return false;
            }
        };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                mSystemUiHider.hide();
            }
        };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any previously scheduled calls.
     */
    private void delayedHide(int delayMillis)
    {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
