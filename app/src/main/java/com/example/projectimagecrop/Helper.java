package com.example.projectimagecrop;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.loader.content.CursorLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class Helper {

    public static BitmapFactory.Options getMyOption(String imagePath){

        try {

            File file = new File(imagePath);
            FileInputStream fis = new FileInputStream(file);;
            Log.e(""," Photo file length "+fis.available());

            BitmapFactory.Options options = new BitmapFactory.Options();

            if(fis.available()  > 3000000){
                options.inSampleSize = 11;
            }else if(fis.available() >= 2000000 && fis.available() < 3000000){
                options.inSampleSize = 8;
            }else if(fis.available() >= 1000000 && fis.available() < 2000000){
                options.inSampleSize = 6;
            }else if(fis.available() >= 800000 && fis.available() < 1000000){
                options.inSampleSize = 4;
            }else if(fis.available() >= 700000 && fis.available() < 800000){
                options.inSampleSize = 3;
            }else if(fis.available() >= 500000 && fis.available() < 700000){
                options.inSampleSize = 2;
            }else{
                options.inSampleSize = 0;
            }
            return options;

        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;


    }

    // Get Path of selected image
    public static String getPath(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "imageview", null);
        return Uri.parse(path);
    }
    
}
