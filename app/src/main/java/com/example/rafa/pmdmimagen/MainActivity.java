package com.example.rafa.pmdmimagen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private Uri uri;
    private ImageView iv;
    private Button bt;
    public static final int REQUEST_IMAGE_GET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.imagen);
        bt = (Button) findViewById(R.id.btCargar);
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_GET){}
        Bitmap thumbnail = data.getParcelableExtra("data");
        uri = data.getData();
        if(uri != null){
            iv.setImageURI(uri);
        }
    }

    public void init(){
        if(getIntent().getData()!=null){
            Uri selectedImage = getIntent().getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);
                iv.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {

            }
        }
    }

    public void original(View v){
        iv.setImageBitmap(bitmap);
        iv.setImageURI(uri);
    }

    public void guardar(View v){

        bitmap = ((BitmapDrawable)iv.getDrawable()).getBitmap();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), "foto.jpg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarFoto(View v){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    public void rotarBitmap(View v) {
        BitmapDrawable bmpDraw = (BitmapDrawable) iv.getDrawable();
        bitmap = bmpDraw.getBitmap();
        iv.setImageBitmap(MainActivity.rotarBitmap2(bitmap, 90));
    }

    public void saturacion (View v) {
        BitmapDrawable bmpDraw = (BitmapDrawable) iv.getDrawable();
        bitmap = bmpDraw.getBitmap();
        iv.setImageBitmap(MainActivity.saturacion2(bitmap));
    }

    public void toEscalaDeGris(View v) {
        BitmapDrawable bmpDraw = (BitmapDrawable) iv.getDrawable();
        bitmap = bmpDraw.getBitmap();
        iv.setImageBitmap(MainActivity.toEscalaDeGris2(bitmap));
    }

    public static Bitmap rotarBitmap2(Bitmap bmpOriginal, float angulo) {
        Matrix matriz = new Matrix();
        matriz.postRotate(angulo);
        return Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matriz, true);
    }

    public static Bitmap saturacion2 (Bitmap bitmap) {
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int pixel, red, green, blue, alpha;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                pixel = bitmap.getPixel(i, j);
                red = Color.red(pixel) + 20;
                green = Color.green(pixel) + 50;
                blue = Color.blue(pixel) + 100;
                alpha = Color.alpha(pixel);
                bmp.setPixel(i, j, Color.argb(alpha, red, green, blue));
            }
        }
        return bmp;
    }

    public static Bitmap toEscalaDeGris2(Bitmap bmpOriginal) {
        Bitmap bmpGris = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas lienzo = new Canvas(bmpGris);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(cmcf);
        lienzo.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGris;
    }
}
