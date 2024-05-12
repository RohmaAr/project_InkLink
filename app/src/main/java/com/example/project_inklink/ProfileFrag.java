package com.example.project_inklink;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Bundle bundle;
    private User user;
    private  PDFBookRecyclerAdapter adapter;
    private ImageBookRecyclerAdapter imageBookRecyclerAdapter;
    String KEY_PARENT="pdfbooks";
    DatabaseReference reference,referenceForImage;
    Button bt,btImageBooks;
    ImageView ivProfile,ivback;
    TextView tvFragName;
    RecyclerView rvMyPdfbooks,rvMyImageBooks;
    EditText etUsername,etEmail;
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public ProfileFrag() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFrag newInstance(String param1, String param2) {
        ProfileFrag fragment = new ProfileFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        imageBookRecyclerAdapter.startListening();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        imageBookRecyclerAdapter.stopListening();
        adapter.stopListening();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bt =view.findViewById(R.id.bt);
        btImageBooks=view.findViewById(R.id.btImageBookAdd);
        ivProfile=view.findViewById(R.id.ivProfileImage);
        etEmail=view.findViewById(R.id.etProfileEmail);
        etUsername=view.findViewById(R.id.etProfileUsername);
        rvMyPdfbooks=view.findViewById(R.id.rvProfileMyPDFbooks);
        rvMyImageBooks=view.findViewById(R.id.rvProfileMyImagebooks);
        ivback=view.findViewById(R.id.ivtoolbarback);
        rvMyImageBooks.setVisibility(View.VISIBLE);
        rvMyPdfbooks.setVisibility(View.VISIBLE);
        ivback.setVisibility(View.GONE);
        tvFragName=view.findViewById(R.id.toolbartitle);
        tvFragName.setText("Profile");


        if (bundle != null) {
            user= (User) bundle.getSerializable("user");
            // Now you can use the receivedData in your fragment
        }
        else {
            System.out.println("BUNDLE EMPTY IN PROFILE FRAG");
        }
        assert user != null;
        reference = FirebaseDatabase.getInstance().getReference();
        FirebaseRecyclerOptions<PDFBook> options =
                new FirebaseRecyclerOptions.Builder<PDFBook>()
                        .setQuery(reference.child(KEY_PARENT).orderByChild("owner").equalTo(user.getUsername()), PDFBook.class)
                        .build();
        adapter = new PDFBookRecyclerAdapter(view.getContext(), options);
        adapter.setFragment(this);

        etUsername.setText(user.getUsername());
        etEmail.setText(user.getEmail());

        rvMyPdfbooks.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMyPdfbooks.setHasFixedSize(true);

        rvMyPdfbooks.setAdapter(adapter);
        ////FOR IMAGEBOOKS LIST


        referenceForImage = FirebaseDatabase.getInstance().getReference().child("imagebooks");

         //Build the query
        Query query = referenceForImage.orderByChild("owner").equalTo(user.getUsername());

        // Get the query string
        String queryString = query.toString();

        // Print the query string to the console
        Log.d("Firebase Query", "Query: " + queryString);

        // Now you can execute the query and handle the results
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshotting : dataSnapshot.getChildren()) {
                    ImageBook BOOK= snapshotting.getValue(ImageBook.class);
                    if (BOOK != null) {
                        // Handle the retrieved user object
                        //System.out.println("key :"+ snapshotting.getKey());
                        System.out.println("BOOK NAME: " + BOOK.getName());
                        System.out.println("Email: " + BOOK.getOwner());
                        System.out.println("BOOK DESC: " + BOOK.getDesc());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        rvMyImageBooks.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        rvMyImageBooks.setHasFixedSize(true);
        FirebaseRecyclerOptions<ImageBook> options2 =
                new FirebaseRecyclerOptions.Builder<ImageBook>()
                        .setQuery(reference.orderByChild("owner").equalTo(user.getUsername()), ImageBook.class)
                        .build();
        imageBookRecyclerAdapter = new ImageBookRecyclerAdapter( options2,view.getContext());
        imageBookRecyclerAdapter.setFragment(this);

        rvMyImageBooks.setAdapter(imageBookRecyclerAdapter);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(view.getContext(), AddPdfBook.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        btImageBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(view.getContext(), AddImageBook.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        System.out.println("PROFILE FRAG ONCREATEVIEW");

        return view;
    }
}