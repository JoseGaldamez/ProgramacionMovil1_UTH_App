package dev.josegaldamez.tickethn_uth.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import dev.josegaldamez.tickethn_uth.R;

/** One card layout and binding shared by every event in the mock catalogue. */
public final class EventCardView extends MaterialCardView {
    public EventCardView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_event_card, this, true);
        setCardBackgroundColor(context.getColor(R.color.home_surface));
        setRadius(12 * getResources().getDisplayMetrics().density);
        setCardElevation(0);
        setStrokeWidth(0);
        setClickable(true);
        setFocusable(true);
    }

    public void bind(Event event, OnClickListener onClick) {
        ((ImageView) findViewById(R.id.event_image)).setImageResource(event.image);
        ((TextView) findViewById(R.id.event_title)).setText(event.title);
        ((TextView) findViewById(R.id.event_date)).setText(event.date);
        ((TextView) findViewById(R.id.event_venue)).setText(event.venue + ", " + event.city);
        ((TextView) findViewById(R.id.event_price)).setText(getContext().getString(R.string.event_price, event.price));
        setOnClickListener(onClick);
    }
}

