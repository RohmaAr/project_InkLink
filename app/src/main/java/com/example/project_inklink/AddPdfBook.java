package com.example.project_inklink;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class AddPdfBook extends AppCompatActivity {

    private ArrayList<String> selectedGenres = new ArrayList<>();
    private CheckBox cbThriller, cbInformative, cbHorror, cbDrama, cbFantasy,cbTechnology,
            cbSciFi, cbSuspense, cbSelfHelp, cbPhilosophy, cbFiction, cbHistory, cbPolitics;
    EditText etBookName;
    Uri imageUri;
    Button btnUpload;
    ImageView ivBookCover;
    EditText etDes;
    TextView tvfindBook;
    ToggleButton toggleButton;
   // ProgressBar progressBar;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    User user;
    PDFBook pdfBook;
    ImageView ivback;
    TextView tvFragName;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpdfbook);
        toggleButton = findViewById(R.id.btnPaid);
        etDes = findViewById(R.id.etpdfsetDesc);
        etBookName = findViewById(R.id.etpdfSetName);
        btnUpload = findViewById(R.id.btnUploadBook);
        ivBookCover = findViewById(R.id.ivpdfaddPicture);
        tvfindBook = findViewById(R.id.tvBookFound);
        ivback=findViewById(R.id.ivtoolbarback);
        tvFragName=findViewById(R.id.toolbartitle);
        tvFragName.setText("Add Book");
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnUpload.setEnabled(false);
        init();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        assert user != null;
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("pdfbooks");
        tvfindBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), 12);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==12 && resultCode==RESULT_OK  && data!=null && data.getData()!=null)
        {
            tvfindBook.setText(getFileName(data.getData()));
            btnUpload.setEnabled(true);

        }
        else if(requestCode==30 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            ivBookCover.setImageURI(data.getData());
            ivBookCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageUri=data.getData();
        }
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert data != null;
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
                    Toast.makeText(AddPdfBook.this, "Pick at least one genre", Toast.LENGTH_SHORT).show();
                    return;
                }
                final boolean[] duplicate = new boolean[1];
                Query pdfQuery = FirebaseDatabase.getInstance().getReference("pdfbooks")
                        .orderByChild("name")
                        .equalTo(name);

                pdfQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // A book with the same name already exists in pdfbooks
                            etBookName.setError("Book with this name already exists");
                            duplicate[0] =true;
                        } else {
                            // Check if a book with the same name exists in imagebooks collection
                            Query imageQuery = FirebaseDatabase.getInstance().getReference("imagebooks")
                                    .orderByChild("name")
                                    .equalTo(name);

                            imageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // A book with the same name already exists in imagebooks
                                        etBookName.setError("Book with this name already exists");
                                        duplicate[0]=true;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(AddPdfBook.this, "Error checking imagebooks", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AddPdfBook.this, "Error checking pdfbooks", Toast.LENGTH_SHORT).show();
                    }
                });
                if(!duplicate[0])
                    uploadPDFToFirebase(data.getData());
            }
        });
    }
    //storage image uri and pass it here
    private <Uri> void uploadPDFToFirebase(Uri data)
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading file");
        progressDialog.show();

        StorageReference reference=storageReference.child(tvfindBook.getText().toString().trim());
        StorageReference imageRef = storageReference.child("coverimages"+etBookName.getText().toString().trim()+"cover");

        reference.putFile((android.net.Uri) data).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task= (Task<Uri>) taskSnapshot.getStorage().getDownloadUrl();
                        while (!task.isComplete());
                        Uri uri=task.getResult();
                        String name=etBookName.getText().toString().trim();
                        String desc=etDes.getText().toString().trim();
                        if(imageUri!=null) {
                            imageRef.putFile((android.net.Uri) imageUri).addOnSuccessListener(
                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @SuppressLint("UseCompatLoadingForDrawables")
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> task = (Task<Uri>) taskSnapshot.getStorage().getDownloadUrl();
                                            while (!task.isComplete()) ;
                                            Uri uriImage = task.getResult();
                                           //WRITE THIS IN OWNER SPACE YAAAAD SE user.getUsername()
                                            pdfBook=new PDFBook(uri.toString(),selectedGenres,name,desc,0,null,uriImage.toString());

                                            if(toggleButton.isChecked())
                                                pdfBook.setPaid(true);
                                            else
                                                pdfBook.setPaid(false);
                                            pdfBook.setLikes(0);
                                            String key=databaseReference.push().getKey();
                                            assert key != null;
                                            databaseReference.child(key).setValue(pdfBook);
                                            Toast.makeText(AddPdfBook.this,"Book Uploaded",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            System.out.println("Picture posted");
                                            finish();
                                        }
                                    }
                            ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Failed uploading: "+e.getMessage());
                                    finish();
                                }
                            });
                        }
                        else{
                            pdfBook=new PDFBook(uri.toString(),selectedGenres,name,desc,0,user.getUsername(),null);

                            pdfBook.setLikes(0);
                            String key=databaseReference.push().getKey();
                            assert key != null;
                            databaseReference.child(key).setValue(pdfBook);
                            Toast.makeText(AddPdfBook.this,"Book Uploaded",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }

                    }
                }
        ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress= (double) (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("File uploaded "+(int)progress+"%");
            }
        });

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
    private void init(){
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
                startActivityForResult(intent,30);
            }
        });
    }
}