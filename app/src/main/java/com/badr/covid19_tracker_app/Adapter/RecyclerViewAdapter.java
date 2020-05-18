package com.badr.covid19_tracker_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.badr.covid19_tracker_app.DetailsActivity;
import com.badr.covid19_tracker_app.R;
import com.badr.covid19_tracker_app.Model.countryModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<countryModel> countryModelList;


    public RecyclerViewAdapter(Context context, List<countryModel> countryModelList) {
        this.context = context;
        this.countryModelList = countryModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating layout from res to create a View object
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_item, parent, false);
        //passing the view object to the ViewHolder class
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        //getting one countryModel from the list of countries to populate it with data
        countryModel countryModel = countryModelList.get(position);

        //setting the country name
        viewHolder.countryName.setText(countryModel.getCountry());

        //using picasso library to download and set the country image
        Picasso.get().load(countryModel.getFlag()).into(viewHolder.countryImage);
    }



    @Override
    public int getItemCount() {
        return countryModelList.size();
    }


    //using the new filtered list
    public void filterList(ArrayList<countryModel> filteredList) {
        countryModelList = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView countryName;
        public ImageView countryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //setting a click listener on the hole itemView
            itemView.setOnClickListener(this);

            //finding the views in the View object passed
            countryName = itemView.findViewById(R.id.countryName);
            countryImage = itemView.findViewById(R.id.countryImage);

        }

        //called when an item is clicked
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            countryModel countryModel = countryModelList.get(position);
            Log.d("item Clicked","onClick " + countryModel.getCountry());

            Intent intent = new Intent(context, DetailsActivity.class).putExtra("countryName", countryModel.getCountry());
            context.startActivity(intent);

        }
    }
}
