package com.amad.btest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amad.btest.Entity.Product;
import com.amad.btest.Utils.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 9/11/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterHolder> {

    private ArrayList<Product> mProducts;
    private LayoutInflater inflater;
    private int mRecourseID;
    private Context mContext;
    private int lastPosition = -1;





    public void clear() {
        int size = this.mProducts.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {

                this.mProducts.remove(0);

                this.notifyItemRemoved(i);
            }

            //this.notifyDataSetChanged();
            //this.notifyItemRemoved(0, size);
        }
    }

    public void SetProducts(ArrayList<Product> products){
        this.mProducts = products;
    }

    public ProductAdapter( Context context, int recourseID)
    {
        this.inflater = LayoutInflater.from(context);
        //this.mProducts = products;
        this.mRecourseID = recourseID;
        this.mContext = context;

    }

    @Override
    public ProductAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(this.mRecourseID,parent,false);
        return new ProductAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapterHolder holder, int position) {


        Product product = mProducts.get(position);
        holder.name.setText(product.getName());
        holder.price.setText("$"+  product.getPrice());
        holder.discount.setText(product.getDiscount() + "% OFF");
        holder.region.setText(product.getRegion());
        String item_color = "#FFC107";
        if(product.getRegion().equals("produce"))
        {
            item_color = "#8BC34A";
        }else if(product.getRegion().equals("lifestyle")){
            item_color = "#2196F3";
        }

        String url= Helper.URL + product.getPhoto();
        Picasso.with(mContext).load(url).placeholder(R.mipmap.empty).into(holder.icon);
        holder.icon_Back.setBackgroundColor(Color.parseColor(item_color));
       // setFadeAnimation(holder.itemView, position);
    }



    private void setFadeAnimation(View view, int position) {
        if (position > lastPosition) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500);
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void setScaleAnimation(View view, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(500);
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }


    class ProductAdapterHolder extends RecyclerView.ViewHolder
    {
        private TextView name,price,discount,region;
        private ImageView icon;
        private View container,icon_Back;

        public ProductAdapterHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            price = (TextView) itemView.findViewById(R.id.txt_price);
            discount = (TextView) itemView.findViewById(R.id.txt_discount);
            region = (TextView) itemView.findViewById(R.id.txt_region);
            icon = (ImageView) itemView.findViewById(R.id.img_product);
            container = itemView.findViewById(R.id.item_Layout);
            icon_Back = itemView.findViewById(R.id.icon_back);
        }
    }
}
