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
