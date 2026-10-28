package dev.josegaldamez.tickethn_uth.details;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Pure selection logic; this does not reserve inventory or charge a payment. */
public final class TicketSelection {
    private final List<PriceTier> tiers;
    private int selected, quantity = 1;
    public TicketSelection(List<PriceTier> tiers) {
        if (tiers.isEmpty()) throw new IllegalArgumentException("At least one tier is required");
        for (PriceTier tier : tiers) {
            if (tier.available < 1 || tier.price < 0) throw new IllegalArgumentException("Invalid tier");
        }
        this.tiers = Collections.unmodifiableList(new ArrayList<>(tiers));
    }
    public void select(int index) {
        if (index < 0 || index >= tiers.size()) throw new IllegalArgumentException("Invalid selection");
        selected = index;
        setQuantity(quantity);
    }
    public void setQuantity(int quantity) { this.quantity = Math.max(1, Math.min(quantity, maximum())); }
    public int maximum() { return Math.min(10, tier().available); }
    public int quantity() { return quantity; }
    public int selected() { return selected; }
    public int total() { return tier().price * quantity; }
    public PriceTier tier() { return tiers.get(selected); }
}

