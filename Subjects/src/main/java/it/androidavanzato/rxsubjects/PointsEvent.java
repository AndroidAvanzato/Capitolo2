package it.androidavanzato.rxsubjects;

public class PointsEvent {
    private int points;

    private int gainedPoints;

    public PointsEvent(int points, int gainedPoints) {
        this.points = points;
        this.gainedPoints = gainedPoints;
    }

    public int getPoints() {
        return points;
    }

    public int getGainedPoints() {
        return gainedPoints;
    }
}
