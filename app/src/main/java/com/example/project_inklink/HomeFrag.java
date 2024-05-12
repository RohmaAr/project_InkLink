package com.example.project_inklink;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    EditText etName, etPhone;
    DatabaseReference reference;
    FloatingActionButton fabAdd;
    RecyclerView rvBooks,rvImageBooks;
    ImageView ivback;
    TextView tvFragName;

    PDFBookRecyclerAdapter adapter;
    ImageBookRecyclerAdapter imageBookRecyclerAdapter;

    private final String KEY_PARENT = "pdfbooks";
    private final String IMAGES_PARENT="imagebooks";

    public HomeFrag() {
        // Required empty public constructor
    }
    private Bundle bundle;

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
    public static HomeFrag newInstance(String param1, String param2) {
        HomeFrag fragment = new HomeFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        reference = FirebaseDatabase.getInstance().getReference();
        rvBooks = view.findViewById(R.id.rvBooksHome);
        ivback=view.findViewById(R.id.ivtoolbarback);
        ivback.setVisibility(View.GONE);
        tvFragName=view.findViewById(R.id.toolbartitle);
        rvImageBooks=view.findViewById(R.id.rvImagesHome);
        tvFragName.setText("Home");
        rvBooks.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvBooks.setHasFixedSize(true);


        FirebaseRecyclerOptions<PDFBook> options =
                new FirebaseRecyclerOptions.Builder<PDFBook>()
                        .setQuery(reference.child(KEY_PARENT), PDFBook.class)
                        .build();
        adapter = new PDFBookRecyclerAdapter(view.getContext(), options);
        adapter.setFragment(this);
        rvBooks.setAdapter(adapter);

        rvImageBooks.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvImageBooks.setHasFixedSize(true);
        FirebaseRecyclerOptions<ImageBook> options2 =
                new FirebaseRecyclerOptions.Builder<ImageBook>()
                        .setQuery(reference.child(IMAGES_PARENT), ImageBook.class)
                        .build();
        imageBookRecyclerAdapter = new ImageBookRecyclerAdapter(options2,view.getContext());
        imageBookRecyclerAdapter.setFragment(this);
        rvImageBooks.setAdapter(imageBookRecyclerAdapter);


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}