package com.example.googlemaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<model> listItems;
    private Context context;

    public MyAdapter(Context context, ArrayList<model> listItems) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        model listItem = listItems.get(position);
        holder.name.setText(listItem.getName());
        //holder.type.setText(listItem.getType());
        holder.rating.setText(listItem.getRating());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        //TextView type;
        TextView rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            //type = itemView.findViewById(R.id.type);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
