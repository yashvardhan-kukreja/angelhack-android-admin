package com.ieeevit.eventoadmin.Activities.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ieeevit.eventoadmin.Classes.Session;
import com.ieeevit.eventoadmin.EventSessionsAdapter;
import com.ieeevit.eventoadmin.R;

import java.util.List;

@SuppressLint("ValidFragment")
public class MealsFragment extends Fragment {

    List<Session> mealsList;
    RecyclerView recyclerView;
    public MealsFragment(List<Session> mealsList) {
        this.mealsList = mealsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_meals, container, false);
        recyclerView = rootView.findViewById(R.id.meals_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new EventSessionsAdapter(mealsList, getContext()));

        return rootView ;
    }

}
