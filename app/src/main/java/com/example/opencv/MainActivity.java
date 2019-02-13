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
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        cameraView = (JavaCameraView) findViewById(R.id.cameraViewer);
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
        Mat frameRGB = inputFrame.rgba();
        Imgproc.cvtColor(frameRGB, imgHSV, Imgproc.COLOR_BGR2HSV);
        Core.inRange(imgHSV, sc1, sc2, imgThresholded);

        Mat morphOutput = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Mat mask = imgThresholded;

        Imgproc.findContours(imgThresholded, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_TC89_L1);

          // morphological operators
          // dilate with large element, erode with small ones
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

        Imgproc.erode(mask, morphOutput, erodeElement);
        Imgproc.dilate(mask, morphOutput, dilateElement);

        if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
        {
            // for each contour, display it in blue
            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
            {
                Imgproc.drawContours(frameRGB, contours, idx, new Scalar(250, 0, 0));
            }
        }

        return frameRGB;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.disableView();
    }
}