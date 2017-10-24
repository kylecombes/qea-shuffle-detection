package com.kylecombes.qeashuffledetector;

import java.util.List;

/**
 * Adapted from https://gist.github.com/tiraeth/1306602
 */

public class PeakDetector {

    /**
     * Detects peaks (calculates local minima and maxima) in the
     * vector <code>values</code>. The resulting list contains
     * maxima at the first position and minima at the last one.
     *
     * Maxima and minima maps contain the indice value for a
     * given position and the value from a corresponding vector.
     *
     * A point is considered a maximum peak if it has the maximal
     * value, and was preceded (to the left) by a value lower by
     * <code>delta</code>.
     *
     * @param values Vector of values for whom the peaks should be detected
     * @param delta The precedor of a maximum peak
     * @param indices Vector of indices that replace positions in resulting maps
     * @return List of maps (maxima and minima pairs) of detected peaks
     */
    public static MaxMinContainer detectPeaks(List<Float> values, Float delta, List<Long> indices)
    {
        MaxMinContainer maxMinContainer = new MaxMinContainer();

        Float maximum = null;
        Float minimum = null;
        Long maximumPos = null;
        Long minimumPos = null;

        boolean lookForMax = true; // values.get(0) < values.get(1);

        for (short i = 0; i < values.size(); ++i) {
            Float value = values.get(i);
            if (maximum == null || value > maximum) {
                maximum = value;
                maximumPos = indices.get(i);
            }

            if (minimum == null || value < minimum) {
                minimum = value;
                minimumPos = indices.get(i);
            }

            if (lookForMax) { // Currently looking for a maximum
                if (value < maximum - delta) {
                    maxMinContainer.addMax(maximumPos, value);
                    minimum = value;
                    minimumPos = indices.get(i);
                    lookForMax = false;
                }
            } else { // Currently looking for a minimum
                if (value > minimum + delta) {
                    maxMinContainer.addMin(minimumPos, value);
                    maximum = value;
                    maximumPos = indices.get(i);
                    lookForMax = true;
                }
            }
        }

        return maxMinContainer;
    }

}
