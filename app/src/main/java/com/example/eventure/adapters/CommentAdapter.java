package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.dto.ReactionDTO;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<ReactionDTO> comments = new ArrayList<>();
    private OnItemActionListener actionListener;

    public interface OnItemActionListener {
        void onApprove(ReactionDTO comment);
        void onDelete(ReactionDTO comment);
    }

    public CommentAdapter(OnItemActionListener listener) {
        this.actionListener = listener;
    }

    public void setComments(List<ReactionDTO> newComments) {
        comments = newComments;
        notifyDataSetChanged();
    }

    public void addComments(List<ReactionDTO> newComments) {
        int startPos = comments.size();
        comments.addAll(newComments);
        notifyItemRangeInserted(startPos, newComments.size());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_card, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentText, tvRating;
        Button btnApprove, btnDelete;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvRating = itemView.findViewById(R.id.tvRating);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(ReactionDTO comment) {
            tvCommentText.setText(comment.getText());
            if (comment.getRating() != null) {
                tvRating.setText("Rating: "+comment.getRating() + "/5");
            } else {
                tvRating.setText("");
            }            btnApprove.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onApprove(comment);
            });
            btnDelete.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onDelete(comment);
            });
        }
    }
}
