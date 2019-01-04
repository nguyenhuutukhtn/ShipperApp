package com.androidev.maps.Adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidev.maps.Model.OrderMainShipper;
import com.androidev.maps.R;

import java.util.ArrayList;

public class AdapterMainShipper extends RecyclerView.Adapter<AdapterMainShipper.ViewHolder>
{
    private ArrayList<OrderMainShipper> list;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public interface OnConfirmClickListener{
        void onConfirmClick(int position);
    }
    private OnItemClickListener mlistener;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mlistener=listener;
    }
    private OnConfirmClickListener confirmClickListener;
    public void setConfirmClickListener(OnConfirmClickListener confirmClickListener){this.confirmClickListener=confirmClickListener;}

    public AdapterMainShipper(ArrayList<OrderMainShipper> list){
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);

        View orderView=inflater.inflate(R.layout.item_main_shipper,viewGroup,false);
        RecyclerView.ViewHolder viewHolder=new ViewHolder(orderView,confirmClickListener);

        return (ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // Get the data model based on position
        OrderMainShipper order = list.get(i);

        // Set item views based on your views and data model
        TextView textViewStoreName = viewHolder.textViewStoreName;
        TextView textViewOrigin = viewHolder.textViewOrigin;
        TextView textViewDestination = viewHolder.textViewDestination;
        //set text
        textViewStoreName.setText(order.getStore_name());
        textViewOrigin.setText(order.getFrom());
        textViewDestination.setText(order.getTo());
    }

    @Override
    public int getItemCount() {
        if (list!= null)
        return list.size();
        return 0;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewStoreName,textViewOrigin,textViewDestination;
        Button buttonConfirm;
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView,final OnConfirmClickListener listener){
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textViewStoreName=(TextView)itemView.findViewById(R.id.txt_store_name_activity_main_shipper);
            textViewOrigin=(TextView)itemView.findViewById(R.id.txt_origin_activity_main_shipper);
            textViewDestination=(TextView)itemView.findViewById(R.id.txt_destination_activity_main_shipper);
            buttonConfirm=(Button)itemView.findViewById(R.id.btn_confirm_item_main_shipper);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position =getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            mlistener.onItemClick(position);
                        }
                    }
                }
            });

            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onConfirmClick(position);
                        }
                    }
                }
            });
        }
    }
}