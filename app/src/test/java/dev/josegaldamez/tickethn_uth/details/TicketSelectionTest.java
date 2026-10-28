package dev.josegaldamez.tickethn_uth.details;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;

public class TicketSelectionTest {
    @Test public void quantityRespectsStockAndOrderLimit() {
        TicketSelection selection = new TicketSelection(Arrays.asList(
                new PriceTier("General", 350, 145, false),
                new PriceTier("VIP", 1200, 2, false)));
        selection.setQuantity(50);
        assertEquals(10, selection.quantity());
        selection.select(1);
        assertEquals(2, selection.quantity());
        selection.setQuantity(0);
        assertEquals(1, selection.quantity());
    }
    @Test public void totalUsesSelectedTierAndCountsPackagesAsUnits() {
        TicketSelection selection = new TicketSelection(Arrays.asList(
                new PriceTier("General", 350, 145, false),
                new PriceTier("Palco", 4000, 12, true)));
        selection.setQuantity(3);
        assertEquals(1050, selection.total());
        selection.select(1);
        assertEquals(12000, selection.total());
    }
}

