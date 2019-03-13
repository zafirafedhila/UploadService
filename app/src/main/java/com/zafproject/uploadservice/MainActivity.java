package com.zafproject.uploadservice;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zafproject.uploadservice.model.ResponseApiModel;
import com.zafproject.uploadservice.server.ApiServices;
import com.zafproject.uploadservice.server.RetroServer;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnselectimage;
    Dialog dialogNew;
    EditText namaDialog;
    Button btnPilihGambar;
    ImageView gambarDialog;
    Button btnUpload;
    private Button uploadNew;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri filePath;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestStoragePermission();

        btnselectimage = findViewById(R.id.btnselectimage);
        btnselectimage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dialogNew = new Dialog(this);
        dialogNew.setContentView(R.layout.dialog_new);
        dialogNew.setTitle("hay");
        dialogNew.setCancelable(true);
        dialogNew.setCanceledOnTouchOutside(false);

        namaDialog = dialogNew.findViewById(R.id.nama_dialognew);
        btnPilihGambar = dialogNew.findViewById(R.id.btn_pilihgambarnew);
        gambarDialog = dialogNew.findViewById(R.id.gambar_dialognew);
        btnUpload = dialogNew.findViewById(R.id.btn_upload);

        btnPilihGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 1.5
                showFileChooser();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 1.7
                uploadMultipartNew(namaDialog.getText().toString().trim());
            }
        });
        dialogNew.show();

    }

    //todo 6.
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //todo 1.5.1
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //todo 1.6
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            String[] imageprojection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(filePath, imageprojection, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int indexImage = cursor.getColumnIndex(imageprojection[0]);
                path = cursor.getString(indexImage);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    gambarDialog.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //todo 1.7.1
    public void uploadMultipartNew(String editNama) {
        String name = editNama;

        //todo 1.8
        String path = getPath(filePath);

        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        File imagefile = new File(path);
        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imagefile);
        RequestBody nameSent = RequestBody.create(MediaType.parse("text/plain"), name);

        //$_POST['image']
        String imagePost = "image";
        MultipartBody.Part partImage = MultipartBody.Part.createFormData(imagePost, imagefile.getName(), reqBody);

        ApiServices api = RetroServer.getInstance();
        Call<ResponseApiModel> upload = api.uploadImage(partImage, nameSent);
        upload.enqueue(new Callback<ResponseApiModel>() {
            @Override
            public void onResponse(Call<ResponseApiModel> call, Response<ResponseApiModel> response) {
                Log.d("RETRO", "ON RESPONSE  : " + response.body().toString());

                if (!response.body().isError()) {
                    Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Not Uploaded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseApiModel> call, Throwable t) {
                Log.d("RETRO", "ON FAILURE : " + t.getMessage());
                Toast.makeText(MainActivity.this, "test " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //todo 1.7.1
    //untuk upload image
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
    //todo 1.7.2
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}

