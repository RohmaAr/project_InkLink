package com.example.project_inklink;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Bundle bundle;
   private ImageView ivback;
   TextView tvFragName;
   User user;
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
    public SearchFrag() {
        // Required empty public constructor
    }
  private SearchView searchView;
    RecyclerView recyclerView;
    ArrayList<Book> books;
    RelativeLayout layout;
    AllAdapter adapter;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private Button btnThriller, btnInformative, btnHorror, btnDrama, btnFantasy, btnSciFi,
            btnSuspense, btnSelfHelp, btnPhilosophy, btnFiction, btnHistory, btnPolitics, btnTechnology;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivback=view.findViewById(R.id.ivtoolbarback);
        ivback.setVisibility(View.GONE);
        tvFragName=view.findViewById(R.id.toolbartitle);
        tvFragName.setText("Search");
        btnThriller = view.findViewById(R.id.btnThriller);
        recyclerView=view.findViewById(R.id.rvListSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        btnInformative = view.findViewById(R.id.btnInformative);
        btnHorror = view.findViewById(R.id.btnHorror);
        btnDrama = view.findViewById(R.id.btnDrama);
        btnFantasy = view.findViewById(R.id.btnFantasy);
        btnSciFi = view.findViewById(R.id.btnSciFi);
        btnSuspense = view.findViewById(R.id.btnSuspense);
        btnSelfHelp = view.findViewById(R.id.btnSelfHelp);
        btnPhilosophy = view.findViewById(R.id.btnPhilosophy);
        btnFiction = view.findViewById(R.id.btnFiction);
        btnHistory = view.findViewById(R.id.btnHistory);
        btnPolitics = view.findViewById(R.id.btnPolitics);
        btnTechnology = view.findViewById(R.id.btnTechnology);
        layout=view.findViewById(R.id.btnsGenres);

        if (bundle != null) {
            user= (User) bundle.getSerializable("user");
            // Now you can use the receivedData in your fragment
        }
        else {
            System.out.println("BUNDLE EMPTY IN PROFILE FRAG");
        }
        assert user != null;

        // Initialize search view
        searchView = view.findViewById(R.id.searchInput);

        books = new ArrayList<>();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);

            }
        });
        View.OnClickListener genreClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(view.getContext(), GenreSearchActivity.class);
                intent.putExtra("username",user.getUsername());
                String genre = ""; // Initialize the genre variable
                if (v.getId() == R.id.btnThriller) {
                    genre = "Thriller";
                } else if (v.getId() == R.id.btnInformative) {
                    genre = "Informative";
                } else if (v.getId() == R.id.btnHorror) {
                    genre = "Horror";
                } else if (v.getId() == R.id.btnDrama) {
                    genre = "Drama";
                } else if (v.getId() == R.id.btnFantasy) {
                    genre = "Fantasy";
                } else if (v.getId() == R.id.btnSciFi) {
                    genre = "Sci-Fi";
                } else if (v.getId() == R.id.btnSuspense) {
                    genre = "Suspense";
                } else if (v.getId() == R.id.btnSelfHelp) {
                    genre = "SelfHelp";
                } else if (v.getId() == R.id.btnPhilosophy) {
                    genre = "Philosophy";
                } else if (v.getId() == R.id.btnFiction) {
                    genre = "Fiction";
                } else if (v.getId() == R.id.btnHistory) {
                    genre = "History";
                } else if (v.getId() == R.id.btnPolitics) {
                    genre = "Politics";
                } else if (v.getId() == R.id.btnTechnology) {
                    genre = "Technology";
                } else {
                    // Handle unexpected clicks or default case
                }
// Now you can use the genre variable as needed, for example, passing it as an extra in an Intent
                intent.putExtra("genre", genre);

                startActivity(intent);
            }
        };

        // Set click listener for all buttons
        btnThriller.setOnClickListener(genreClickListener);
        btnInformative.setOnClickListener(genreClickListener);
        btnHorror.setOnClickListener(genreClickListener);
        btnDrama.setOnClickListener(genreClickListener);
        btnFantasy.setOnClickListener(genreClickListener);
        btnSciFi.setOnClickListener(genreClickListener);
        btnSuspense.setOnClickListener(genreClickListener);
        btnSelfHelp.setOnClickListener(genreClickListener);
        btnPhilosophy.setOnClickListener(genreClickListener);
        btnFiction.setOnClickListener(genreClickListener);
        btnHistory.setOnClickListener(genreClickListener);
        btnPolitics.setOnClickListener(genreClickListener);
        btnTechnology.setOnClickListener(genreClickListener);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                books.clear();
                reference.child("pdfbooks").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            PDFBook pdfBook = snapshot.getValue(PDFBook.class);
                            if(pdfBook != null && pdfBook.getName().contains(newText))
                            {
                                books.add(pdfBook);
                            }
                        }
                        // Fetch ImageBooks from the imagebooks collection
                        reference.child("imagebooks").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ImageBook imageBook = snapshot.getValue(ImageBook.class);
                                    if (imageBook != null && imageBook.getName().contains(newText)) {
                                        books.add(imageBook);
                                    }
                                }
                                // Initialize and set the adapter with the populated books ArrayList
                                adapter = new AllAdapter(books, getContext(), user.getUsername());
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                recyclerView.setVisibility(View.VISIBLE); for(Book book : books)
                                {
                                    System.out.println(book.getName()+" "+book.getOwner());
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle database error
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return true;
            }
        });
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFrag newInstance(String param1, String param2) {
        SearchFrag fragment = new SearchFrag();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

}