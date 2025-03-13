package com.example.housecleaner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {


    private List<Comment> comments;

    public CommentAdapter() {
        this.comments = new ArrayList<>();
    }

    public CommentAdapter(List<Comment> comments) {
        if (comments != null) {
            this.comments = comments;
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvCommentText.setText(comment.getCommentText());
        holder.tvTimestamp.setText(comment.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void updateComments(List<Comment> newComments) {
        comments.clear();
        comments.addAll(newComments);
        notifyDataSetChanged();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentText, tvTimestamp;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}