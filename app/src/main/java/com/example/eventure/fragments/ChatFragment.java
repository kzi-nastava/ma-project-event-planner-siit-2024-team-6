package com.example.eventure.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.adapters.ChatAdapter;

public class ChatFragment extends Fragment {
    private static final String ARG_CHAT_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_IMAGE = "image";

    public static ChatFragment newInstance(int id, String name, String image) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHAT_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        TextView nameView = view.findViewById(R.id.chat_name);
        ImageView profileImage = view.findViewById(R.id.chat_image);
        RecyclerView chatRecycler = view.findViewById(R.id.chat_recycler);

        String name = getArguments().getString(ARG_NAME);
        String imageRes = getArguments().getString(ARG_IMAGE);
        nameView.setText(name);

        Glide.with(this)
                .load(imageRes)
                .placeholder(R.drawable.placeholder_image) // optional: shown while loading
                .error(R.drawable.error_image)       // optional: shown if failed
                .into(profileImage);

        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecycler.setAdapter(null);

        return view;
    }
}
