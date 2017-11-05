package com.example.khantilchoksi.detectunknown;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

//import com.squareup.picasso.Callback;
//import com.squareup.picasso.Picasso;

/**
 * Created by Khantil on 11-05-2017.
 */

public class LabelsAdapter extends RecyclerView.Adapter<LabelsAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayList<String> labelList;
    private ArrayList<Float> scoresList;
    private Activity mActivity;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView getLabelTextView() {
            return labelTextView;
        }

        public TextView getPercentageTextView() {
            return percentageTextView;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        private final TextView labelTextView;
        private final TextView percentageTextView;
        private final ProgressBar progressBar;


        public ViewHolder(View itemView) {
            super(itemView);

            labelTextView = (TextView)itemView.findViewById(R.id.labelTitle);
            percentageTextView = (TextView)itemView.findViewById(R.id.percentage);
            progressBar = (ProgressBar) itemView.findViewById(R.id.ProgressBar);

        }
    }

    public LabelsAdapter(ArrayList<String> labels, ArrayList<Float> scores, Activity activity) {
        labelList = new ArrayList<>();
        scoresList = new ArrayList<>();

        if (labels != null && scores!=null) {
            labelList = labels;
            scoresList = scores;
        }
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_row_item, parent, false);

        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.getLabelTextView().setText(labelList.get(position));
        holder.getPercentageTextView().setText(String.format("%.2f",scoresList.get(position))+" %");

        float f = scoresList.get(position);
        holder.getProgressBar().setProgress((int)f,true);
        //holder.getProgressBar().getProgressDrawable().
    }




    @Override
    public int getItemCount() {
        return labelList.size();
    }


}
