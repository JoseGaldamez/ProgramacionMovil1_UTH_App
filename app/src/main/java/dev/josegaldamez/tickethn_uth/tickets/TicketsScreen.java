package dev.josegaldamez.tickethn_uth.tickets;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import dev.josegaldamez.tickethn_uth.R;

/** Owns paging and its position indicator; ViewPager2 provides snapping and accessibility. */
public final class TicketsScreen {
    private final ViewPager2 pager;
    private final TextView counter;
    private final LinearLayout dots;
    private final int count;
    public TicketsScreen(View root,int initialPosition) {
        pager=root.findViewById(R.id.ticket_pager);
        counter=root.findViewById(R.id.ticket_counter);
        dots=root.findViewById(R.id.ticket_dots);
        TicketPagerAdapter adapter=new TicketPagerAdapter(MockTicketRepository.getTickets());
        count=adapter.getItemCount();
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        RecyclerView recycler=(RecyclerView)pager.getChildAt(0);
        int peek=dp(24);
        recycler.setPadding(peek,0,peek,0);
        recycler.setClipToPadding(false);
        recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        pager.setCurrentItem(Math.max(0,Math.min(initialPosition,count-1)),false);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) { updatePosition(position); }
        });
        updatePosition(pager.getCurrentItem());
    }
    public int getPosition() { return pager.getCurrentItem(); }
    private void updatePosition(int position) {
        counter.setText(counter.getContext().getString(R.string.ticket_position,position+1,count));
        dots.removeAllViews();
        for(int i=0;i<count;i++) {
            View dot=new View(dots.getContext());
            GradientDrawable circle=new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(i==position?0xFF00D8E5:0xFF1B4858);
            dot.setBackground(circle);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(dp(8),dp(8));
            params.setMargins(dp(5),0,dp(5),0);
            dots.addView(dot,params);
        }
    }
    private int dp(int value) { return Math.round(value*pager.getResources().getDisplayMetrics().density); }
}

