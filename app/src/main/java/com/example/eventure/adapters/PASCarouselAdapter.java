package com.example.eventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.model.PAS;

import java.util.List;

public class PASCarouselAdapter extends RecyclerView.Adapter<PASCarouselAdapter.PASViewHolder> {

    private List<PAS> pasList;

    public PASCarouselAdapter(List<PAS> pasList) {
        if (pasList == null) {
            throw new IllegalArgumentException("PAS list cannot be null");
        }
        this.pasList = pasList;
    }

    @NonNull
    @Override
    public PASViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_pas_card, parent, false);
        return new PASViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PASViewHolder holder, int position) {
        PAS pas = pasList.get(position);
        holder.bind(pas);
    }

    @Override
    public int getItemCount() {
        return pasList.size();
    }

    public static class PASViewHolder extends RecyclerView.ViewHolder {

        private ImageView pasImage;
        private TextView pasTitle;
        private TextView pasDescription;
        private TextView pasPrice;

        public PASViewHolder(@NonNull View itemView) {
            super(itemView);

            pasImage = itemView.findViewById(R.id.top_pas_image);
            pasTitle = itemView.findViewById(R.id.top_pas_title);
            pasDescription = itemView.findViewById(R.id.top_pas_description);
        }

        public void bind(PAS pas) {
            pasImage.setImageResource(pas.getPhotoID());
            pasTitle.setText(pas.getTitle());
            pasDescription.setText(pas.getDescription());
        }
    }
}

