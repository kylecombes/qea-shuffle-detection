package com.kylecombes.qeashuffledetector;

import java.util.ArrayList;


public class AccelDataProcessor {

    private static final float MIN_PEAK_DELTA = 0.1f; // m/s^2
    private int mPointsToProcess;
    private ArrayList<Long> times;
    private ArrayList<Float> values;

    /**
     * Creates a new AccelDataProcessor for processing accelerometer data in one dimension.
     * @param pointsToProcess the number of the most recent data points to consider when performing
     *                        an analysis
     */
    public AccelDataProcessor(int pointsToProcess) {
        mPointsToProcess = pointsToProcess;
        times = new ArrayList<>();
        values = new ArrayList<>();
    }

    public void addDataPoint(Long x, Float y) {
        times.add(x);
        values.add(y);
        if (times.size() > mPointsToProcess) { // Check if we're at our maximum number of points to keep track of
            // Remove the oldest point
            times.remove(0);
            values.remove(0);
        }
    }

    public float getAveragePeakValue() {
        // Find the maxima and minima
        MaxMinContainer maxMinContainer = PeakDetector.detectPeaks(values, MIN_PEAK_DELTA, times);

        Object[] maxima = maxMinContainer.getMaxima();
        ArrayList<Float> yVals = (ArrayList<Float>) maxima[1];

        if (yVals == null) { // Deviation did not exceed MIN_PEAK_DELTA
            return values.get(0); // Just return the first value, since they're all the same
        }

        // Calculate average value
        float sum = 0;
        int numPoints = yVals.size();
        for (short i = 0; i < numPoints; ++i) {
            sum += yVals.get(i);
        }
        return sum / ((float) numPoints);

    }

    public float getAverageTroughValue() {
        // Find the maxima and minima
        MaxMinContainer maxMinContainer = PeakDetector.detectPeaks(values, MIN_PEAK_DELTA, times);

        Object[] minima = maxMinContainer.getMaxima();
        ArrayList<Float> yVals = (ArrayList<Float>) minima[1];

        if (yVals == null) { // Deviation did not exceed MIN_PEAK_DELTA
            return values.get(0); // Just return the first value, since they're all the same
        }

        // Calculate average value
        float sum = 0f;
        int numPoints = yVals.size();
        for (short i = 0; i < numPoints; ++i) {
            sum += yVals.get(i);
        }
        return sum / ((float) numPoints);

    }

    public void clearDataPoints() {
        times.clear();
        values.clear();
    }

}
