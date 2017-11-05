package com.example.khantilchoksi.detectunknown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.InputStream;

/**
 * Created by khantilchoksi on 04/11/17.
 */
public class FacialRecognitionTask extends AsyncTask<InputStream, String, Face[]> {
    private boolean mSucceed = true;
    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private Context mContext;
    private static FaceServiceClient mFaceServiceClient;

    private static final String LOG_TAG = FacialRecognitionTask.class.getSimpleName();
    public AsyncResponse delegate = null;


    public FacialRecognitionTask(FaceServiceClient faceServiceClient, Context context, Activity activity, ProgressDialog progressDialog, AsyncResponse delegate){
        Log.d(LOG_TAG, "In constructor");
        this.mFaceServiceClient = faceServiceClient;
        this.mContext = context;
        this.mActivity = activity;
        this.mProgressDialog = progressDialog;
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(Face[] faces);
    }


    @Override
    protected Face[] doInBackground(InputStream... params) {
        // Get an instance of face service client to detect faces in image.

        Log.d(LOG_TAG, "In constructor 44");
        Log.d(LOG_TAG, "params[0] "+params[0]);
        try {
            publishProgress("Detecting...");

            // Start detection.
            return mFaceServiceClient.detect(
                    params[0],  /* Input stream of image to detect */
                    true,       /* Whether to return face ID */
                    true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                    new FaceServiceClient.FaceAttributeType[] {
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender,
                            FaceServiceClient.FaceAttributeType.Smile,
                            FaceServiceClient.FaceAttributeType.Glasses,
                            FaceServiceClient.FaceAttributeType.FacialHair,
                            FaceServiceClient.FaceAttributeType.Emotion,
                            FaceServiceClient.FaceAttributeType.HeadPose,
                            FaceServiceClient.FaceAttributeType.Accessories,
                            FaceServiceClient.FaceAttributeType.Blur,
                            FaceServiceClient.FaceAttributeType.Exposure,
                            FaceServiceClient.FaceAttributeType.Hair,
                            FaceServiceClient.FaceAttributeType.Makeup,
                            FaceServiceClient.FaceAttributeType.Noise,
                            FaceServiceClient.FaceAttributeType.Occlusion
                    });
        } catch (Exception e) {
            mSucceed = false;
            publishProgress(e.getMessage());
            //addLog(e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();

    }

    @Override
    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(Face[] result) {
        mProgressDialog.dismiss();
        if (mSucceed) {
            Log.d(LOG_TAG," Succeeded");
            Log.d(LOG_TAG, "Response: Success. Detected " + (result == null ? 0 : result.length)
                        + " face(s) in " + result.length);
            delegate.processFinish(result);
        }else{
            Log.d(LOG_TAG," Not Succeeded");
        }


    }
}
