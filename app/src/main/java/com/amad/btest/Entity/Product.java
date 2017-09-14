package com.amad.btest.Entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pushparajparab on 9/11/17.
 */

public class Product implements Parcelable {

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", discount=" + discount +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", region='" + region + '\'' +
                '}';
    }

    private int id,discount;
    private double price;
    private String name,photo,region;

    public Product(){};
    protected Product(Parcel in) {
        id = in.readInt();
        discount = in.readInt();
        price = in.readDouble();
        name = in.readString();
        photo = in.readString();
        region = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(discount);
        dest.writeDouble(price);
        dest.writeString(name);
        dest.writeString(photo);
        dest.writeString(region);
    }
}
