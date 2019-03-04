package com.example.myapplication5;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView imgPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        // check permission got or not
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // if permission not get
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            Toast.makeText(this, "已獲得儲存權限!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initView() {
        imgPicture = (ImageView)findViewById(R.id.imgPicture);
        //
        Button btnSavePicture = (Button)findViewById(R.id.btnSavePicture);
        btnSavePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSavePicture();
            }
        });
    }

    private void doSavePicture() {
        if (saveToPictureFolder()) {
            Toast.makeText(MainActivity.this, "儲存成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "儲存失敗", Toast.LENGTH_SHORT).show();
        }
    }

    // save to Internal storage
    private boolean saveToPictureFolder() {
        //取得 Pictures 目錄
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/stock_app");
        Log.d(">>>", "Pictures Folder path: " + picDir.getAbsolutePath());


        //假如有該目錄
        if (!picDir.exists()) {
            if(picDir.mkdirs()) {  // else means something wrong
                System.out.println("Directory Created");

                //儲存圖片
                File pic = new File(picDir, "pic.jpg");
                imgPicture.setDrawingCacheEnabled(true);
                imgPicture.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
                Bitmap bmp = imgPicture.getDrawingCache();

                return saveBitmap(bmp, pic);
            }
        } else {
            //儲存圖片
            File pic = new File(picDir, "pic.jpg");
            imgPicture.setDrawingCacheEnabled(true);
            imgPicture.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
            Bitmap bmp = imgPicture.getDrawingCache();

            return saveBitmap(bmp, pic);
        }

        return false;
    }

    private boolean saveBitmap(Bitmap bmp, File pic) {
        if (bmp == null || pic == null) return false;
        //
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pic);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();

            scanGallery(this, pic);
            Log.d(">>>", "bmp path: " + pic.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.e(">>>", "save bitmap failed!");
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void scanGallery(Context ctx, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }
}
