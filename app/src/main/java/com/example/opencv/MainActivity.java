package com.example.opencv;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.androidcv.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d("TAG", "OpCV no loaded");
        } else {
          Log.d("TAG", "OpCv loaded");
        }
    }

    int iLowH = 50;
    int iLowS = 100;
    int iLowV = 100;
    int iHighH = 70;
    int iHighS = 255;
    int iHighV = 255;
    Mat imgHSV, imgThresholded;
    Scalar sc1, sc2;
    JavaCameraView cameraView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        sc1 = new Scalar(iLowH, iLowS, iLowV);
        sc2 = new Scalar(iHighH, iHighS, iHighV);
        cameraView = (JavaCameraView) findViewById(R.id.cameraview);
        cameraView.setCameraIndex(0);
        cameraView.setCvCameraViewListener(this);
        cameraView.enableView();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        imgHSV = new Mat(width, height, CvType.CV_8UC4);
        imgThresholded = new Mat(width, height, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Imgproc.cvtColor(inputFrame.rgba(), imgHSV, Imgproc.COLOR_BGR2HSV);
        Core.inRange(imgHSV, sc1, sc2, imgThresholded);
        return imgThresholded;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.disableView();
    }
}