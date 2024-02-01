package com.example.readtrack.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.model.Books;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int BOOKS_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onBooksItemClick(String id);
    }
    private Map<String, String> bookList;
    private List<String> chiavi;

    public HashMapRecyclerViewAdapter(HashMap<String,String> bookList, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.bookList=bookList;
        this.chiavi= new ArrayList<>(this.bookList.keySet());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);
        return new BooksViewHolder(view);
    }

    public void setBookList(HashMap<String, String> newBookList) {
        this.bookList.clear();
        this.bookList.putAll(newBookList);
        this.chiavi = new ArrayList<>(newBookList.keySet());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HashMapRecyclerViewAdapter.ReadingBooksViewHolder) {
            //((ReadingBooksViewHolder) holder).bind(bookList.get(chiavi.get(position)), bookList.);
        } else if (holder instanceof HashMapRecyclerViewAdapter.BooksViewHolder) {
            ((BooksViewHolder) holder).bind(bookList.get(chiavi.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        if (bookList != null) {
            return bookList.size();
        }
        return 0;
    }

    public class ReadingBooksViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView imageViewThumbnail;
        private final TextView title;
        private final TextView percentage;
        private final LinearProgressIndicator progress;

        public ReadingBooksViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail=itemView.findViewById(R.id.image_cover);
            title=itemView.findViewById(R.id.title);
            percentage=itemView.findViewById(R.id.reading_percentage);
            progress=itemView.findViewById(R.id.linearProgressIndicator);
            itemView.setOnClickListener(this);
        }

        public void bind(String link, String title, int percent) {
            try {
                Picasso.get()
                        .load(link)
                        .into(imageViewThumbnail);
            } catch (NullPointerException pointerException) {
                Log.d("pointer exception", pointerException.toString());
            }
            this.title.setText(title);
            percentage.setText("Percentuale di lettura: "+percent);
            progress.setProgress(percent);
        }

        @Override
        public void onClick(View v) {

        }
    }



    public class BooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageViewThumbnail;

        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.image_cover);
            itemView.setOnClickListener(this);
        }

        public void bind(String link) {
            try {
                Picasso.get()
                        .load(link)
                        .into(imageViewThumbnail);
            } catch (NullPointerException pointerException) {
                Log.d("pointer exception", pointerException.toString());
            }
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onBooksItemClick(chiavi.get(getAdapterPosition()));
        }
    }
}
