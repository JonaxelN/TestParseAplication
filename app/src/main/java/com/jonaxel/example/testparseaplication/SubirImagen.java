package com.jonaxel.example.testparseaplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class SubirImagen extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView imageView;
    private EditText textChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_imagen);

        textChannel = (EditText) findViewById(R.id.text_channel_image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subir_imagen, menu);
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

    public void obtenerImagen(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
                    null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView = (ImageView) findViewById(R.id.image_retrieve);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    public void subirImagen(View view) {
        Toast.makeText(getApplicationContext(), "Uploading image", Toast.LENGTH_SHORT).show();
        byte[] image = getImageView();                                  //Get converted image file

        final String channel = textChannel.getText().toString();
        ParseFile imgFile = new ParseFile ("braveImg.png", image);
        imgFile.saveInBackground();

        final JSONObject dataSend = new JSONObject();

        final ParseObject imageUpload = new ParseObject("Image");       //Upload in class image on parse
        imageUpload.put("Image", "picturePath");
        imageUpload.put("imageFile", imgFile);
        imageUpload.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String id = imageUpload.getObjectId();
                    try {
                        dataSend.put("title", "Reeact");
                        dataSend.put("alert", "You have a new message");
                        dataSend.put("type", "1");
                        dataSend.put("id", id);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    ParsePush parsePush = new ParsePush();
                    parsePush.setChannel(channel);
                    parsePush.setData(dataSend);

                    Toast.makeText(getApplicationContext(), "Your images success", Toast.LENGTH_SHORT).show();

                    parsePush.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(MainActivity.TAG, "Successful push with data");
                                Toast.makeText(getBaseContext(), "Message send", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.d(MainActivity.TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private byte[] getImageView() {
        imageView.buildDrawingCache();           //Get image from ImageView
        Bitmap bmap = imageView.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();            //Return converted image file
    }
}
