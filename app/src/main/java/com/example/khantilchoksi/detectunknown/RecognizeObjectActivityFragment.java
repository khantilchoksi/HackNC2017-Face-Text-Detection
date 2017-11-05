package com.example.khantilchoksi.detectunknown;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecognizeObjectActivityFragment extends Fragment implements ObjectRecognitionTask.AsyncResponse{

    private final int PICK_IMAGE = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LabelsAdapter mLabelsAdapter;
    private Bitmap mBitmap;
    private View mRootView;
    private TextView mTextView;

    public RecognizeObjectActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_recognize_object, container, false);


        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.objects_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        //mTextView = (TextView) mRootView.findViewById(R.id.objects_textview);
/*        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("<Your-API_KEY>"));


        Vision vision = visionBuilder.build();*/

        Button button1 = (Button)mRootView.findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
            }
        });



        return mRootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                mBitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri),1200);
                ImageView imageView = (ImageView) mRootView.findViewById(R.id.objectImage);

                imageView.setImageBitmap(mBitmap);

                //Now once I have got the image from the gallery, call the microsoft Azure
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

                //detectAndFrame(mBitmap);
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setTitle("Analysing Object....");

                ObjectRecognitionTask facialRecognitionTask = new ObjectRecognitionTask
                        (mBitmap,getActivity().getApplicationContext(),getActivity(),mProgressDialog,this);
                facialRecognitionTask.execute(inputStream);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }


    @Override
    public void processFinish(ArrayList<String> labelsList, ArrayList<Float> scoresList) {
        if(labelsList!= null && scoresList!= null){
            mLabelsAdapter = new LabelsAdapter(labelsList,scoresList,getActivity());
            mRecyclerView.setAdapter(mLabelsAdapter);
        }
    }
}
