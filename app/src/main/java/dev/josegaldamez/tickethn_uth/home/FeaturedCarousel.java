package dev.josegaldamez.tickethn_uth.home;

import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dev.josegaldamez.tickethn_uth.R;

/** Owns circular paging and a single timer, active only while Home is resumed. */
public final class FeaturedCarousel {
    private static final long INTERVAL_MS = 5000;
    private final View root;
    private final ViewPager2 pager;
    private final LinearLayout dots;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final FeaturedEventAdapter.OnEventClick click;
    private List<Event> events = Collections.emptyList();
    private FeaturedEventAdapter adapter;
    private boolean active, touching;
    private int scrollState = ViewPager2.SCROLL_STATE_IDLE;
    private final Runnable advance = this::advancePage;
    private void advancePage() {
        if (canAdvance()) pager.setCurrentItem(pager.getCurrentItem() + 1, true);
    }

    public FeaturedCarousel(View root, java.util.function.Consumer<Event> onClick) {
        this.root = root;
        click = onClick::accept;
        pager = root.findViewById(R.id.featured_pager);
        dots = root.findViewById(R.id.featured_dots);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) { updateDots(position); }
            @Override public void onPageScrollStateChanged(int state) {
                scrollState = state;
                if (state == ViewPager2.SCROLL_STATE_IDLE && events.size() > 1) {
                    int position = pager.getCurrentItem();
                    if (position == 0) pager.setCurrentItem(events.size(), false);
                    else if (position == events.size() + 1) pager.setCurrentItem(1, false);
                }
                schedule();
            }
        });
        RecyclerView recycler = (RecyclerView) pager.getChildAt(0);
        recycler.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) touching = true;
                else if (event.getActionMasked() == MotionEvent.ACTION_UP
                        || event.getActionMasked() == MotionEvent.ACTION_CANCEL) touching = false;
                schedule();
                return false;
            }
        });
    }

    public void submit(List<Event> items) {
        if (events.equals(items)) return;
        Event current = events.isEmpty() ? null : events.get(getPosition());
        events = new ArrayList<>(items);
        handler.removeCallbacks(advance);
        adapter = new FeaturedEventAdapter(events, click);
        pager.setAdapter(adapter);
        root.setVisibility(events.isEmpty() ? View.GONE : View.VISIBLE);
        int index = Math.max(0, events.indexOf(current));
        pager.setCurrentItem(events.size() > 1 ? index + 1 : 0, false);
        updateDots(pager.getCurrentItem());
        schedule();
    }

    public int getPosition() {
        return adapter == null || events.isEmpty() ? 0 : adapter.realPosition(pager.getCurrentItem());
    }
    public void restorePosition(int position) {
        if (events.isEmpty()) return;
        int index = Math.max(0, Math.min(position, events.size() - 1));
        pager.setCurrentItem(events.size() > 1 ? index + 1 : 0, false);
    }
    public void setActive(boolean active) { this.active = active; schedule(); }

    private boolean canAdvance() {
        AccessibilityManager accessibility = (AccessibilityManager)
                root.getContext().getSystemService(android.content.Context.ACCESSIBILITY_SERVICE);
        return active && !touching && events.size() > 1
                && scrollState == ViewPager2.SCROLL_STATE_IDLE
                && !accessibility.isTouchExplorationEnabled();
    }
    private void schedule() {
        handler.removeCallbacks(advance);
        if (canAdvance()) handler.postDelayed(advance, INTERVAL_MS);
    }
    private void updateDots(int position) {
        dots.removeAllViews();
        if (adapter == null || events.isEmpty()) return;
        int selected = adapter.realPosition(position);
        dots.setContentDescription(root.getContext().getString(
                R.string.featured_position, selected + 1, events.size()));
        for (int i = 0; i < events.size(); i++) {
            View dot = new View(root.getContext());
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(i == selected ? 0xFF00D8E5 : 0xFF245064);
            dot.setBackground(circle);
            int size = Math.round(6 * root.getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(size / 2, 0, size / 2, 0);
            dots.addView(dot, params);
        }
    }
}

