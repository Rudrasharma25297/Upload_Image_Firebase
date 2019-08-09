package com.example.arsltechtutorials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


     private static final int PICK_UP = 234;
    private Uri filePath;
    private Button btn_upload, btn_choose;
    private ImageView imageView;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_choose = (Button)findViewById(R.id.btn_choose);
        btn_upload = (Button)findViewById(R.id.btn_upload);
        imageView = (ImageView)findViewById(R.id.myImage);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

         btn_choose.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 chooseImage();
             }
         });


    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_UP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        filePath = data.getData();
        Bitmap   bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);

        Toast.makeText(getApplicationContext(),""+data.getData(),Toast.LENGTH_SHORT).show();
//        if(requestCode == PICK_UP && requestCode == RESULT_OK && data!=null && data.getData()!=null){
//
//            filePath = data.getData();
//            Toast.makeText(getApplicationContext(),""+data.getData(),Toast.LENGTH_SHORT).show();
//
//            try {
//                Bitmap   bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        }


    private void uploadImage() {

        if(filePath!=null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading.......");
            progressDialog.show();

            StorageReference reference = storageReference.child("images/" + UUID.randomUUID().toString());
            reference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progres = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progres+"%");

                        }
                    });

        }

    }
}
