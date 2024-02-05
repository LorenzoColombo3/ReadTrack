package com.example.readtrack.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.model.Books;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BooksFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final OnItemClickListener onItemClickListener;
    private List<Books> bookList;

    public BooksFragmentRecyclerViewAdapter(List<Books> bookList, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.bookList=bookList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_reading_item, parent, false);
        return new ReadingBooksViewHolder(view);
    }

    public void setBookList(List<Books> newBookList) {
        this.bookList = new ArrayList<>(newBookList);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Books book = bookList.get(position);
        if(book.getVolumeInfo() == null) {
            ((ReadingBooksViewHolder) holder).bind(null, book.getTitleBookDB(), book.getBookMarker(), 0);
        }else{
            float percent = (float) book.getBookMarker() / book.getVolumeInfo().getPageCount() * 100;
            ((ReadingBooksViewHolder) holder).bind(book.getVolumeInfo().getImageLinks().getThumbnail(), book.getVolumeInfo().getTitle(), book.getBookMarker() , (int) percent);
        }
    }

    @Override
    public int getItemCount() {
        if (bookList != null) {
            return bookList.size();
        }
        return 0;
    }

    public interface OnItemClickListener {
        void onBooksItemClick(String id);
    }

    public class ReadingBooksViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView imageViewThumbnail;
        private final TextView titolo;
        private final TextView percentage;
        private final LinearProgressIndicator progress;

        public ReadingBooksViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail=itemView.findViewById(R.id.image_cover);
            titolo = itemView.findViewById(R.id.title);
            percentage=itemView.findViewById(R.id.reading_percentage);
            progress=itemView.findViewById(R.id.linearProgressIndicator);
            itemView.setOnClickListener(this);
        }

        public void bind(String link, String title, int pagina, int percent) {
            try {
                Picasso.get()
                        .load( "https"+link.substring(4))
                        .error(R.drawable.image_not_found)
                        .into(imageViewThumbnail);
            } catch (NullPointerException pointerException) {
                Log.d("pointer exception", pointerException.toString());
            }
            this.titolo.setText(title);
            percentage.setText("Segnalibro: pagina "+String.valueOf(pagina));
            progress.setProgress(percent);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onBooksItemClick(bookList.get(getAdapterPosition()).getId());
        }
    }

}
