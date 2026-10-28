package dev.josegaldamez.tickethn_uth.details;

import android.content.Context;
import java.util.Arrays;
import java.util.List;
import dev.josegaldamez.tickethn_uth.R;
import dev.josegaldamez.tickethn_uth.home.Event;

final class MockEventDetailRepository {
    private MockEventDetailRepository() {}
    static List<PriceTier> tiers(Context context, Event event) {
        int base = Integer.parseInt(event.price.replace(",", ""));
        return Arrays.asList(
            new PriceTier(context.getString(R.string.detail_general), base, 145, false),
            new PriceTier(context.getString(R.string.detail_vip), Math.max(1200, base * 2), 42, false),
            new PriceTier(context.getString(R.string.detail_box), Math.max(4000, base * 4), 12, true));
    }
}

