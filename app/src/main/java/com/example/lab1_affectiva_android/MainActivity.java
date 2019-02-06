package com.example.lab1_affectiva_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Face;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraDetector.CameraEventListener, CameraDetector.ImageListener{

    SurfaceView cameraDetectorSurfaceView;
    CameraDetector cameraDetector;


    TextView sma_label;
    TextView time_label;
    TextView wma_label;
    ArrayList<Float> queue = new ArrayList<>();
    ArrayList<Float> list_t = new ArrayList<>();

    int counter_t = -1;
    int maxProcessingRate = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sma_label = (TextView) findViewById(R.id.SMA_Label);
        time_label = (TextView) findViewById(R.id.time_label);
        wma_label = (TextView) findViewById(R.id.WMA_Label);
        cameraDetectorSurfaceView = (SurfaceView) findViewById(R.id.cameraDetectorSurfaceView);

        //4
        cameraDetector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraDetectorSurfaceView);

        //5
        cameraDetector.setMaxProcessRate(maxProcessingRate);

        //6
        cameraDetector.setImageListener(this);
        cameraDetector.setOnCameraEventListener(this);

        //7
        cameraDetector.setDetectAllEmotions(true);

        //8
        cameraDetector.start();



    }

        @Override
        public void onCameraSizeSelected(int cameraHeight, int cameraWidth, Frame.ROTATE rotation) {

            //1
            ViewGroup.LayoutParams params = cameraDetectorSurfaceView.getLayoutParams();

            //2
            params.height = cameraHeight;
            params.width = cameraWidth;

            //3
            cameraDetectorSurfaceView.setLayoutParams(params);
        }


    public void onImageResults(List<Face> faces, Frame frame, float timeStamp) {
        //1
        if (faces == null)
            return; //frame was not processed

        //2
        if (faces.size() == 0)
            return; //no face found

        //3
        Face face = faces.get(0);

        //4
        float joy = face.emotions.getJoy();
        float anger = face.emotions.getAnger();
        float surprise = face.emotions.getSurprise();


        //5

        int time = (int)timeStamp;

        //******** TIME **************
        if(list_t.size()==10 & time >counter_t){
            counter_t++;
            list_t.remove(0);
            list_t.add(joy);

            float avg_t = average(list_t);
            if(avg_t>=40) {
                time_label.setVisibility(View.VISIBLE);
            }
            else{
                time_label.setVisibility(View.INVISIBLE);
            }

        }else if (list_t.size()<=10 & time>counter_t){
            list_t.add(joy);
            counter_t++;
        }


        //*********** 100 Data Points ***********
        if(queue.size()<100){
            queue.add(joy);
        }else{
            queue.remove(0);
            queue.add(joy);

            float sma = average(queue);
            float wma = wAverage(queue);

            if(sma>=40) {
                sma_label.setVisibility(View.VISIBLE);
            }
            else {
                sma_label.setVisibility(View.INVISIBLE);
            }

            if(wma>=40){
                wma_label.setVisibility(View.VISIBLE);
            }
            else{
                wma_label.setVisibility(View.INVISIBLE);
            }
        }


    }

    public float wAverage (ArrayList <Float> queue){

        float weight = (float).1;
        float total = 0;
        float total_w = 0;

        for(int i = 0; i< queue.size(); i++){
            total_w+= weight;
            total+= queue.get(i)*weight;
            weight += .1;
        }

        float avg = total/total_w;

        return avg;
    }

    public float average (ArrayList <Float> queue){

        float total = 0;

        for(Float num: queue){

            total += num;
        }

        float average = total/queue.size();

        return average;

    }


}

