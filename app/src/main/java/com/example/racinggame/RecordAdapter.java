package com.example.racinggame;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Fragment fragment;
    private ArrayList<Record> records = new ArrayList<>();
    private RecordItemClickedListener recordItemClickedListener;

    public RecordAdapter(Fragment fragment, ArrayList<Record> records){
        this.records = records;
        this.fragment = fragment;
    }

    public RecordAdapter setRecordItemClickedListener(RecordItemClickedListener recordItemClickedListener) {
        this.recordItemClickedListener = recordItemClickedListener;
        return this;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_score_item , parent , false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecordViewHolder recordViewHolder = (RecordViewHolder) holder;
        Record record = getItem(position);

        recordViewHolder.record_LBL_score.setText("" +record.getScore());
        recordViewHolder.record_LBL_player.setText(record.getName());

    }


    @Override
    public int getItemCount() {
        return records.size();
    }

    private Record getItem(int position){
        return records.get(position);
    }

    public interface RecordItemClickedListener{
        void RecordItemClicked(Record record ,int position);
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder{

        private MaterialTextView record_LBL_player;
        private MaterialTextView record_LBL_score;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            record_LBL_player = itemView.findViewById(R.id.list_LBL_player);
            record_LBL_score = itemView.findViewById(R.id.list_LBL_score);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordItemClickedListener.RecordItemClicked(getItem(getAdapterPosition()) , getAdapterPosition());
                }
            });

        }
    }
}
