package com.example.myapplication;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private Button signupButton, loadImageButton;
    private EditText nameInput, surnameInput, passwordInput, emailInput, gradYearInput;
    private TextView startDateInput;
    private ImageView imageView;
    static  final int SELECT_IMAGE = 0;
    private Uri imageUri;
    private DatePickerDialog.OnDateSetListener gradDateSetListener;
    private DatePickerDialog.OnDateSetListener startDateSetListener;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private FirebaseAuth firebaseAuth;
    private Bitmap myImage ;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        nameInput = (EditText) findViewById(R.id.nameInputOnSignup);
        passwordInput = (EditText) findViewById(R.id.passwordInputOnSignup);
        surnameInput = (EditText) findViewById(R.id.surnameInputOnSignup);
        startDateInput = (TextView) findViewById(R.id.startDateInput);
        gradYearInput = (EditText) findViewById(R.id.gradYearInput);
        emailInput = (EditText) findViewById(R.id.emailInput);
        signupButton = (Button) findViewById(R.id.signupButtonOnSignup);
        loadImageButton = (Button) findViewById(R.id.loadImageButton);
        imageView = (ImageView) findViewById(R.id.imageViewOnSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

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

                        loadImageButton.setText("FOTOĞRAF DEĞİŞTİR");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

                });

        //signup actions
        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String name = nameInput.getText().toString().trim();
                String surname = surnameInput.getText().toString().trim();
                String startDate = startDateInput.getText().toString().trim();
                String gradYear = gradYearInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString();
                if(name.length()>0 && surname.length()>0 && startDate.length() == 10) {
                    if (gradYear.length() == 4 && isNumeric(gradYear)) {
                        if (isEmail(email)) {
                            if (password.length() >= 8 && !password.equals(password.toLowerCase()) && !password.equals(password.toUpperCase())) {
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this, "Kayıt işlemi başarılı.", Toast.LENGTH_SHORT).show();

                                                    String id =  firebaseAuth.getCurrentUser().getUid();
                                                    User user = new User(id, name, surname, startDate, gradYear, email, password, myImage);

                                                    writeUserToFirebase(user);

                                                    Intent intent = new Intent( SignupActivity.this, MainActivity.class);
                                                    intent.putExtra("email", user.getEmail());
                                                    SignupActivity.this.startActivity(intent);
                                                }
                                                else
                                                    Toast.makeText(SignupActivity.this, "Kayıt işemi başarısız, tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else
                                Toast.makeText(SignupActivity.this, "Şifre Büyük ve Küçük Harf içermeli ayrıca minimum 8 karakterden oluşmalı.", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SignupActivity.this, "Geçerli email giriniz.", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SignupActivity.this, "Mezuniyet yılı 4 basamaklı sayı olmalıdır.", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(SignupActivity.this, "Bütün bilgiler doldurulmalıdır.", Toast.LENGTH_SHORT).show();
            }
        });

        //uploading images
        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });


        //getting input to start date
        startDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(SignupActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                , startDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month +=1;
                String m = "", d = "";
                if (month<10){
                    m="0";
                }
                if (day <10){
                    d = "0";
                }
                String date = d + day+"/" + m + month + "/" + year;
                startDateInput.setText(date);
            }
        };
    }



    private void askCameraPermissions(){
        if (ContextCompat.checkSelfPermission (this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                activityResultLauncher.launch(takePictureIntent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(takePictureIntent);
                }
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public boolean isEmail(String str){
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private void writeUserToFirebase(User user){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", user.getUserID());
        hashMap.put("name", user.getName());
        hashMap.put("surname", user.getSurname());
        hashMap.put("startDate", user.getStartDate());
        hashMap.put("gradulationYear", user.getGradulationYear());
        hashMap.put("email", user.getEmail());
        hashMap.put("password", user.getPassword());

        //upload image to storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.child("users/"+user.getUserID()).putBytes(data);

        //hashMap.put("image", data);
        firebaseFirestore.collection("users").document(user.getUserID())
                .set(hashMap)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<Void>() {
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

}
