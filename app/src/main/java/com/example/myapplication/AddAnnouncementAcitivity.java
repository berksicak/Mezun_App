package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddAnnouncementAcitivity extends AppCompatActivity {

    private EditText descriptionInput;
    private Button uploadImageButton;
    private Button sendAnnouncementButton;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap myImage;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    public static final int GALLERY_PERM_CODE = 103;
    public static final int CAMERA_REQUEST_CODE = 102;
    private int listSize;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement_acitivity);

        descriptionInput = (EditText) findViewById(R.id.descriptionInput);
        uploadImageButton = (Button) findViewById(R.id.loadImageButtonOnAddAnnouncement);
        sendAnnouncementButton = (Button) findViewById(R.id.sendAnnouncementButton);
        imageView = (ImageView) findViewById(R.id.imageViewOnAddAnnouncement);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        listSize = intent.getIntExtra("size", 0);

        sendAnnouncementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Announcement announcement = new Announcement(descriptionInput.getText().toString());
                writeUserToFirebase(announcement);
            }
        });


        activityResultLauncher = registerForActivityResult (new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                                imageView.setVisibility(View.VISIBLE);
                                imageView.setImageBitmap(bitmap);
                                myImage = bitmap;
                                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                                params.width = 300; // genişlik 500 piksel olsun
                                params.height = 300; // yükseklik 500 piksel olsun
                                imageView.setLayoutParams(params);

                                uploadImageButton.setText("FOTOĞRAF DEĞİŞTİR");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(pickPhoto);
            }
        });
    }

    private void askGalleryPermissions(){
        if (ContextCompat.checkSelfPermission (this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }else{
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(pickPhoto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(pickPhoto);
            } else {
                Toast.makeText(this, "Gallery Permission is Required to Select an Image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void writeUserToFirebase(Announcement announcement){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("description", announcement.getDescription());
        hashMap.put("userName", readUser());

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        Date nextYear = calendar.getTime();

        hashMap.put("dateTime", now);
        hashMap.put("expireDate", nextYear);



        //upload image to storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.child("announcements/"+String.valueOf(listSize)).putBytes(data);

        //hashMap.put("image", data);
        firebaseFirestore.collection("announcements").document(String.valueOf(listSize))
                .set(hashMap)
                .addOnCompleteListener(AddAnnouncementAcitivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            System.out.println("Clouda yükleme basarili");
                        }
                        else
                            System.out.println("clouda yükleme basarisiz");
                    }
                });

    }
    public String readUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // Kullanıcının bilgilerini SharedPreferences üzerinden yükle
        String name = sharedPreferences.getString("name", "");
        String surname = sharedPreferences.getString("surname", "");
        return name + " " + surname;
    }
}