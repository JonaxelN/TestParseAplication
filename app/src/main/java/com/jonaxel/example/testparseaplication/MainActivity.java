package com.jonaxel.example.testparseaplication;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText texto;
    private EditText canal;

    public TextView textoEditar;

    private ParsePush parsePush;

    public static final String TAG = "parse";
    public static final String GET_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        texto = (EditText) findViewById(R.id.txt_texto);
        canal = (EditText) findViewById(R.id.txt_canal);
        textoEditar = (TextView) findViewById(R.id.texto_mostrar);

        parsePush = new ParsePush();
        Bundle extras = getIntent().getExtras();
        if (getIntent().getStringExtra(GET_MESSAGE) != null) {
            String message = extras.getString(GET_MESSAGE);
            Log.d("MENSAJE", "" + message);
            textoEditar.setText(message);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Function.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Function.activityPaused();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void alPresionar(View view) {
        parsePush.setChannel(canal.getText().toString());
        parsePush.setMessage(texto.getText().toString());
        parsePush.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Successful sent message");
                } else {
                    Log.d(TAG, "Something went wrong");
                    e.printStackTrace();
                }
            }
        });
    }

    public void customPush(View view) {

        final JSONObject dataSend = new JSONObject();

        final String channel = canal.getText().toString();
        String message = texto.getText().toString();

        final ParseObject object = new ParseObject("Message");
        object.put("Texto", message);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    String id = object.getObjectId();
                    try {
                        dataSend.put("title", "Reeact");
                        dataSend.put("alert", "You have a new message");
                        dataSend.put("type", "0");
                        dataSend.put("id", id);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    ParsePush parsePush = new ParsePush();
                    parsePush.setChannel(channel);
                    parsePush.setData(dataSend);

                    Log.d(TAG, "Mensaje subido correctamente");

                    parsePush.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Successful push with data");
                                Toast.makeText(getBaseContext(), "Message send", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void subirImagen(View view) {
        startActivity(new Intent(this, SubirImagen.class));
    }

    public void enviarWhats(View view) {
        PackageManager pm=getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "http://www.facebook.com";

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void enviarSms(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", "http://facebook.com");
        startActivity(sendIntent);
    }

    public void grabarVideo(View view) {
        startActivity(new Intent(this, MyViewCapture.class));
    }
}
