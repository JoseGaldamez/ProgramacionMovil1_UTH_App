package dev.josegaldamez.tickethn_uth.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import dev.josegaldamez.tickethn_uth.R;

final class FeaturedEventAdapter extends RecyclerView.Adapter<FeaturedEventAdapter.Holder> {
    interface OnEventClick { void onClick(Event event); }
    private final List<Event> events;
    private final OnEventClick click;
    FeaturedEventAdapter(List<Event> events, OnEventClick click) {
        this.events = events;
        this.click = click;
    }
    // Duplicate the end pages so both directions wrap without animating backwards.
    int realPosition(int position) {
        return events.size() <= 1 ? 0 : Math.floorMod(position - 1, events.size());
    }
    @Override public int getItemCount() {
        return events.size() <= 1 ? events.size() : events.size() + 2;
    }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_event, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        Event event = events.get(realPosition(position));
        ((ImageView) holder.itemView.findViewById(R.id.featured_image)).setImageResource(event.image);
        ((TextView) holder.itemView.findViewById(R.id.featured_title)).setText(event.title);
        ((TextView) holder.itemView.findViewById(R.id.featured_meta))
                .setText(event.date + " • " + event.venue + ", " + event.city);
        ((TextView) holder.itemView.findViewById(R.id.featured_price))
                .setText(holder.itemView.getContext().getString(R.string.event_price, event.price));
        holder.itemView.setOnClickListener(v -> click.onClick(event));
    }
    static final class Holder extends RecyclerView.ViewHolder {
        Holder(View view) { super(view); }
    }
}

