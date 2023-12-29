package com.example.readtrack.adapter;

import android.app.Application;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksSearchRecyclerAdapter extends
        RecyclerView.Adapter<BooksSearchRecyclerAdapter.BooksSearchViewHolder>{

    public interface OnItemClickListener {
        void onBooksItemClick(Books book);
    }

    private final List<Books> booksList;

    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public BooksSearchRecyclerAdapter(List<Books> booksList, Application application, OnItemClickListener onItemClickListener) {
        this.application=application;
        this.booksList = booksList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BooksSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.book_search_list_item, parent, false);

        return new BooksSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksSearchViewHolder holder, int position) {
        holder.bind(booksList.get(position));
    }

    @Override
    public int getItemCount() {
        if (booksList != null) {
            return booksList.size();
        }
        return 0;
    }
    public class BooksSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView imageViewThumbnail;
        private final TextView title;
        private final TextView author;

        public BooksSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.image_cover);
            title = itemView.findViewById(R.id.book_title);
            author = itemView.findViewById(R.id.book_author);
            itemView.setOnClickListener(this);
        }
        public void bind(Books book) {
            try{
                Picasso.get()
                        .load("https"+book.getVolumeInfo().getImageLinks().getThumbnail().substring(4))
                        .into(imageViewThumbnail);
                title.setText(setContent(book.getVolumeInfo().getTitle()));
                author.setText(setContent(book.getVolumeInfo().getAuthors().get(0)));
            }catch(NullPointerException pointerException){
                Log.d("pointer exception", pointerException.toString());
            }
            //Log.d("uri",uri);
        }
        public String setContent(String content){
            String contentRidotto="";
            final int MAX_CHAR=30;
            if(content.length()>MAX_CHAR){
                contentRidotto=content.substring(0,MAX_CHAR)+"...";
            }else{
                return content;
            }
            return contentRidotto;
        }
        @Override
        public void onClick(View v) {
            onItemClickListener.onBooksItemClick(booksList.get(getAdapterPosition()));
        }
/*
        private void setImageViewFavoriteBooks(boolean isFavorite) {
            if (isFavorite) {
                like.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_24));
                like.setColorFilter(
                        ContextCompat.getColor(
                                like.getContext(),
                                R.color.red_500)
                );
            } else {
                like.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_border_24));
                like.setColorFilter(
                        ContextCompat.getColor(
                                like.getContext(),
                                R.color.black)
                );
            }
        }*/
    }
}
