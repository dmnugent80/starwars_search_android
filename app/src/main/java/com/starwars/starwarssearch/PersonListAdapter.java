package com.starwars.starwarssearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

/**
 * Adapter class for the Person RecyclerView
 */

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.MovieViewHolder> {
    private List<Person> mPersonList;
    private PersonClickCallback mPersonClickListener;

    public interface PersonClickCallback {
        void onClick(Person person);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mName, mYearBorn;
        public MovieViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mName = view.findViewById(R.id.tv_name);
            mYearBorn = view.findViewById(R.id.tv_year);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Person person = mPersonList.get(position);
            if (person != null && mPersonClickListener != null) {
                mPersonClickListener.onClick(person);
            }
        }
    }

    public PersonListAdapter(List<Person> personList, PersonClickCallback listener) {
        mPersonList = personList;
        mPersonClickListener = listener;
    }

    @Override
    public PersonListAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        Person person = mPersonList.get(position);
        holder.mName.setText(person.mName);
        holder.mYearBorn.setText(person.mBirthYear);
    }


    @Override
    public int getItemCount() {
        return mPersonList.size();
    }
}
