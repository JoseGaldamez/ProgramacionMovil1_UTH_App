package dev.josegaldamez.tickethn_uth.home;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import dev.josegaldamez.tickethn_uth.R;

public final class MockEventRepository {
    private MockEventRepository() {}
    public static List<Event> getEvents() {
        return Collections.unmodifiableList(Arrays.asList(
            new Event("GRANDES ÉXITOS EN VIVO", "Conciertos", "Sáb, 14 Nov • 8:00 PM",
                "Estadio Chochi Sosa", "Tegucigalpa", "350", R.drawable.event_concert, true),
            new Event("Obra de Teatro: La Tempestad", "Teatro", "Dom, 15 Nov • 6:00 PM",
                "Teatro Manuel Bonilla", "Tegucigalpa", "650", R.drawable.event_theater, false),
            new Event("Clásico Nacional: Olimpia vs Motagua", "Deportes", "Sáb, 21 Nov • 7:00 PM",
                "Estadio Nacional Chelato Uclés", "Tegucigalpa", "350", R.drawable.event_football, false),
            new Event("Festival del Verano 2026", "Conciertos", "Vie, 27 Nov • 4:00 PM",
                "Campo Agas", "San Pedro Sula", "1,200", R.drawable.event_concert, false),
            new Event("Noche de Artes Escénicas", "Culturales", "Sáb, 28 Nov • 7:00 PM",
                "Teatro Manuel Bonilla", "Tegucigalpa", "250", R.drawable.event_theater, false)
        ));
    }
}

