package com.example.readtrack.adapter;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.model.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksRecyclerViewAdapter extends
        RecyclerView.Adapter<BooksRecyclerViewAdapter.BooksViewHolder>{

    public interface OnItemClickListener {
        void onBooksItemClick(Book book);
    }

    private final List<Book> booksList;
    private final OnItemClickListener onItemClickListener;

    public BooksRecyclerViewAdapter(List<Book> booksList, OnItemClickListener onItemClickListener) {
        this.booksList = booksList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.book_list_item, parent, false);

        return new BooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksViewHolder holder, int position) {
        holder.bind(booksList.get(position));
    }

    @Override
    public int getItemCount() {
        if (booksList != null) {
            return booksList.size();
        }
        return 0;
    }

    public class BooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView imageViewThumbnail;
        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.image_cover);
            itemView.setOnClickListener(this);
        }
        public void bind(Book book) {
            try{
                Picasso.get()
                        .load("https"+book.getVolumeInfo().getImageLinks().getThumbnail().substring(4))
                        .into(imageViewThumbnail);
            }catch(NullPointerException pointerException){
                Log.d("pointer exception", pointerException.toString());
            }
            //Log.d("uri",uri);
        }
        @Override
        public void onClick(View v) {
            onItemClickListener.onBooksItemClick(booksList.get(getAdapterPosition()));
        }
    }
}
