/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudklosett.cloudvision;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;

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
//import com.google.api.services.vision.v1.model.SafeSearchAnnotation;

// added for openCV //
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;
import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import org.opencv.android.OpenCVLoader;
import org.opencv.video.Video;


import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyBAliqPapTruckNSk_nRdIDi5vadf_NqvM";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private ImageView mMainImage;
    private ImageView mMainImage2;
    private Bitmap bmp;
    private Mat matIMG;//
    //
    private Mat mGray = new Mat();
    private Mat mRgb = new Mat();
    private Mat mFGMask = new Mat();

    //
    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });

        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);
        mMainImage2 = (ImageView) findViewById(R.id.main_image2);
        //File f = new File("shoe.jpg");

    } // end of on create

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }


    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            uploadImage(Uri.fromFile(getCameraFile()));
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);
                //added by
                // end of added by me
                callCloudVision(bitmap);

                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }


    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                //
                                 // We override this so we can inject important identifying fields into the HTTP
                                 //headers. This enables use of a restricted cloud platform API key.
                                 ///
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

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
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
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
                    //array containing keywords describing image
                    ArrayList<String> descr = convertResponseToArray(response);
                    //added for debug purposes
                    Log.d("BEGINGING", "ARRAY LOOP");
                    for(String s : descr){
                        Log.d("s", s);
                    }
                    //end of addition


                    String types = convertResponseToString(response);
                    Log.d("TYPES DEBUG", types);
                    return types;
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {

                mImageDetails.setText(result);
                mMainImage2.setImage


            }
        }.execute();


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

    //converts BatchAnnotateImagesResponse response into a new line seperated String
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
                Log.d("LOOP", message);
            }
        } else {
            message += "nothing";
        }

        return message;
    }

    //Added by Froylan Valencia
    //converts BatchAnnotateImagesResponse response into an array
    private ArrayList<String> convertResponseToArray(BatchAnnotateImagesResponse response) {
        String message = "I added these things to Array List:\n\n";
        ArrayList<String> ret = new ArrayList<String>();
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                ret.add(label.getDescription());
                message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
                Log.d("LOOP", message);
            }
        }
        return ret;
    }

}
/*

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cloudklosett.cloudvision.R.*;

public class MainActivity extends AppCompatActivity {
    static {
        //System.loadLibrary("opencv_java");
        if(!OpenCVLoader.initDebug()){
            Log.i("opencv","opencv initialization failed");
        }else{
            Log.i("opencv","opencv initialization successful");
        }
    }

    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private ImageView mPhotoCapturedImageView;
    private String mImageFileLocation = "shoe.jpg";
    private Bitmap photoSmall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);


        mPhotoCapturedImageView = (ImageView)findViewById(R.id.main_image2);
        takePhoto(mPhotoCapturedImageView);
    }

    File createImageFile()throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,".jpg",storageDirectory);
        mImageFileLocation = image.getAbsolutePath();
        return image;
    }

    // ** camera functionality **
    public void takePhoto(View view){
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photo = null;
        try{
            photo = createImageFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(callCameraApplicationIntent,ACTIVITY_START_CAMERA_APP);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK){
            //Bundle extras = data.getExtras();
            //Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
            //mPhotoCapturedImageView.setImageBitmap(photoCapturedBitmap);
            //Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation);
            //mPhotoCapturedImageView.setImageBitmap(photoCapturedBitmap);
            reduceImage();
        }
    }

    void reduceImage(){
        Log.i("opencv","reached reduce");
        int vWidth = mPhotoCapturedImageView.getWidth();
        int vHeight = mPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOps = new BitmapFactory.Options();
        // ** dont delete this
        bmOps.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOps);

        int camWidth = bmOps.outWidth;
        int camHeight = bmOps.outHeight;

        int scale = Math.max(camWidth/vWidth,camHeight/vHeight);
        bmOps.inSampleSize = scale;
        bmOps.inJustDecodeBounds = false;
        photoSmall = BitmapFactory.decodeFile(mImageFileLocation,bmOps);
        showImage();
    }

    // **** Functions for various options ***

    public boolean showImage(){
        mPhotoCapturedImageView.setImageBitmap(photoSmall);
        return true;
    }
    public boolean showGrayImage(){
        Bitmap newBmp = photoSmall.copy(photoSmall.getConfig(), true);
        Mat imgMAT = new Mat (photoSmall.getHeight(), photoSmall.getWidth(), CvType.CV_8UC1);;
        Utils.bitmapToMat(photoSmall, imgMAT);
        Imgproc.cvtColor(imgMAT, imgMAT, Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(imgMAT, newBmp);
        mPhotoCapturedImageView.setImageBitmap(newBmp);
        return true;
    }

    public boolean showFeatures(){
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        Bitmap newBmp = photoSmall.copy(photoSmall.getConfig(), true);
        Mat imgMAT = new Mat (photoSmall.getHeight(), photoSmall.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(photoSmall, imgMAT);
        Imgproc.cvtColor(imgMAT, imgMAT, Imgproc.COLOR_RGB2GRAY);
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        detector.detect(imgMAT, keyPoints);
        Features2d.drawKeypoints(imgMAT, keyPoints, imgMAT);
        Utils.matToBitmap(imgMAT, newBmp);
        mPhotoCapturedImageView.setImageBitmap(newBmp);
        return true;
    }

    public void removeBackground(){
        Mat thresholdImg = new Mat (photoSmall.getHeight(), photoSmall.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(photoSmall, thresholdImg);
        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<Mat>();
        Imgproc.cvtColor(thresholdImg, hsvImg, Imgproc.COLOR_RGB2HSV);
        Core.split(hsvImg,hsvPlanes);

        MatOfInt histSize = new MatOfInt(180);
        double average = 0.0;
        Mat hist_hue = new Mat();
        List<Mat> hue = new ArrayList<>();
        hue.add(hsvPlanes.get(0));
        Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), hist_hue, histSize, new MatOfFloat(0, 179));
        for (int h = 0; h < 180; h++)
            average += (hist_hue.get(h, 0)[0] * h);
        average = average / hsvImg.size().height / hsvImg.size().width;
        double threshValue = average;
        Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);
        Imgproc.blur(thresholdImg, thresholdImg, new Size(5, 5));
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);
        Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);

        Bitmap bgBmp = BitmapFactory.decodeResource(getResources(), 32);
        Mat bgMat = new Mat (bgBmp.getHeight(), bgBmp.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bgBmp, bgMat);

        Mat foreground = new Mat(thresholdImg.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        Size sz = new Size(thresholdImg.width(),thresholdImg.height());
        Imgproc.resize( bgMat, foreground, sz);

        Mat frame = new Mat (photoSmall.getHeight(), photoSmall.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(photoSmall, frame);
        frame.copyTo(foreground, thresholdImg);

        Bitmap newBmp = photoSmall.copy(photoSmall.getConfig(), true);
        Utils.matToBitmap(foreground, newBmp);
        mPhotoCapturedImageView.setImageBitmap(newBmp);
    }

    // ****   OPTIONS  ****
    private MenuItem grayItem;
    private MenuItem featuresItem;
    private MenuItem camItem;
    private MenuItem removebgItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        camItem = menu.add("Preview Original");
        grayItem = menu.add("Preview Gray");
        featuresItem = menu.add("Find features");
        removebgItem = menu.add("GT Badge");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item == camItem) {
            showImage();
        } else if(item == grayItem) {
            showGrayImage();
        } else if (item == featuresItem) {
            showFeatures();
        } else if (item == removebgItem) {
            removeBackground();
        }

        return true;
    }

}
*/