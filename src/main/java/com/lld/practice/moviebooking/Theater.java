package com.lld.practice.moviebooking;

import java.util.List;

public class Theater {
    private String id;
    private String name;
    private List<ShowTime> showTimes;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ShowTime> getShowTimes() {
        return showTimes;
    }

    public List<ShowTime> getShowTimesByMovie(Movie movie) {
        return null;
    }
}
