package dev.josegaldamez.tickethn_uth.tickets;

/** Immutable ticket data, independent of the view and the future API. */
public final class Ticket {
    public final String title, venue, date, section, row, seat, price, order;
    public Ticket(String title, String venue, String date, String section,
                  String row, String seat, String price, String order) {
        this.title = title; this.venue = venue; this.date = date;
        this.section = section; this.row = row; this.seat = seat;
        this.price = price; this.order = order;
    }
}

