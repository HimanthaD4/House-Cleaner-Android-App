package com.example.housecleaner;

import android.content.Intent;
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

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<House> jobList;

    public JobAdapter(List<House> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        House house = jobList.get(position);
        holder.bind(house);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HouseDetailActivity.class);
            intent.putExtra("HOUSE_ID", house.getId()); // Pass only ID
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public House getHouseAt(int position) {
        return jobList.get(position);
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        ImageView jobImage;
        TextView location, status, rooms, bathrooms, floorType, contact, price;

        JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobImage = itemView.findViewById(R.id.ivJobImage);
            location = itemView.findViewById(R.id.location);
            status = itemView.findViewById(R.id.status);
            rooms = itemView.findViewById(R.id.tvRooms);
            bathrooms = itemView.findViewById(R.id.tvBathrooms);
            floorType = itemView.findViewById(R.id.tvFloorType);
            contact = itemView.findViewById(R.id.tvContact);
            price = itemView.findViewById(R.id.tvPrice); // Initialize price TextView
        }

        void bind(House house) {
            location.setText(house.getLocation());
            status.setText(house.getStatus());
            rooms.setText("Rooms: " + house.getRooms());
            bathrooms.setText("Bathrooms: " + house.getBathrooms());
            floorType.setText("Floor: " + house.getFloorType());
            contact.setText("Contact: " + house.getContact());
            price.setText("LKR " + house.getPrice()); // Set price

            // Load image
            if (house.getImage() != null && !house.getImage().isEmpty()) {
                byte[] decodedString = Base64.decode(house.getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                jobImage.setImageBitmap(decodedByte);
            }
        }
    }
}