package com.example.housecleaner;

import android.os.Parcel;
import android.os.Parcelable;

public class House implements Parcelable {
    private int id;
    private int userId;
    private String location;
    private String rooms;
    private String bathrooms;
    private String floorType;
    private String contact;
    private String image;
    private String status;
    private String price; // Added price field

    public House(String location, String rooms, String bathrooms, String floorType, String contact, String image) {
        this.location = location;
        this.rooms = rooms;
        this.bathrooms = bathrooms;
        this.floorType = floorType;
        this.contact = contact;
        this.image = image;
        this.status = "available";
    }

    protected House(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        location = in.readString();
        rooms = in.readString();
        bathrooms = in.readString();
        floorType = in.readString();
        contact = in.readString();
        image = in.readString();
        status = in.readString();
        price = in.readString(); // Read price from Parcel
    }

    public static final Creator<House> CREATOR = new Creator<House>() {
        @Override
        public House createFromParcel(Parcel in) {
            return new House(in);
        }

        @Override
        public House[] newArray(int size) {
            return new House[size];
        }
    };

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getRooms() { return rooms; }
    public void setRooms(String rooms) { this.rooms = rooms; }
    public String getBathrooms() { return bathrooms; }
    public void setBathrooms(String bathrooms) { this.bathrooms = bathrooms; }
    public String getFloorType() { return floorType; }
    public void setFloorType(String floorType) { this.floorType = floorType; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrice() { return price; } // Getter for price
    public void setPrice(String price) { this.price = price; } // Setter for price

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeString(location);
        dest.writeString(rooms);
        dest.writeString(bathrooms);
        dest.writeString(floorType);
        dest.writeString(contact);
        dest.writeString(image);
        dest.writeString(status);
        dest.writeString(price); // Write price to Parcel
    }
}