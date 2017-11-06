//package com.example.khantilchoksi.detectunknown;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.microsoft.projectoxford.face.contract.Face;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//public class MainActivity23 extends AppCompatActivity implements FacialRecognitionTask.AsyncResponse{
//    private final int PICK_IMAGE = 1;
//    private static final String LOG_TAG = MainActivity23.class.getSimpleName();
//    private ProgressDialog mProgressDialog;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        Button button1 = (Button)findViewById(R.id.button1);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                gallIntent.setType("image/*");
//                startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
//            }
//        });
//
//
//
//    }
//
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
//                imageView.setImageBitmap(bitmap);
//
//                //Now once I have got the image from the gallery, call the microsoft Azure
//                ByteArrayOutputStream output = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
//                ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
//
//                //detectAndFrame(bitmap);
//                mProgressDialog = new ProgressDialog(this);
//                mProgressDialog.setTitle("Analysing....");
//
//                FacialRecognitionTask facialRecognitionTask = new FacialRecognitionTask
//                        (getApplicationContext(),this,mProgressDialog,this);
//                facialRecognitionTask.execute(inputStream);
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//
//
//    @Override
//    public void processTextFinish(Face[] faces) {
//        Log.d(LOG_TAG, "processTextFinish");
//        if(faces != null)
//            Log.d(LOG_TAG, "NOT NULL");
//    }
//}
