package com.example.readtrack.adapter;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.model.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksRecyclerViewAdapter extends
        RecyclerView.Adapter<BooksRecyclerViewAdapter.NewViewHolder>{

    public interface OnItemClickListener {
        void onNewsItemClick(Book news);
        void onFavoriteButtonPressed(int position);
    }

    private final List<Book> booksList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public BooksRecyclerViewAdapter(List<Book> booksList, Application application,
                                   OnItemClickListener onItemClickListener) {
        this.booksList = booksList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.book_list_item, parent, false);

        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
        holder.bind(booksList.get(position));
    }

    @Override
    public int getItemCount() {
        if (booksList != null) {
            return booksList.size();
        }
        return 0;
    }

    public class NewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView imageViewThumbnail;
        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.image_cover);
            itemView.setOnClickListener(this);
            imageViewThumbnail.setOnClickListener(this);
        }
        public void bind(Book book) {
            String uri=book.getThumbnailURL();
            Picasso.get()
                    .load(uri)
                    .into(imageViewThumbnail);
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image_cover) {
                Log.d("uriCover","uriCover");
            } else {

            }
        }
    }
}
