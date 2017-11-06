package com.example.khantilchoksi.detectunknown;

import android.app.Activity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by khantilchoksi on 04/11/17.
 */
public class ObjectTextRecognitionTask extends AsyncTask<Object, Void, String> {
    private boolean mSucceed = true;
    //private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private Context mContext;
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static Bitmap mBitmap;
    private static String[] mTextLabels = {"Label1","Label2"};

    private static final String LOG_TAG = ObjectTextRecognitionTask.class.getSimpleName();
    public AsyncResponse delegate = null;


    public ObjectTextRecognitionTask(Bitmap bitmap, Context context, Activity activity, AsyncResponse delegate) {
        Log.d(LOG_TAG, "In constructor");
        this.mBitmap = bitmap;
        this.mContext = context;
        this.mActivity = activity;
        //this.mProgressDialog = progressDialog;
        this.delegate = delegate;


    }

    public interface AsyncResponse {
        void processTextFinish(String[] mTextLabels);
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
                    labelDetection.setType("TEXT_DETECTION");
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

/*
    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }*/


    @Override
    protected void onPostExecute(String result) {
       // mProgressDialog.dismiss();
        if (mSucceed) {
            Log.d(LOG_TAG, " Succeeded");
            delegate.processTextFinish(mTextLabels);
        } else {
            Log.d(LOG_TAG, " Not Succeeded");
        }


    }


    private String convertResponseToString(BatchAnnotateImagesResponse response) throws IOException {
        String message = "I found these things:\n\n";
        Log.d(LOG_TAG, "HELLO: "+response.toPrettyString());

        //mTextLabels = searchWords(response);

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }

    public static String[] searchWords (JSONObject json) throws JSONException {

        try{
            //Object obj = parser.parse(new FileReader("C:\\Users\\Dax Amin\\Downloads\\result(1).json"));
            //JSONObject json = (JSONObject) obj;
            JSONArray text =  (JSONArray)json.get("textAnnotations");
//            Iterator<JSONObject> iterator = text.get
  //          iterator.next();

            HashMap<String, Long> searchWord = new HashMap<String, Long>();
            for(int i = 1; i<text.length(); i++){
                JSONObject temp = text.getJSONObject(i);
                JSONObject temp2 = (JSONObject) temp.get("boundingPoly");
                JSONArray vertex = (JSONArray) temp2.get("vertices");
                JSONObject first = (JSONObject) vertex.get(1);
                JSONObject second = (JSONObject) vertex.get(2);

                Long first_y = (Long) first.get("y");
                Long second_y = (Long) second.get("y");
                searchWord.put((String) temp.get("description"), second_y-first_y);
            }

            Map<String, Long> sortedSearchWord = sortByValue(searchWord);

            Iterator it = sortedSearchWord.entrySet().iterator();
            String[] str = new String[3];
            int i =0;
            while(it.hasNext() && i<3) {
                Map.Entry pair = (Map.Entry) it.next();
                str[i] = (String) pair.getKey();
                i++;
            }

            return(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
