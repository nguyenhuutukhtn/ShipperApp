package com.androidev.maps.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidev.maps.Model.OrderDetail;
import com.androidev.maps.Model.OrderMainShipper;
import com.androidev.maps.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterOrderDetail extends RecyclerView.Adapter<AdapterOrderDetail.ViewHolder>
{
    private ArrayList<OrderDetail> list;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    private AdapterOrderDetail.OnItemClickListener listener;
    public void setOnItemClickListener(AdapterOrderDetail.OnItemClickListener listener){
        this.listener=listener;
    }

    public AdapterOrderDetail(ArrayList<OrderDetail> list){
        this.list=list;
    }

    @NonNull
    @Override
    public AdapterOrderDetail.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);

        View orderView=inflater.inflate(R.layout.item_order_detail,viewGroup,false);
        RecyclerView.ViewHolder viewHolder=new AdapterOrderDetail.ViewHolder(orderView);

        return (AdapterOrderDetail.ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderDetail.ViewHolder viewHolder, int i) {
        // Get the data model based on position
        OrderDetail order = list.get(i);

        // Set item views based on your views and data model
        TextView textViewProductName = viewHolder.textViewProductName;
        TextView textViewProductPrice = viewHolder.textViewProductPrice;
        TextView textViewProductNumber = viewHolder.textViewProductNumber;
        TextView textViewProductTotal = viewHolder.textViewProductTotal;
        ImageView imageViewAvatar=viewHolder.imageViewAvatar;
        //set text
        textViewProductName.setText(order.getFood_name());
        textViewProductPrice.setText("Giá:" +order.getPrice()+"đ");
        textViewProductNumber.setText("Số lượng: "+order.getAmount());
        Glide.with(imageViewAvatar.getContext()).load(order.getThumbnail().toString()).into(imageViewAvatar);
        int total=order.getAmount()*(Integer.parseInt(order.getPrice()));

        textViewProductTotal.setText("Tổng: "+total+"đ");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewProductName,textViewProductPrice,textViewProductNumber,textViewProductTotal;
        ImageView imageViewAvatar;
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textViewProductName=(TextView)itemView.findViewById(R.id.txtName_item_order_detail);
            textViewProductPrice=(TextView)itemView.findViewById(R.id.txt_price_item_order_detail);
            textViewProductNumber=(TextView)itemView.findViewById(R.id.txt_number_item_order_detail);
            textViewProductTotal=(TextView)itemView.findViewById(R.id.txt_total_item_order_detail);
            imageViewAvatar=(ImageView)itemView.findViewById(R.id.img_avatar_item_order_detail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position =getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}