/*
 * Copyright (C) 2012-2013 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.thialfihar.android.apg;

import android.app.Application;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.thialfihar.android.apg.util.Log;
import org.thialfihar.android.apg.util.PRNGFixes;

import java.io.File;
import java.security.Provider;
import java.security.Security;

public class ApgApplication extends Application {

    /**
     * Called when the application is starting, before any activity, service, or receiver objects
     * (excluding content providers) have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * Sets Bouncy (Spongy) Castle as preferred security provider
         *
         * insertProviderAt() position starts from 1
         */
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

        /*
         * apply RNG fixes
         *
         * among other things, executes Security.insertProviderAt(new
         * LinuxPRNGSecureRandomProvider(), 1) for Android <= SDK 17
         */
        PRNGFixes.apply();
        Log.d(Constants.TAG, "Bouncy Castle set and PRNG Fixes applied!");

        if (Constants.DEBUG) {
            Provider[] providers = Security.getProviders();
            Log.d(Constants.TAG, "Installed Security Providers:");
            for (Provider p : providers) {
                Log.d(Constants.TAG, "provider class: " + p.getClass().getName());
            }
        }

        // Create APG directory on sdcard if not existing
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Constants.Path.APP_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                // ignore this for now, it's not crucial
                // that the directory doesn't exist at this point
            }
        }

        brandGlowEffect(getApplicationContext(),
                getApplicationContext().getResources().getColor(R.color.emphasis));
    }

    static void brandGlowEffect(Context context, int brandColor) {
        // terrible hack to brand the edge overscroll glow effect
        // https://gist.github.com/menny/7878762#file-brandgloweffect_full-java

        //glow
        int glowDrawableId = context.getResources().getIdentifier("overscroll_glow", "drawable", "android");
        Drawable androidGlow = context.getResources().getDrawable(glowDrawableId);
        androidGlow.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
        //edge
        int edgeDrawableId = context.getResources().getIdentifier("overscroll_edge", "drawable", "android");
        Drawable androidEdge = context.getResources().getDrawable(edgeDrawableId);
        androidEdge.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
    }
}
