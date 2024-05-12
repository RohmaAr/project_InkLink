package com.example.project_inklink;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.database.Cursor;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddImageBook extends AppCompatActivity {

    private ArrayList<String> selectedGenres = new ArrayList<>();
    private CheckBox cbThriller, cbInformative, cbHorror, cbDrama, cbFantasy,cbTechnology,
            cbSciFi, cbSuspense, cbSelfHelp, cbPhilosophy, cbFiction, cbHistory, cbPolitics;
    EditText etBookName;
    Uri imageUri;
    Button btnUpload,btnFindImages;
    ImageView ivBookCover;
    EditText etDes;
    String coverUri;
    RecyclerView rvImages;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    User user;
    ImageBook imageBook;
    ImageView ivback;
    TextView tvFragName;
    RecyclerAdapter adapter;
    ArrayList<Uri> imageUris=new ArrayList<>();
    private final static int READ_PERMISSION=101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_imagebook);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        assert user != null;
        adapter=new RecyclerAdapter(this,imageUris);
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(adapter);
        if(ContextCompat.checkSelfPermission(AddImageBook.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(AddImageBook.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_PERMISSION);
        }
        btnFindImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Pictures"),1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            if(data.getClipData()!=null)
            {
                btnUpload.setVisibility(View.VISIBLE);
                int x=data.getClipData().getItemCount();
                for(int i=0;i<x;i++)
                {
                    imageUris.add(data.getClipData().getItemAt(i).getUri());
                    adapter.notifyDataSetChanged();
                }

            }
            else if(data.getData()!=null) {
                String imageUri=data.getData().getPath();
                imageUris.add(Uri.parse(imageUri));

                btnUpload.setVisibility(View.VISIBLE);
            }

        }
        else if(requestCode==40 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            ivBookCover.setImageURI(data.getData());
            ivBookCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageUri=data.getData();
           // Toast.makeText(this, "IMAGE SET for cover", Toast.LENGTH_SHORT).show();
        }
    }

    public void disableUpload(){
        btnUpload.setVisibility(View.GONE);
    }
    private void init(){
        btnFindImages=findViewById(R.id.btFindImages);
        etDes = findViewById(R.id.etImagesetDesc);
        etBookName = findViewById(R.id.etImageSetName);
        btnUpload = findViewById(R.id.btnUploadBook);
        btnUpload.setVisibility(View.GONE);
        ivBookCover = findViewById(R.id.ivImageaddPicture);
        ivback=findViewById(R.id.ivtoolbarback);
        tvFragName=findViewById(R.id.toolbartitle);
        rvImages=findViewById(R.id.rvImagesPick);
        tvFragName.setText("Create Images Book");
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cbThriller = findViewById(R.id.cbThriller);
        cbInformative = findViewById(R.id.cbInformative);
        cbHorror = findViewById(R.id.cbHorror);
        cbDrama = findViewById(R.id.cbDrama);
        cbFantasy = findViewById(R.id.cbFantasy);
        cbSciFi = findViewById(R.id.cbSciFi);
        cbSuspense = findViewById(R.id.cbSuspense);
        cbSelfHelp = findViewById(R.id.cbSelfHelp);
        cbPhilosophy = findViewById(R.id.cbPhilosophy);
        cbFiction = findViewById(R.id.cbFiction);
        cbHistory = findViewById(R.id.cbHistory);
        cbPolitics = findViewById(R.id.cbPolitics);
        cbTechnology = findViewById(R.id.cbTechnology);
        // CheckBox listeners
        CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Add the checked genre to the ArrayList
                    selectedGenres.add(buttonView.getText().toString().trim());
                    System.out.println(buttonView.getText().toString()+"was checked");
                } else {
                    // Remove the unchecked genre from the ArrayList
                    selectedGenres.remove(buttonView.getText().toString().trim());

                    System.out.println(buttonView.getText().toString()+"was unchecked");
                }
            }
        };

// Set listeners to CheckBoxes
        cbThriller.setOnCheckedChangeListener(checkBoxListener);
        cbInformative.setOnCheckedChangeListener(checkBoxListener);
        cbHorror.setOnCheckedChangeListener(checkBoxListener);
        cbDrama.setOnCheckedChangeListener(checkBoxListener);
        cbFantasy.setOnCheckedChangeListener(checkBoxListener);
        cbSciFi.setOnCheckedChangeListener(checkBoxListener);
        cbSuspense.setOnCheckedChangeListener(checkBoxListener);
        cbSelfHelp.setOnCheckedChangeListener(checkBoxListener);
        cbPhilosophy.setOnCheckedChangeListener(checkBoxListener);
        cbFiction.setOnCheckedChangeListener(checkBoxListener);
        cbHistory.setOnCheckedChangeListener(checkBoxListener);
        cbPolitics.setOnCheckedChangeListener(checkBoxListener);
        cbTechnology.setOnCheckedChangeListener(checkBoxListener);
        ivBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,40);
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=etBookName.getText().toString().trim();
                String desc=etDes.getText().toString().trim();
                if (name.isEmpty()) {
                    etBookName.setError("Book name cannot be empty");
                    return;
                }

                if (desc.isEmpty()) {
                    etDes.setError("Description cannot be empty");
                    return;
                }

                //Logiv to set the book Cover as icbookcover if not set by user
                if(selectedGenres.isEmpty())
                {
                    Toast.makeText(AddImageBook.this, "Pick at least one genre", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadPDFToFirebase();
            }
        });
    }
    private void uploadPDFToFirebase() {

        ArrayList<String> pages=new ArrayList<>();
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("imagebooks");
        StorageReference imagesBookReference = storageReference.child(etBookName.getText().toString().trim());
        // Storage reference for image files
        StorageReference imageReference = storageReference.child("coverimages" + etBookName.getText().toString().trim() + "cover");

        // List to store the Task objects for uploading files
       // List<Task<Uri>> uploadTasks = new ArrayList<>();

        // Upload Images files
        for (Uri uri : imageUris) {
            StorageReference fileRef = imagesBookReference.child(getFileName(uri));
            UploadTask uploadTask = fileRef.putFile(uri);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // File uploaded successfully, now get the download URL
                    fileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        String downloadLink = downloadUrl.toString();
                        // Do something with the download link (e.g., store it or display it)
                        // For example, you can add it to your 'pages' list:
                        pages.add(downloadLink);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Could not upload book", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Handle upload failure
                    Exception exception = task.getException();
                    // You can throw an exception or handle it as needed
                }

//            uploadTasks.add(uploadTask.continueWithTask(task -> {
//                if (!task.isSuccessful()) {
//                    throw Objects.requireNonNull(task.getException());
//                }
//                Uri pageUri=task.getResult().getUploadSessionUri();
//                assert pageUri != null;
//                pages.add(pageUri.toString());
//                // Continue with the task to get the download URL
//                return fileRef.getDownloadUrl();
            });
        }

        // Upload image file
        if (imageUri != null) {
            UploadTask imageUploadTask = imageReference.putFile(imageUri);
//            uploadTasks.add(imageUploadTask.continueWithTask(task -> {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                Uri pageUri=task.getResult().getUploadSessionUri();
//                assert pageUri != null;
//                coverUri=pageUri.toString();
//                // Continue with the task to get the download URL
//                return imageReference.getDownloadUrl();
//            }));
            imageUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task= (Task<Uri>) taskSnapshot.getStorage().getDownloadUrl();
                    while (!task.isComplete());
                    Uri uri=task.getResult();
                    coverUri=uri.toString();
                    imageBook=new ImageBook(selectedGenres,etBookName.getText().toString().trim(),etDes.getText().toString().trim(),0,user.getUsername(),pages,coverUri);
                    imageBook.setLikes(0);
                    String key=databaseReference.push().getKey();
                    assert key != null;
                    databaseReference.child(key).setValue(imageBook);
                    System.out.println("Files uploaded successfully");
                    Toast.makeText(AddImageBook.this, "Book cover posted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddImageBook.this, "Error Uploading Bookcover", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Combine all upload tasks into a single Task
//        Task<Void> allTasks = Tasks.whenAll(uploadTasks);
//
//        allTasks.addOnSuccessListener(task -> {
//            //ArrayList<String> genres, String name, String desc, int views, String owner, ArrayList<String> urls,String image
//            Toast.makeText(AddImageBook.this, "Book Uploaded", Toast.LENGTH_SHORT).show();
//            imageBook=new ImageBook(selectedGenres,etBookName.getText().toString().trim(),etDes.getText().toString().trim(),0,user.getUsername(),pages,coverUri);
//             imageBook.setLikes(0);
//            String key=databaseReference.push().getKey();
//            assert key != null;
//            databaseReference.child(key).setValue(imageBook);
//            System.out.println("Files uploaded successfully");
//        }).addOnFailureListener(e -> {
//            Toast.makeText(AddImageBook.this, "Failed uploading files: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//          //  progressDialog.dismiss();
//            System.out.println("Failed uploading files: " + e.getMessage());
//        });

   }



    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (uri.getScheme() != null && uri.getScheme().equals("file")) {
            result = new File(Objects.requireNonNull(uri.getPath())).getName();
        }
        return result;
    }


}