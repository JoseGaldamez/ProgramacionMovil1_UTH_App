package dev.josegaldamez.tickethn_uth.details;

public final class PriceTier {
    public final String name;
    public final int price, available;
    public final boolean packageOfFour;
    public PriceTier(String name, int price, int available, boolean packageOfFour) {
        this.name = name; this.price = price; this.available = available;
        this.packageOfFour = packageOfFour;
    }
}

