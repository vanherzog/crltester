package com.example.andi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    private Context mContext;
    private OnItemClickListener mListener;
    private ArrayList<? extends Server> servers;
    ArrayList<Server> noCheckedServer = new ArrayList<>();
    ArrayList<Server> checkedServers = new ArrayList<>();


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public RecyclerViewAdapter(Context context, ArrayList<? extends Server> servers) {
        mContext = context;
        this.servers = servers;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_item, parent, false);
        ViewHolder holder = new ViewHolder(view, mListener);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.server_name.setText(servers.get(position).getName());
        holder.grey.setVisibility(View.VISIBLE);
        if(servers.get(position).getStatus() !=null && !servers.get(position).getStatus().isTested()){
            holder.grey.setVisibility(View.VISIBLE);
            holder.green.setVisibility(View.INVISIBLE);
            holder.red.setVisibility(View.INVISIBLE);
        }else{
            if(servers.get(position).getStatus().getTestStates().isEmpty()){
                holder.grey.setVisibility(View.INVISIBLE);
                holder.green.setVisibility(View.VISIBLE);
            }else{
                holder.grey.setVisibility(View.INVISIBLE);
                holder.red.setVisibility(View.VISIBLE);
            }
        }



        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CheckBox chk = (CheckBox) v;
                if (chk.isChecked()) {
                    checkedServers.add(servers.get(position));
                    noCheckedServer.remove(servers.get(position));
                } else if (!chk.isChecked()) {
                    checkedServers.remove(servers.get(position));
                    noCheckedServer.add(servers.get(position));
                }
            }
        });

        /**for(Server p : checkedServers) {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXX   " + p.toString() + p.getStatus().getTestStates().toString());
            if (p.getStatus().getTestStates().isEmpty()) {
                holder.grey.setVisibility(View.INVISIBLE);
                holder.green.setVisibility(View.VISIBLE);
            } else if(p.getStatus().getTestStates().toString().contains("ERROR")){
                holder.grey.setVisibility(View.INVISIBLE);
                holder.red.setVisibility(View.VISIBLE);
            }
        }**/




    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView server_name;
        public CheckBox checkbox;
        public ImageView green;
        public ImageView red;
        public ImageView grey;

        ItemClickListener itemClickListener;


        public ViewHolder(View itemView, final RecyclerViewAdapter.OnItemClickListener listener) {
            super(itemView);
            server_name = itemView.findViewById(R.id.server_name);
            checkbox = itemView.findViewById(R.id.checkBox);
            green = itemView.findViewById(R.id.imageViewOnline);
            red = itemView.findViewById(R.id.imageViewOffline);
            grey = itemView.findViewById(R.id.imageViewDefault);

            checkbox.setOnClickListener(this);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });


        }


            public void setItemClickListener (ItemClickListener ic){
                this.itemClickListener = ic;
            }

            @Override
            public void onClick (View v){
                this.itemClickListener.onItemClick(v, getLayoutPosition());
            }
        }

        @Override
        public int getItemCount() {
            return servers.size();
        }


    }