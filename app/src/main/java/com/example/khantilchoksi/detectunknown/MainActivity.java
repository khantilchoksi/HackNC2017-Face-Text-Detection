package com.example.khantilchoksi.detectunknown;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Accessory;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.contract.Hair;
import com.microsoft.projectoxford.face.contract.HeadPose;
import com.microsoft.projectoxford.face.contract.Makeup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements FacialRecognitionTask.AsyncResponse{
    private final int PICK_IMAGE = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private static FaceServiceClient mFaceServiceClient;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PersonAdapter mPersonAdapter;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFaceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));
        
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.persons_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                imageView.setImageBitmap(mBitmap);

                //Now once I have got the image from the gallery, call the microsoft Azure
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

                //detectAndFrame(mBitmap);
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Analysing....");

                FacialRecognitionTask facialRecognitionTask = new FacialRecognitionTask
                        (mFaceServiceClient,getApplicationContext(),this,mProgressDialog,this);
                facialRecognitionTask.execute(inputStream);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void processFinish(Face[] faces) {
        Log.d(LOG_TAG, "processTextFinish");
        if(faces != null){
            mPersonAdapter = new PersonAdapter(faces,this,mBitmap);
            mRecyclerView.setAdapter(mPersonAdapter);
        }
            Log.d(LOG_TAG, "Number of faces: "+faces.length);
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

}
