package com.lalitadithya;

import java.io.Serializable;

public class Seat implements Serializable {
    private String clientName;
    private long lastHeartbeat;
    private boolean isBooked;

    public Seat(String clientName, long lastHeartbeat, boolean isBooked) {
        this.clientName = clientName;
        this.lastHeartbeat = lastHeartbeat;
        this.isBooked = isBooked;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
