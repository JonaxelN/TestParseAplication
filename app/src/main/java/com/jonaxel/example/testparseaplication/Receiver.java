package com.jonaxel.example.testparseaplication;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

public class Receiver extends ParsePushBroadcastReceiver {

    private String message;
    Data data = new Data();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d(MainActivity.TAG, "onReceive");
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        Log.d(MainActivity.TAG, "onPushOpen");
        /*ParseAnalytics.trackAppOpenedInBackground(intent);

        Intent i = new Intent(context, MainActivity.class);
        Log.d("MESSAGE", ""+ data.getMessage());
        i.putExtra(MainActivity.GET_MESSAGE, data.getMessage());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
    }


    @Override
    protected void onPushReceive(final Context context, final Intent intent) {
        super.onPushReceive(context, intent);

        Log.d(MainActivity.TAG, "onPushReceive");

        ParseQuery<ParseObject> query;
        //Get bundle for obtain ID of row to retrieve
        Bundle bundle = intent.getExtras();
        //Get the whole JSON from bundle
        String messages = bundle.getString("com.parse.Data");
        Log.d(MainActivity.TAG, bundle.getString("com.parse.Data"));
        try {
            //String to object
            JSONObject object = new JSONObject(messages);
            String type = object.getString("type");
            Log.d(MainActivity.TAG, type);
            if (!type.isEmpty()) {
                /**
                 * Case 0 --> Texto
                 * Case 1 --> Image
                 * Case 2 --> Video
                 **/

                switch (Integer.parseInt(type)) {
                    case 0:
                        //From class Message
                        query = ParseQuery.getQuery("Message");
                        //Get column id equals to retrieve
                        query.whereEqualTo("objectId", object.getString("id"));
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    //Get value Texto from class Message where objectId equals to id retrieve
                                    message = parseObject.get("Texto").toString();
                                    new Data(message);
                                    Log.d(MainActivity.TAG, parseObject.get("Texto").toString());
                                } else {
                                    Log.d(MainActivity.TAG, "Error en TEXTO: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;

                    case 1:
                        //From class Message
                        query = ParseQuery.getQuery("Image");
                        //Get column id equals to retrieve
                        query.whereEqualTo("objectId", object.getString("id"));
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {

                                    Log.d("test", "Retrieved the object.");
                                    ParseFile fileObject = (ParseFile) parseObject.get("imageFile");
                                    fileObject.getDataInBackground(new GetDataCallback() {
                                        public void done(byte[] data, ParseException e) {
                                            if (e == null) {
                                                Log.d("test", "We've got data in data.");
                                                // use data for something

                                            } else {
                                                Log.d("test", "There was a problem downloading the data.");
                                            }
                                        }
                                    });
                                    //Get value Texto from class Message where objectId equals to id retrieve
                                    message = parseObject.get("imageFile").toString();
                                } else {
                                    Log.d(MainActivity.TAG, "Error en IMAGEN" + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                }
            }
        } catch (JSONException e) {
            Log.d(MainActivity.TAG, "Something went wrong on pushReceive");
            //e.printStackTrace();
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
        Log.d(MainActivity.TAG, "onPushDismiss");
    }

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        Log.d(MainActivity.TAG, "getActivity");
        return super.getActivity(context, intent);

    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Log.d(MainActivity.TAG, "getNotification");
        return super.getNotification(context, intent);
    }
}
