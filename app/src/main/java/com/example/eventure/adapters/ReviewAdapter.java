package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.dto.ReactionDTO;

import android.widget.TextView;
import android.widget.RatingBar;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<ReactionDTO> reviews;

    public ReviewAdapter(List<ReactionDTO> reviews) {
        this.reviews = reviews;
    }

    public void addReviews(List<ReactionDTO> newReviews) {
        int start = reviews.size();
        reviews.addAll(newReviews);
        notifyItemRangeInserted(start, newReviews.size());
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        ReactionDTO review = reviews.get(position);

        holder.userName.setText(review.getUserName());

        // Handle rating
        if (review.getRating() != null && review.getRating() > 0) {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(review.getRating());
        } else {
            holder.ratingBar.setVisibility(View.GONE);
        }

        // Handle review text
        if (review.getText() != null && !review.getText().trim().isEmpty()) {
            holder.reviewText.setVisibility(View.VISIBLE);
            holder.reviewText.setText(review.getText());
        } else {
            holder.reviewText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, reviewText;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_user_name);
            reviewText = itemView.findViewById(R.id.tv_review_text);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}