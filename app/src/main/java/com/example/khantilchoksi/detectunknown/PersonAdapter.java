package com.example.khantilchoksi.detectunknown;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khantilchoksi.detectunknown.helper.ImageHelper;
import com.microsoft.projectoxford.face.contract.Accessory;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.contract.Hair;
import com.microsoft.projectoxford.face.contract.HeadPose;
import com.microsoft.projectoxford.face.contract.Makeup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import com.squareup.picasso.Callback;
//import com.squareup.picasso.Picasso;

/**
 * Created by Khantil on 11-05-2017.
 */

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private List<Face> facesList;
    private ArrayList<Bitmap> faceThumbnailsList;
    private Activity mActivity;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView getAgeTextView() {
            return ageTextView;
        }

        public TextView getGenderTextView() {
            return genderTextView;
        }

        public TextView getHairTextView() {
            return hairTextView;
        }

        public TextView getBlurTextView() {
            return blurTextView;
        }

        public ImageView getPersonIconImageView() {
            return personIconImageView;
        }

        private final TextView ageTextView;
        private final TextView genderTextView;



        private final TextView hairTextView;
        private final TextView blurTextView;

        private final ImageView personIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
/*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG," Element: "+getAdapterPosition()+ " clicked. Means "
                            +specialityNamesList.get(getAdapterPosition())+" got.");
                    String specialityId = specialityIdList.get(getAdapterPosition());
                    Intent showDoctorsIntent = new Intent(mActivity, ShowDoctorsActivity.class);
                    showDoctorsIntent.putExtra("specialityId",specialityId);
                    showDoctorsIntent.putExtra("specialityName",specialityNamesList.get(getAdapterPosition()));
                    mActivity.startActivity(showDoctorsIntent);

                }
            });*/

            ageTextView = (TextView)itemView.findViewById(R.id.age);
            genderTextView = (TextView)itemView.findViewById(R.id.gender);
            hairTextView = (TextView)itemView.findViewById(R.id.hair);
            blurTextView = (TextView)itemView.findViewById(R.id.blur);
            personIconImageView = (ImageView)itemView.findViewById(R.id.person_icon);
        }
    }

    public PersonAdapter(Face[] detectionResult, Activity activity, Bitmap originalBitMap) {
        facesList = new ArrayList<>();
        faceThumbnailsList = new ArrayList<>();

        if (detectionResult != null) {
            facesList = Arrays.asList(detectionResult);
            for (Face face : facesList) {
                try {
                    // Crop face thumbnail with five main landmarks drawn from original image.
                    faceThumbnailsList.add(ImageHelper.generateFaceThumbnail(
                            originalBitMap, face.faceRectangle));
                } catch (IOException e) {
                    // Show the exception when generating face thumbnail fails.

                }
            }
        }
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.getAgeTextView().setText("Age: "+facesList.get(position).faceAttributes.age);
        holder.getGenderTextView().setText("Gender: "+facesList.get(position).faceAttributes.gender);

        holder.getHairTextView().setText("Hair: "+getHair(facesList.get(position).faceAttributes.hair));
        holder.getBlurTextView().setText("Blur: "+facesList.get(position).faceAttributes.blur.blurLevel);

        holder.getPersonIconImageView().setImageBitmap(faceThumbnailsList.get(position));
    }

    // Detecting/Analysing
    private String getHair(Hair hair) {
        if (hair.hairColor.length == 0)
        {
            if (hair.invisible)
                return "Invisible";
            else
                return "Bald";
        }
        else
        {
            int maxConfidenceIndex = 0;
            double maxConfidence = 0.0;

            for (int i = 0; i < hair.hairColor.length; ++i)
            {
                if (hair.hairColor[i].confidence > maxConfidence)
                {
                    maxConfidence = hair.hairColor[i].confidence;
                    maxConfidenceIndex = i;
                }
            }

            return hair.hairColor[maxConfidenceIndex].color.toString();
        }
    }

    private String getMakeup(Makeup makeup) {
        return  (makeup.eyeMakeup || makeup.lipMakeup) ? "Yes" : "No" ;
    }

    private String getAccessories(Accessory[] accessories) {
        if (accessories.length == 0)
        {
            return "NoAccessories";
        }
        else
        {
            String[] accessoriesList = new String[accessories.length];
            for (int i = 0; i < accessories.length; ++i)
            {
                accessoriesList[i] = accessories[i].type.toString();
            }

            return TextUtils.join(",", accessoriesList);
        }
    }

    private String getFacialHair(FacialHair facialHair) {
        return (facialHair.moustache + facialHair.beard + facialHair.sideburns > 0) ? "Yes" : "No";
    }

    private String getEmotion(Emotion emotion)
    {
        String emotionType = "";
        double emotionValue = 0.0;
        if (emotion.anger > emotionValue)
        {
            emotionValue = emotion.anger;
            emotionType = "Anger";
        }
        if (emotion.contempt > emotionValue)
        {
            emotionValue = emotion.contempt;
            emotionType = "Contempt";
        }
        if (emotion.disgust > emotionValue)
        {
            emotionValue = emotion.disgust;
            emotionType = "Disgust";
        }
        if (emotion.fear > emotionValue)
        {
            emotionValue = emotion.fear;
            emotionType = "Fear";
        }
        if (emotion.happiness > emotionValue)
        {
            emotionValue = emotion.happiness;
            emotionType = "Happiness";
        }
        if (emotion.neutral > emotionValue)
        {
            emotionValue = emotion.neutral;
            emotionType = "Neutral";
        }
        if (emotion.sadness > emotionValue)
        {
            emotionValue = emotion.sadness;
            emotionType = "Sadness";
        }
        if (emotion.surprise > emotionValue)
        {
            emotionValue = emotion.surprise;
            emotionType = "Surprise";
        }
        return String.format("%s: %f", emotionType, emotionValue);
    }

    private String getHeadPose(HeadPose headPose)
    {
        return String.format("Pitch: %s, Roll: %s, Yaw: %s", headPose.pitch, headPose.roll, headPose.yaw);
    }

    @Override
    public int getItemCount() {
        return facesList.size();
    }


}
