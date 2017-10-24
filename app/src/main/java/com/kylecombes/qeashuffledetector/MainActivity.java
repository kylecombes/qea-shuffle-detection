package com.kylecombes.qeashuffledetector;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Vibrator;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private RelativeLayout layout;
    private TextView statusTV;
    private Button testButton;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Vibrator vibrator;
    private final short SAMPLE_RATE = 50; // readings/sec
    private final short IDLE = 0;
    private final short CALIBRATING_NORMAL = 1;
    private final short CALIBRATING_SHUFFLE = 2;
    private final short MONITORING = 3;
    private final float FRACTION_CALIBRATION_DATA_TO_USE = 2f/3f;
    private final short CALIBRATION_PERIOD = 10000; // Calibrate each walk style for 30 seconds
    private final short MONITORING_UPDATE_PERIOD = 2000; // Recalc every 5s
    private short mCurrentState = IDLE;
    private short ACCEL_AXIS = 1; // Look at acceleration along the y-axis
    private AccelDataProcessor mProcessor;
    private float aveMin = 0;
    private float shuffleAvePeak;
    private float normalAvePeak;
    private float threshold;
    private float sittingThreshold = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout)findViewById(R.id.layout);
        statusTV = (TextView) findViewById(R.id.statusTextView);
        testButton = (Button) findViewById(R.id.test);

        // Initialize the accelerometer stuff
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mSensor == null) { // There was a problem accessing the accelerometer
            layout.setBackgroundColor(Color.RED);
        }

        // Initialize vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void calibrateClick(View view) {
        // Create an acceleration data processor
        mProcessor = new AccelDataProcessor((int)(SAMPLE_RATE * CALIBRATION_PERIOD * FRACTION_CALIBRATION_DATA_TO_USE));

        mCurrentState = CALIBRATING_NORMAL;
        layout.setBackgroundColor(Color.YELLOW);
        new CountDownTimer(CALIBRATION_PERIOD, 1000) {

            public void onTick(long millisUntilFinished) {
                statusTV.setText("Walk normally ("+(millisUntilFinished/1000+1)+"s)...");
            }

            public void onFinish() {

                aveMin = mProcessor.getAverageTroughValue();
                normalAvePeak = mProcessor.getAveragePeakValue();
                mProcessor.clearDataPoints();

                mCurrentState = CALIBRATING_SHUFFLE;
                layout.setBackgroundColor(Color.BLUE);
                vibrator.vibrate(200);
                new CountDownTimer(CALIBRATION_PERIOD, 1000) {

                    public void onTick(long millisUntilFinished) {
                        statusTV.setText("Normal: " + normalAvePeak+ " m/s²\nCalibrate shuffle... ("+(millisUntilFinished/1000+1)+"s)...");
                    }

                    public void onFinish() {
                        mCurrentState = IDLE;
                        layout.setBackgroundColor(Color.WHITE);
                        shuffleAvePeak = mProcessor.getAveragePeakValue();
                        statusTV.setText("Calibrated!\nNormal: " + normalAvePeak + " m/s²\nShuffle: " + shuffleAvePeak + " m/s²");
                        threshold = (normalAvePeak - shuffleAvePeak) / 2 + shuffleAvePeak;
                        testButton.setEnabled(true);
                    }
                }.start();
            }
        }.start();
    }

    public void testClick(View view) {
        statusTV.setText("Monitoring...");
        layout.setBackgroundColor(Color.GREEN);

        // Create an acceleration data processor
        mProcessor = new AccelDataProcessor(SAMPLE_RATE * MONITORING_UPDATE_PERIOD);

        new CountDownTimer(MONITORING_UPDATE_PERIOD*100, MONITORING_UPDATE_PERIOD)  {
            public void onTick(long millisUntilFinished) {
                float avePeak = mProcessor.getAveragePeakValue();
                if (sittingThreshold < avePeak && avePeak < threshold) {
                    layout.setBackgroundColor(Color.RED);
                    // Vibrate for 500 milliseconds
                    vibrator.vibrate(new long[] {100, 200}, 0);
                } else {
                    layout.setBackgroundColor(Color.GREEN);
                    vibrator.cancel();
                }
                statusTV.setText("Monitoring...\nAve peak: " + avePeak + " m/s²");
                mProcessor.clearDataPoints();
            }

            public void onFinish() {

            }
        }.start();

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                float avePeak = mProcessor.getAveragePeakValue();
//                if (avePeak < threshold) {
//                    layout.setBackgroundColor(Color.RED);
//                    // TODO Vibrate phone
//                } else {
//                    layout.setBackgroundColor(Color.GREEN);
//                }
//                statusTV.setText("Monitoring...\nAve peak: " + avePeak + " m/s²");
//                mProcessor.clearDataPoints();
//            }
//        }, 1000, MONITORING_UPDATE_PERIOD); // delay 1 second and then run every MONITORING_UPDATE_PERIOD seconds
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mProcessor != null) {
            float a = event.values[ACCEL_AXIS] - 9.8f;
            mProcessor.addDataPoint(event.timestamp,  a);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        int refreshDelay = 1/(SAMPLE_RATE * 1000000); // Microseconds between samples
        mSensorManager.registerListener(this, mSensor, refreshDelay);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
