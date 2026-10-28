package dev.josegaldamez.tickethn_uth.tickets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MockTicketRepository {
    private MockTicketRepository() {}
    public static List<Ticket> getTickets() {
        return Collections.unmodifiableList(Arrays.asList(
            new Ticket("Concierto Rock Fest", "Estadio Chochi Sosa, Tegucigalpa",
                "Sáb, 14 Nov 2026 • 8:00 PM", "VIP A", "D", "24", "1,200", "HN-90123"),
            new Ticket("La Tempestad", "Teatro Manuel Bonilla, Tegucigalpa",
                "Dom, 15 Nov 2026 • 6:00 PM", "Platea", "B", "12", "650", "HN-90124"),
            new Ticket("Olimpia vs Motagua", "Estadio Nacional Chelato Uclés, Tegucigalpa",
                "Sáb, 21 Nov 2026 • 7:00 PM", "Silla", "F", "08", "350", "HN-90125")
        ));
    }
}

