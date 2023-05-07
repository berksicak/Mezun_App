package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private AnnouncementsAdapter adapter;
    private List<Announcement> announcementList;
    private CollectionReference collectionReference;
    private FloatingActionButton fab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fab = view.findViewById(R.id.addAnnouncementFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), AddAnnouncementAcitivity.class);
                myIntent.putExtra("size", announcementList.size());
                startActivity(myIntent);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        collectionReference = FirebaseFirestore.getInstance().collection("announcements");
        Task<QuerySnapshot> query = collectionReference.get();
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    announcementList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Announcement announcement = new Announcement(document.get("description").toString(), document.get("dateTime").toString(), document.get("userName").toString(), document.get("expireDate").toString());
                        announcementList.add(announcement);
                    }
                    adapter = new AnnouncementsAdapter(announcementList, getContext());
                    recyclerView.setHasFixedSize(true);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        return view;
    }
}