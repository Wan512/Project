package com.example.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project.AppointmentListActivity;
import com.example.project.R;
import com.example.project.model.appointment;

import java.util.List;


public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnLongClickListener{

        public TextView tvDate;
        public TextView tvTime;
        public TextView tvDesc;
        public ViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDesc);

            itemView.setOnLongClickListener(this);
        }
        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }

    }
    private int currentPos;

    private List<appointment> mListData;   // list of book objects
    private Context mContext;       // activity context

    public AppointmentAdapter(Context context, List<appointment> listData){
        mListData = listData;
        mContext = context;
    }

    private Context getmContext(){return mContext;}


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the single item layout
        View view = inflater.inflate(R.layout.appointment_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder
        appointment m = mListData.get(position);
        holder.tvDate.setText(m.getAppointment_date());
       // holder.tvTime.setText(m.getAppointment_time());
        holder.tvDesc.setText(m.getAppointment_desc());
    }

    public void remove(int position) {
        if (mListData != null && position >= 0 && position < mListData.size()) {
            mListData.remove(position);
            notifyItemRemoved(position);
        }
    }


    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public appointment getSelectedItem() {
        if(currentPos>=0 && mListData!=null && currentPos<mListData.size()) {
            return mListData.get(currentPos);
        }
        return null;
    }


}
