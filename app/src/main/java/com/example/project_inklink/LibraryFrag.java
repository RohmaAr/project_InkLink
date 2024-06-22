package com.example.project_inklink;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Bundle bundle;
    User user;
    private TextView tvFragName;
    private ImageView ivback;
    RecyclerView recyclerView;
    AllAdapter adapter;
    ArrayList<Book> books;
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
    public LibraryFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFrag newInstance(String param1, String param2) {
        LibraryFrag fragment = new LibraryFrag();
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
        ivback=view.findViewById(R.id.ivtoolbarback);
        ivback.setVisibility(View.GONE);
        tvFragName=view.findViewById(R.id.toolbartitle);
        tvFragName.setText("Library");
        recyclerView=view.findViewById(R.id.rvlib);
        if (bundle != null) {
            user= (User) bundle.getSerializable("user");
            // Now you can use the receivedData in your fragment
        }
        else {
            System.out.println("BUNDLE EMPTY IN PROFILE FRAG");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }
}