package com.jonaxel.example.testparseaplication;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class Function extends Application {

    private static boolean activityVisible;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "CofixcfvGfJogAXWkPHQg44lhIlgv1uEeHizUZBt", "wo0lZyD1DpzhcyhvM2tyMsMi5hR7klpfCKsmiD0H");
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "Succed save installation");
                } else {
                    Log.e("com.parse.push", "failed to installation", e);
                }
            }
        });

        ParsePush.subscribeInBackground("broadcast", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}
