package dev.josegaldamez.tickethn_uth;

import android.content.res.ColorStateList;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.Normalizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import dev.josegaldamez.tickethn_uth.home.Event;
import dev.josegaldamez.tickethn_uth.home.EventCardView;
import dev.josegaldamez.tickethn_uth.home.FeaturedCarousel;
import dev.josegaldamez.tickethn_uth.home.MockEventRepository;
import dev.josegaldamez.tickethn_uth.tickets.TicketsScreen;
import dev.josegaldamez.tickethn_uth.auth.LoginActivity;
import dev.josegaldamez.tickethn_uth.about.AboutActivity;
import dev.josegaldamez.tickethn_uth.details.EventDetailActivity;

public class MainActivity extends AppCompatActivity {
    private final List<Event> events = MockEventRepository.getEvents();
    private String category = "Todo";
    private String city = "";
    private EditText search;
    private ChipGroup categories;
    private TicketsScreen ticketsScreen;
    private FeaturedCarousel featuredCarousel;
    private boolean resumed;
    private int selectedTab = R.id.nav_explore;
    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    ((BottomNavigationView) findViewById(R.id.bottom_navigation))
                            .setSelectedItemId(R.id.nav_explore);
                    android.widget.Toast.makeText(this, R.string.auth_success,
                            android.widget.Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TicketHN);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        if (savedInstanceState != null) {
            category = savedInstanceState.getString("category", "Todo");
            city = savedInstanceState.getString("city", "");
        }
        search = findViewById(R.id.search);
        categories = findViewById(R.id.categories);
        featuredCarousel = new FeaturedCarousel(findViewById(R.id.featured_carousel), this::showEvent);
        createCategories();
        updateLocation();
        View.OnClickListener openAbout = view ->
                startActivity(new Intent(this, AboutActivity.class));
        findViewById(R.id.about_button).setOnClickListener(openAbout);
        findViewById(R.id.tickets_about_button).setOnClickListener(openAbout);
        findViewById(R.id.location).setOnClickListener(v -> showCities());
        findViewById(R.id.filters).setOnClickListener(v -> showCities());
        findViewById(R.id.view_all).setOnClickListener(v -> {
            city = "";
            search.setText("");
            ((Chip) categories.getChildAt(0)).setChecked(true);
            updateLocation();
            renderEvents();
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderEvents(); }
            @Override public void afterTextChanged(Editable s) {}
        });
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        ticketsScreen = new TicketsScreen(findViewById(R.id.tickets_content),
                savedInstanceState == null ? 0 : savedInstanceState.getInt("ticketPosition", 0));
        navigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_explore || item.getItemId() == R.id.nav_tickets) {
                selectedTab = item.getItemId();
                showSelectedTab();
                return true;
            }
            loginLauncher.launch(new Intent(this, LoginActivity.class));
            return false;
        });
        if (savedInstanceState != null) {
            navigation.setSelectedItemId(savedInstanceState.getInt("selectedTab", R.id.nav_explore));
        }
        renderEvents();
        if (savedInstanceState != null) {
            featuredCarousel.restorePosition(savedInstanceState.getInt("featuredPosition", 0));
        }
    }

    private void showSelectedTab() {
        boolean tickets = selectedTab == R.id.nav_tickets;
        findViewById(R.id.home_content).setVisibility(tickets ? View.GONE : View.VISIBLE);
        findViewById(R.id.tickets_content).setVisibility(tickets ? View.VISIBLE : View.GONE);
        featuredCarousel.setActive(resumed && !tickets);
        if (tickets) {
            ((android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(search.getWindowToken(), 0);
            search.clearFocus();
        }
    }

    private void createCategories() {
        ColorStateList chipBackground = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{getColor(R.color.ticket_cyan), getColor(R.color.home_background)});
        ColorStateList chipText = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{getColor(R.color.home_background), getColor(R.color.home_secondary)});
        for (String name : getResources().getStringArray(R.array.home_categories)) {
            Chip chip = new Chip(this);
            chip.setId(View.generateViewId());
            chip.setText(name);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColor(chipBackground);
            chip.setTextColor(chipText);
            chip.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.home_secondary)));
            chip.setChipStrokeWidth(dp(1));
            chip.setChipCornerRadius(dp(24));
            chip.setEnsureMinTouchTargetSize(true);
            categories.addView(chip);
            chip.setChecked(name.equals(category));
        }
        categories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                category = ((Chip) group.findViewById(checkedIds.get(0))).getText().toString();
                renderEvents();
            }
        });
    }

    private void showCities() {
        String[] options = getResources().getStringArray(R.array.home_cities);
        int selected = city.isEmpty() ? 0 : city.equals(options[1]) ? 1 : 2;
        new MaterialAlertDialogBuilder(this).setTitle(R.string.filter_city)
                .setSingleChoiceItems(options, selected, (dialog, which) -> {
                    city = which == 0 ? "" : options[which];
                    updateLocation();
                    renderEvents();
                    dialog.dismiss();
                }).setNegativeButton(R.string.action_close, null).show();
    }

    private void updateLocation() {
        ((MaterialButton) findViewById(R.id.location)).setText(
                city.isEmpty() ? getString(R.string.home_location) : city + " ▾");
    }

    private void renderEvents() {
        LinearLayout list = findViewById(R.id.event_list);
        list.removeAllViews();
        String query = normalize(search.getText().toString().trim());
        List<Event> featured = new ArrayList<>();
        int count = 0;
        for (Event event : events) {
            if (!category.equals("Todo") && !category.equals(event.category)) continue;
            if (!city.isEmpty() && !city.equals(event.city)) continue;
            if (!normalize(event.title + " " + event.venue + " " + event.city).contains(query)) continue;
            if (featured.size() < 3) featured.add(event);
            if (event.featured) continue;
            EventCardView card = new EventCardView(this);
            card.bind(event, v -> showEvent(event));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = dp(8);
            list.addView(card, params);
            count++;
        }
        featuredCarousel.submit(featured);
        findViewById(R.id.empty).setVisibility(count == 0 && featured.isEmpty() ? View.VISIBLE : View.GONE);
        findViewById(R.id.upcoming_header).setVisibility(count == 0 ? View.GONE : View.VISIBLE);
    }

    private void showEvent(Event event) {
        featuredCarousel.setActive(false);
        startActivity(EventDetailActivity.intent(this, event));
    }

    @Override protected void onResume() {
        super.onResume();
        resumed = true;
        featuredCarousel.setActive(selectedTab == R.id.nav_explore);
    }

    @Override protected void onPause() {
        resumed = false;
        featuredCarousel.setActive(false);
        super.onPause();
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    private static String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        outState.putString("category", category);
        outState.putString("city", city);
        outState.putInt("ticketPosition", ticketsScreen.getPosition());
        outState.putInt("selectedTab", selectedTab);
        outState.putInt("featuredPosition", featuredCarousel.getPosition());
        super.onSaveInstanceState(outState);
    }

    @Override protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        renderEvents();
    }
}

