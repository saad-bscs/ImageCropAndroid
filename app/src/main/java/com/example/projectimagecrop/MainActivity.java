package com.example.projectimagecrop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {

    Button btnTake;
    ImageView imgProfile;

    private static final int GALLERY_REQUEST = 200;

    private static final String readExternalStorage;

    private static final String readMediaImages;
    public static String storage_permissions = readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String storage_permissions_33 = readMediaImages = Manifest.permission.READ_MEDIA_IMAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTake = findViewById(R.id.btnTake);
        imgProfile = findViewById(R.id.imgProfile);

        btnTake.setOnClickListener(view -> {
            openGallery();
        });
    }

    @AfterPermissionGranted(GALLERY_REQUEST)
    private void openGallery() {

        if (hasGalleryPermission()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                startActivityForResult(intent, GALLERY_REQUEST);
            }

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_gallery), GALLERY_REQUEST, permissions());

        }
    }

    private boolean hasGalleryPermission() {

        return EasyPermissions.hasPermissions(this, permissions());
    }

    public static String[] permissions() {
        String p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return new String[]{p};
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Log.e("=====From Gallery", "=========");

                Uri sourceUri = data.getData();
                String currentTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File destinationFile = new File(getCacheDir(), "croppedImage.png" + currentTimeStamp);
                Uri destinationUri = Uri.fromFile(destinationFile);

                // Start uCrop activity
                assert sourceUri != null;
                UCrop.of(sourceUri, destinationUri)
                        .withAspectRatio(1, 1) // Set the desired aspect ratio
                        .withMaxResultSize(500, 500) // Set maximum size for the cropped image
                        .withOptions(getCropOptions()) // Set additional options
                        .start(this);

            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }
        }
    }

    private void handleCropResult(@Nullable Intent data) {
        if (data == null) return;

        Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
            // Handle the cropped image URI

            imgProfile.setImageURI(resultUri);
        }
    }

    private void handleCropError(@Nullable Intent data) {
        if (data == null) return;

        final Throwable cropError = UCrop.getError(data);
        if (cropError != null) {
            cropError.printStackTrace();
            // Handle the error here
        }
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(90);
        options.setHideBottomControls(false); // Show or hide controls
        options.setFreeStyleCropEnabled(true); // Enable freestyle cropping
        return options;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }
}