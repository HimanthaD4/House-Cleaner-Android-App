package com.example.housecleaner;

public class Comment {
    private final String commentText;
    private final String timestamp;

    public Comment(String commentText, String timestamp) {
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getTimestamp() {
        return timestamp;
    }
}