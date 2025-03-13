package com.example.housecleaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.HouseViewHolder> {

    private List<House> houseList;
    private OnHouseListener onHouseListener;
    private DatabaseHelper dbHelper;

    public HouseAdapter(List<House> houseList, OnHouseListener onHouseListener, DatabaseHelper dbHelper) {
        this.houseList = houseList;
        this.onHouseListener = onHouseListener;
        this.dbHelper = dbHelper;
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
        holder.tvPrice.setText("LKR " + house.getPrice()); // Display price

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

        // Handle toggle status button click
        holder.btnToggleStatus.setOnClickListener(v -> {
            String newStatus = house.getStatus().equals("available") ? "accepted" : "available";
            dbHelper.updateHouseStatus(house.getId(), newStatus); // Update status in DB
            house.setStatus(newStatus); // Update status in the list
            notifyItemChanged(position); // Refresh the item
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

    static class HouseViewHolder extends RecyclerView.ViewHolder {

        TextView location, status, tvRoomsBathrooms, tvFloorType, tvContact, tvPrice;
        ImageView ivHouseImage;
        Button btnDelete, btnToggleStatus;

        public HouseViewHolder(@NonNull View itemView, OnHouseListener onHouseListener) {
            super(itemView);
            location = itemView.findViewById(R.id.location);
            status = itemView.findViewById(R.id.status);
            tvRoomsBathrooms = itemView.findViewById(R.id.tvRoomsBathrooms);
            tvFloorType = itemView.findViewById(R.id.tvFloorType);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivHouseImage = itemView.findViewById(R.id.ivHouseImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus); // Initialize toggle button
        }
    }

    public interface OnHouseListener {
        void onHouseDelete(int position);
    }
}