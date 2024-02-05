package com.example.readtrack.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readtrack.R;
import com.example.readtrack.model.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksRecyclerViewProfile extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int BOOKS_VIEW_TYPE = 0;
    private static final int BUTTON_VIEW_TYPE = 1;
    public interface OnItemClickListener {
        void onBooksItemClick(Book book);

        void onButtonItemClick();
    }

    private final List<Book> bookList;
    private final OnItemClickListener onItemClickListener;

    public BooksRecyclerViewProfile(List<Book> bookList, OnItemClickListener onItemClickListener) {
        this.bookList = bookList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 ) {
            return BUTTON_VIEW_TYPE;
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
                    inflate(R.layout.book_item_add, parent, false);
            return new AddBooksViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BooksViewHolder) {
            ((BooksViewHolder) holder).bind(bookList.get(position));
        } else if (holder instanceof AddBooksViewHolder) {
            ((AddBooksViewHolder) holder).setOnItemClickListener(onItemClickListener);;
        }
    }

    @Override
    public int getItemCount() {
        if (bookList != null) {
            return bookList.size()+1;
        }
        return 0;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList.clear();
        this.bookList.addAll(bookList);
    }

    public class BooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageViewThumbnail;

        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.image_cover);
            itemView.setOnClickListener(this);
        }

        public void bind(Book book) {
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
            onItemClickListener.onBooksItemClick(bookList.get(getAdapterPosition()));
        }
    }

    public class AddBooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageButton imageButton;
        private OnItemClickListener onItemClickListener;

        AddBooksViewHolder(View view) {
            super(view);
            imageButton = view.findViewById(R.id.image_button);
            imageButton.setOnClickListener(this);
        }

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
        @Override
        public void onClick(View v) {
            onItemClickListener.onButtonItemClick();
        }
    }
}