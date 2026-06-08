package com.lld.practice.moviebooking;

import java.util.List;

public class Reservation {
    private String confirmationId;
    private ShowTime showTime;
    private List<String> seatIds;

    public String getConfirmationId() {
        return confirmationId;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }
}
