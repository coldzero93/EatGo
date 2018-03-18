package com.eatgo.eatgo;


import java.util.ArrayList;

public class Eatery {

    private String name;
    private int tot;
    private double avg;
    private int numExp;
    private ArrayList<String> whoExp = new ArrayList<String>();

    public Eatery() {

    }

    public Eatery(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTot() {
        return tot;
    }

    public void setTot(int tot) {
        this.tot += tot;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(int totalNumber) {
        this.avg = (totalNumber != this.numExp) ? (double) this.tot / (totalNumber - this.numExp) : 0;
    }

    public int getNumExp() {
        return numExp;
    }

    public void setNumExp(int numExp) {
        this.numExp += numExp;
    }

    public ArrayList<String> getWhoExp() {
        return whoExp;
    }

    public void setWhoExp(String whoExp) {
        this.whoExp.add(whoExp);
    }
}
