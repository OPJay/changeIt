/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fullpower.changeit.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.StrictMode;

import com.fullpower.changeit.AppApplication;
import com.fullpower.changeit.activities.MainActivity;
import com.fullpower.changeit.serviceAlphaCoders.Wallpaper;
import com.fullpower.changeit.serviceFlickr.PhotoFlickr;
import com.fullpower.changeit.activities.WallpaperPagerActivity;
import com.fullpower.changeit.service500px.Photo500px;
import com.fullpower.changeit.servicePixabay.Hit;
import com.fullpower.changeit.serviceUnSplash.PhotoUnSplash;

//import com.fullpower.changeit.instagram.MainActivity;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    private Utils() {};
    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder
                        .setClassInstanceLimit(MainActivity.class, 1)
                        .setClassInstanceLimit(WallpaperPagerActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }
    public static boolean hasLollipop()
    {
        return Build.VERSION.SDK_INT>=VERSION_CODES.LOLLIPOP;
    }
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }
    public static int isInstanceOf(Object obj)
    {
        if(Photo500px.class.isInstance(obj))
            return 0;
        else if(PhotoFlickr.class.isInstance(obj))
            return 1;
        else if(PhotoUnSplash.class.isInstance(obj))
            return 2;
        else if(Hit.class.isInstance(obj))
            return 3;
        else if(Wallpaper.class.isInstance(obj))
            return 4;
        else
            return -1;
    }
    public static int getResolution()
    {
        if(AppApplication.width<600)
            return 1;
        else return 2;
    }
}
