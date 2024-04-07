package ca.bcit.soilmonitor;

public class Scheduler {
    public int hour;
    public int minute;
    public boolean waterOn;

    public Scheduler(int hour, int minute, boolean waterOn) {
        this.hour = hour;
        this.minute = minute;
        this.waterOn = waterOn;
    }
    // Getters and setters if necessary
}

