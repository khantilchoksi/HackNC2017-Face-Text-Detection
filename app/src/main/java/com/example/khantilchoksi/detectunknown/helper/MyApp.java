package com.example.khantilchoksi.detectunknown.helper;

import android.app.Application;

import com.example.khantilchoksi.detectunknown.R;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

/**
 * Created by khantilchoksi on 04/11/17.
 */
public class MyApp extends Application {
    private static FaceServiceClient sFaceServiceClient;

    @Override
    public void onCreate() {
        super.onCreate();
        sFaceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));
    }

    public static FaceServiceClient getFaceServiceClient() {
        return sFaceServiceClient;
    }


}
