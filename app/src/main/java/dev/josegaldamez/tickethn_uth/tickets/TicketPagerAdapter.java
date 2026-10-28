package dev.josegaldamez.tickethn_uth.tickets;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import dev.josegaldamez.tickethn_uth.R;

public final class TicketPagerAdapter extends RecyclerView.Adapter<TicketPagerAdapter.Holder> {
    private final List<Ticket> tickets;
    public TicketPagerAdapter(List<Ticket> tickets) { this.tickets = tickets; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket,parent,false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) { holder.bind(tickets.get(position)); }
    @Override public int getItemCount() { return tickets.size(); }
    static final class Holder extends RecyclerView.ViewHolder {
        Holder(View view) { super(view); }
        void bind(Ticket ticket) {
            text(R.id.ticket_title,ticket.title);
            text(R.id.ticket_venue,ticket.venue);
            text(R.id.ticket_date,ticket.date);
            text(R.id.ticket_price,itemView.getContext().getString(R.string.ticket_price,ticket.section,ticket.price));
            text(R.id.ticket_seat,itemView.getContext().getString(R.string.ticket_seat,ticket.section,ticket.row,ticket.seat,ticket.order));
            ((MockTicketCodeView)itemView.findViewById(R.id.ticket_code)).bind(ticket.order);
        }
        void text(int id,String value) { ((TextView)itemView.findViewById(id)).setText(value); }
    }
}

