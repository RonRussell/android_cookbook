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


package com.tco.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class FormFactorResolver
{
    /* Class data members */
    private static final int UNDEFINED = -1;
    private static final String TAG = FormFactorResolver.class.getSimpleName();
    
    /* Initially set as undefined */
    private static int _screenLayoutSize = UNDEFINED;

    /**
     * Determine whether or not this device is likely to have the form factor of a phone 
     */
    public static boolean isPhoneFormFactor(Context context)
    {
        return getScreenLayoutSize(context) < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    
    /**
     * Determine whether or not this device is likely to have the form factor of a 7" tablet or larger 
     */
    public static boolean isTabletFormFactor(Context context)
    {
        return (isMediumTabletFormFactor(context) || isLargeTabletFormFactor(context));
    }

    /**
     * Determine whether or not this device is likely to have a form factor similar to a 7" tablet  
     */
    public static boolean isMediumTabletFormFactor(Context context)
    {
        return getScreenLayoutSize(context) >= Configuration.SCREENLAYOUT_SIZE_LARGE && getScreenLayoutSize(context) < Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    
    /**
     * Determine whether or not this device is likely to have a form factor similar to a 10" tablet
     */
    public static boolean isLargeTabletFormFactor(Context context)
    {
        return getScreenLayoutSize(context) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    
    /**
     * Dump the detected form factor to logcat
     */
    public static void dump(Context context)
    {
        String layoutSize;
        
        switch (getScreenLayoutSize(context))
        {
            case Configuration.SCREENLAYOUT_SIZE_SMALL :
                layoutSize = "SCREENLAYOUT_SIZE_SMALL";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL :
                layoutSize = "SCREENLAYOUT_SIZE_NORMAL";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE :
                layoutSize = "SCREENLAYOUT_SIZE_LARGE";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE :
                layoutSize = "SCREENLAYOUT_SIZE_XLARGE";
                break;
            default :
                layoutSize = "UNKNOWN";
        }
        
        Log.i(TAG, "Current screen layout size is " + layoutSize);
    }
    
    
    private static int getScreenLayoutSize(Context context)
    {
        if (UNDEFINED == _screenLayoutSize)
        {
            _screenLayoutSize = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        }
        
        return _screenLayoutSize;
    }

}
