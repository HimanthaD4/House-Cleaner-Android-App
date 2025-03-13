package com.example.housecleaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.HouseViewHolder> {

    private List<House> houseList;
    private OnHouseListener onHouseListener;

    public HouseAdapter(List<House> houseList, OnHouseListener onHouseListener) {
        this.houseList = houseList;
        this.onHouseListener = onHouseListener;
    }

    @NonNull
    @Override
    public HouseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_house, parent, false);
        return new HouseViewHolder(view, onHouseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseViewHolder holder, int position) {
        House house = houseList.get(position);

        // Set house details
        holder.location.setText(house.getLocation());
        holder.status.setText(house.getStatus());
        holder.tvRoomsBathrooms.setText("Rooms: " + house.getRooms() + ", Bathrooms: " + house.getBathrooms());
        holder.tvFloorType.setText("Floor Type: " + house.getFloorType());
        holder.tvContact.setText("Contact: " + house.getContact());

        // Load house image
        if (house.getImage() != null && !house.getImage().isEmpty()) {
            byte[] decodedString = Base64.decode(house.getImage(), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.ivHouseImage.setImageBitmap(decodedBitmap);
        } else {
            holder.ivHouseImage.setImageResource(R.drawable.ic_placeholder); // Placeholder image
        }

        // Handle delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (onHouseListener != null) {
                onHouseListener.onHouseDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return houseList.size();
    }

    public House getHouseAt(int position) {
        return houseList.get(position);
    }

    public void removeHouse(int position) {
        houseList.remove(position);
        notifyItemRemoved(position);
    }

    public static class HouseViewHolder extends RecyclerView.ViewHolder {

        TextView location, status, tvRoomsBathrooms, tvFloorType, tvContact;
        ImageView ivHouseImage, btnDelete;

        public HouseViewHolder(@NonNull View itemView, OnHouseListener onHouseListener) {
            super(itemView);
            location = itemView.findViewById(R.id.location);
            status = itemView.findViewById(R.id.status);
            tvRoomsBathrooms = itemView.findViewById(R.id.tvRoomsBathrooms);
            tvFloorType = itemView.findViewById(R.id.tvFloorType);
            tvContact = itemView.findViewById(R.id.tvContact);
            ivHouseImage = itemView.findViewById(R.id.ivHouseImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnHouseListener {
        void onHouseDelete(int position);
    }
}