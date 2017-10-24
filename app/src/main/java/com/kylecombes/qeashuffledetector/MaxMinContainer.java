package com.kylecombes.qeashuffledetector;

import java.util.ArrayList;

public class MaxMinContainer {

    private ArrayList<Long> maximaX = new ArrayList<>();
    private ArrayList<Float> maximaY = new ArrayList<>();
    private ArrayList<Long> minimaX = new ArrayList<>();
    private ArrayList<Float> minimaY = new ArrayList<>();

    public void addMax(Long x, Float y) {
        maximaX.add(x);
        maximaY.add(y);
    }

    public void addMin(Long x, Float y) {
        minimaX.add(x);
        minimaY.add(y);
    }

    public Object[] getMaxima() {
        return new Object[]{maximaX, maximaY};
    }

    public Object[] getMinima() {
        return new Object[]{minimaX, minimaY};
    }

}
