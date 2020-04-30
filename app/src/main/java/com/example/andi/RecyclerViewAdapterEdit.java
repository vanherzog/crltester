package com.example.andi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterEdit extends RecyclerView.Adapter<RecyclerViewAdapterEdit.ViewHolder> {


    private Context mContext;
    private OnItemClickListener mListener;
    private ArrayList<? extends Server> servers;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onEditClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public RecyclerViewAdapterEdit(Context context, ArrayList<? extends Server> servers) {
        mContext = context;
        this.servers = servers;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_item_edit, parent, false);
        ViewHolder holder = new ViewHolder(view, mListener);
        return holder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.server_name.setText(servers.get(position).getName());

    }


    class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView server_name;
        public ImageView mDeleteImage;
        public ImageView mEditImage;

        ItemClickListener itemClickListener;

        public ViewHolder(final View itemView, final RecyclerViewAdapterEdit.OnItemClickListener listener) {
            super(itemView);
            server_name = itemView.findViewById(R.id.server_name_edit);
            mDeleteImage = itemView.findViewById(R.id.button_delete);
            mEditImage = itemView.findViewById(R.id.server_item_edit);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });



            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            mEditImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onEditClick(position);
                        }
                    }
                }
            });



        }

        public void setItemClickListener(ItemClickListener ic){
            this.itemClickListener = ic;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }
    }



    @Override
    public int getItemCount() {
        return servers.size();
    }



}