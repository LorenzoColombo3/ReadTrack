package com.example.readtrack.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.model.Books;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int BOOKS_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;
    public interface OnItemClickListener {
        void onBooksItemClick(Books book);
    }

    private final List<Books> booksList;
    private final OnItemClickListener onItemClickListener;

    public BooksRecyclerViewAdapter(List<Books> booksList, OnItemClickListener onItemClickListener) {
        this.booksList = booksList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (booksList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return BOOKS_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        if (viewType == BOOKS_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.book_list_item, parent, false);
            return new BooksViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.book_loading_item, parent, false);
            return new LoadingBooksViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BooksViewHolder) {
            ((BooksViewHolder) holder).bind(booksList.get(position));
        } else if (holder instanceof LoadingBooksViewHolder) {
            ((LoadingBooksViewHolder) holder).activate();
        }
    }

    @Override
    public int getItemCount() {
        if (booksList != null) {
            return booksList.size();
        }
        return 0;
    }

    public void setBookList(List<Books> booksList) {
        this.booksList.clear();
        this.booksList.addAll(booksList);
    }

    public class BooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageViewThumbnail;

        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.image_cover);
            itemView.setOnClickListener(this);
        }

        public void bind(Books book) {
            try {
                Picasso.get()
                        .load( "https"+book.getVolumeInfo().getImageLinks().getThumbnail().substring(4))
                        .error(R.drawable.image_not_found)
                        .into(imageViewThumbnail);
            } catch (NullPointerException pointerException) {
                Log.d("pointer exception", pointerException.toString());
            }
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onBooksItemClick(booksList.get(getAdapterPosition()));
        }
    }

    public static class LoadingBooksViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;

        LoadingBooksViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressbar_loading_books);
        }

        public void activate() {
            progressBar.setIndeterminate(true);
        }
    }
}