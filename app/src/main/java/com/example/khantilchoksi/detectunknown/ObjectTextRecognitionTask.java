package com.example.khantilchoksi.detectunknown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by khantilchoksi on 04/11/17.
 */
public class ObjectTextRecognitionTask extends AsyncTask<Object, Void, String> {
    private boolean mSucceed = true;
    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private Context mContext;
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static Bitmap mBitmap;
    private static ArrayList<String> mLabelsList;
    private static ArrayList<Float> mScoresList;

    private static final String LOG_TAG = ObjectTextRecognitionTask.class.getSimpleName();
    public AsyncResponse delegate = null;


    public ObjectTextRecognitionTask(Bitmap bitmap, Context context, Activity activity, ProgressDialog progressDialog, AsyncResponse delegate) {
        Log.d(LOG_TAG, "In constructor");
        this.mBitmap = bitmap;
        this.mContext = context;
        this.mActivity = activity;
        this.mProgressDialog = progressDialog;
        this.delegate = delegate;
        this.mLabelsList = new ArrayList<>();
        this.mScoresList = new ArrayList<>();

    }

    public interface AsyncResponse {
        void processFinish(ArrayList<String> labelsList, ArrayList<Float> scoresList);
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            VisionRequestInitializer requestInitializer =
                    new VisionRequestInitializer(mContext.getResources().getString(R.string.CLOUD_VISION_API_KEY)) {
                        /**
                         * We override this so we can inject important identifying fields into the HTTP
                         * headers. This enables use of a restricted cloud platform API key.
                         */
                        @Override
                        protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                throws IOException {
                            super.initializeVisionRequest(visionRequest);

                            String packageName = mActivity.getPackageName();
                            visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                            String sig = PackageManagerUtils.getSignature(mActivity.getPackageManager(), packageName);

                            visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                        }
                    };

            Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
            builder.setVisionRequestInitializer(requestInitializer);

            Vision vision = builder.build();

            BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                    new BatchAnnotateImagesRequest();
            batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                // Add the image
                Image base64EncodedImage = new Image();
                // Convert the bitmap to a JPEG
                // Just in case it's a format that Android understands but Cloud Vision
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                // Base64 encode the JPEG
                base64EncodedImage.encodeContent(imageBytes);
                annotateImageRequest.setImage(base64EncodedImage);

                // add the features we want
                annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                    Feature labelDetection = new Feature();
                    //labelDetection.setType("LABEL_DETECTION");
                    //labelDetection.setType("TEXT_DETECTION");
                    labelDetection.setMaxResults(10);
                    add(labelDetection);
                }});

                // Add the list of one thing to the request
                add(annotateImageRequest);
            }});

            Vision.Images.Annotate annotateRequest =
                    vision.images().annotate(batchAnnotateImagesRequest);
            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            annotateRequest.setDisableGZipContent(true);
            Log.d(TAG, "created Cloud Vision request object, sending request");

            BatchAnnotateImagesResponse response = annotateRequest.execute();
            return convertResponseToString(response);

        } catch (GoogleJsonResponseException e) {
            Log.d(TAG, "failed to make API request because " + e.getContent());
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
        return "Cloud Vision API request failed. Check logs for details.";
    }


    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }


    @Override
    protected void onPostExecute(String result) {
        mProgressDialog.dismiss();
        if (mSucceed) {
            Log.d(LOG_TAG, " Succeeded");
            delegate.processFinish(mLabelsList, mScoresList);
        } else {
            Log.d(LOG_TAG, " Not Succeeded");
        }


    }


    private String convertResponseToString(BatchAnnotateImagesResponse response) throws IOException {
        String message = "I found these things:\n\n";
        Log.d(LOG_TAG, "HELLO: "+response.toPrettyString());

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                mLabelsList.add(label.getDescription());
                mScoresList.add(label.getScore()*100);
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }
}
