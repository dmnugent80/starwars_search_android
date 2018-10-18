package com.starwars.starwarssearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter class for the Film RecyclerView
 */

public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.FilmViewHolder> {
    private List<Film> mFilmList;
    private FilmClickCallback mFilmClickListener;

    public interface FilmClickCallback {
        void onClick(Film film);
    }

    public class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        public FilmViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mTitle = view.findViewById(R.id.tv_title);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Film film = mFilmList.get(position);
            if (film != null && mFilmClickListener != null) {
                mFilmClickListener.onClick(film);
            }
        }
    }

    public FilmListAdapter(List<Film> filmList, FilmClickCallback listener) {
        mFilmList = filmList;
        mFilmClickListener = listener;
    }

    @Override
    public FilmListAdapter.FilmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FilmViewHolder holder, final int position) {
        Film film = mFilmList.get(position);
        holder.mTitle.setText(film.mTitle);
    }


    @Override
    public int getItemCount() {
        return mFilmList.size();
    }
}
