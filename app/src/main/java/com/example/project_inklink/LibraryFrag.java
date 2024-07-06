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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private TextView tvFragName,tvEmptyLibrary;
    private ImageView ivback;
    RecyclerView pdfRecyclerView,imageRecyclerView;
    AllAdapter pdfadapter,imagebookadapter;
    ArrayList<Book> pdfbooks=new ArrayList<>();
    ArrayList<Book> imagebooks=new ArrayList<>();
    Library library;
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
        tvEmptyLibrary=view.findViewById(R.id.tvEmptyLibrary);
        tvFragName=view.findViewById(R.id.toolbartitle);
        tvFragName.setText("Library");
        pdfRecyclerView =view.findViewById(R.id.rvlibpdf);
        imageRecyclerView=view.findViewById(R.id.rvlibImagebooks);
        pdfRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        pdfRecyclerView.setHasFixedSize(true);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        imageRecyclerView.setHasFixedSize(true);
        //also add the recycler view for the other type of books
        if (bundle != null) {
            user= (User) bundle.getSerializable("user");
            // Now you can use the receivedData in your fragment
        }
        else {
            System.out.println("BUNDLE EMPTY IN PROFILE FRAG");
        }
        DatabaseReference libraryRef = FirebaseDatabase.getInstance().getReference().child("library").child(user.getUsername());
        //CHECK IF THIS RUNS THE FIRST TIME ONLY OR IF IT ONLY RUNS WHEN LIBRARY
        ValueEventListener valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> readsList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String bookId = (String) snapshot.getValue();
                        readsList.add(bookId);
                    }
                    if (!readsList.isEmpty()) {
                        imagebooks.clear();
                        pdfbooks.clear();
                        DatabaseReference fetchBooks = FirebaseDatabase.getInstance().getReference();
                        for (String bookId : readsList) {
                            fetchBooks.child("imagebooks").child(bookId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ImageBook imageBook = dataSnapshot.getValue(ImageBook.class);
                                    if (imageBook != null) {
                                        imagebooks.add(imageBook);
                                        System.out.println("Image book "+imageBook.getName()+" "+imageBook.getOwner());
                                    }

                                    if(readsList.size()==pdfbooks.size()+ imagebooks.size())
                                    {
                                        setAdapters(view);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.err.println("Error fetching image book: " + databaseError.getMessage());


                                    if(readsList.size()==pdfbooks.size()+ imagebooks.size())
                                    {
                                        setAdapters(view);
                                    }
                                }
                            });

                        }
                        for (String bookId : readsList) {
                            fetchBooks.child("pdfbooks").child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    PDFBook pdfBook = dataSnapshot.getValue(PDFBook.class);
                                    if (pdfBook != null) {
                                        pdfbooks.add(pdfBook);
                                        System.out.println("Pdf book "+pdfBook.getName()+" "+pdfBook.getOwner());
                                    }
                                    if(readsList.size()==pdfbooks.size()+ imagebooks.size())
                                    {
                                        setAdapters(view);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.err.println("Error fetching image book: " + databaseError.getMessage());
                                    if(readsList.size()==pdfbooks.size()+ imagebooks.size())
                                    {
                                        setAdapters(view);
                                    }
                                }
                            });

                        }


                    } else {
                        tvEmptyLibrary.setVisibility(View.VISIBLE);
                        System.out.println("List is empty");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(view.getContext(), "Failed to check reads", Toast.LENGTH_SHORT).show();
            }
        };
        libraryRef.child("reads").addValueEventListener(valueEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }
    public void setAdapters(View view)
    {
        System.out.println("THE LIST PDF BOOKS");
        for(Book book:pdfbooks)
        {
            System.out.println("pdf Book"+book.getName()+" "+book.getOwner());
        }
        System.out.println("THE LIST IMAGE BOOKS");
        for(Book book:imagebooks)
        {
            System.out.println("imagebook"+book.getName()+" "+book.getOwner());
        }
        imagebookadapter=new AllAdapter(imagebooks, view.getContext(), user.getUsername());
        pdfadapter=new AllAdapter(pdfbooks, view.getContext(), user.getUsername());

        imageRecyclerView.setAdapter(imagebookadapter);
        pdfRecyclerView.setAdapter(pdfadapter);

    }
}