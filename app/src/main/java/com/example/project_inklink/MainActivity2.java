package com.example.project_inklink;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {

    EditText etBookName;
    Button btnUpload;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main2);
        etBookName=findViewById(R.id.etAddbook);
        btnUpload=findViewById(R.id.btnUploadBook);
        btnUpload.setEnabled(false);
        Intent intent=getIntent();
        User user= (User) intent.getSerializableExtra("user");
        Toast.makeText(this,user.getEmail()+" "+user.getPfpUrl()+" "+user.getUsername(),Toast.LENGTH_SHORT).show();
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("pdfbooks");
        etBookName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"),12);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==12 && resultCode==RESULT_OK  && data!=null && data.getData()!=null)
        {
            btnUpload.setEnabled(true);
            etBookName.setText(Objects.requireNonNull(data.getDataString()).substring(
                    data.getDataString().lastIndexOf("/")+1
            ));
        }
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert data != null;
                uploadPDFToFirebase(data.getData());
            }
        });
    }
    private <Uri> void uploadPDFToFirebase(Uri data)
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading file");
        progressDialog.show();

        StorageReference reference=storageReference.child("PDF"+System.currentTimeMillis()+".pdf");
        reference.putFile((android.net.Uri) data).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task= (Task<Uri>) taskSnapshot.getStorage().getDownloadUrl();
                        while (!task.isComplete());
                        Uri uri=task.getResult();

                        PDFBook pdfBook=new PDFBook(uri.toString(),new ArrayList<>(),"NAME","Scifiction book",50,"USER MAN");
                        String key=databaseReference.push().getKey();
                        assert key != null;
                        databaseReference.child(key).setValue(pdfBook);
                        Toast.makeText(MainActivity2.this,"Book Uploaded",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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
}