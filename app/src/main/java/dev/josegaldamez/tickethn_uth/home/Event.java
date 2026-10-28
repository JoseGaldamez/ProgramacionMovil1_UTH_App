package dev.josegaldamez.tickethn_uth.home;

import androidx.annotation.DrawableRes;

/** Immutable presentation data; replace the mock repository when the API is ready. */
public final class Event implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public final String title, category, date, venue, city, price;
    @DrawableRes public final int image;
    public final boolean featured;

    public Event(String title, String category, String date, String venue,
                 String city, String price, int image, boolean featured) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.venue = venue;
        this.city = city;
        this.price = price;
        this.image = image;
        this.featured = featured;
    }
}

