package dev.josegaldamez.tickethn_uth.details;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import dev.josegaldamez.tickethn_uth.BuildConfig;
import dev.josegaldamez.tickethn_uth.R;
import dev.josegaldamez.tickethn_uth.home.Event;

public final class EventDetailActivity extends AppCompatActivity {
    private static final String EXTRA_EVENT = "event";
    private Event event;
    private TicketSelection selection;
    private final NumberFormat money = NumberFormat.getIntegerInstance(Locale.US);
    private boolean mapLoaded;
    private final Runnable mapTimeout = () -> {
        if (!mapLoaded) {
            text(R.id.map_status, getString(R.string.detail_map_unavailable));
        }
    };

    public static Intent intent(Context context, Event event) {
        return new Intent(context, EventDetailActivity.class).putExtra(EXTRA_EVENT, event);
    }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        event = (Event) getIntent().getSerializableExtra(EXTRA_EVENT);
        if (event == null) { finish(); return; }
        setContentView(R.layout.activity_event_detail);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_root), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        ((ImageView) findViewById(R.id.detail_image)).setImageResource(event.image);
        text(R.id.detail_event_title, event.title);
        text(R.id.detail_date, event.date);
        text(R.id.detail_location, event.venue + ", " + event.city);
        text(R.id.detail_venue, event.venue);
        findViewById(R.id.detail_back).setOnClickListener(v -> finish());
        findViewById(R.id.detail_share).setOnClickListener(v -> share());
        findViewById(R.id.open_maps).setOnClickListener(v -> openMaps());

        List<PriceTier> tiers = MockEventDetailRepository.tiers(this, event);
        selection = new TicketSelection(tiers);
        if (state != null) {
            selection.select(Math.max(0, Math.min(state.getInt("tier"), tiers.size() - 1)));
            selection.setQuantity(state.getInt("quantity", 1));
        }
        bindTiers(tiers);
        findViewById(R.id.quantity_minus).setOnClickListener(v -> changeQuantity(-1));
        findViewById(R.id.quantity_plus).setOnClickListener(v -> changeQuantity(1));
        findViewById(R.id.detail_buy).setOnClickListener(v -> previewPurchase());
        updateSelection();
        configureMap();
    }

    private void bindTiers(List<PriceTier> tiers) {
        RadioGroup group = findViewById(R.id.detail_tiers);
        ColorStateList colors = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{getColor(R.color.ticket_cyan), getColor(R.color.home_text)});
        for (int i = 0; i < tiers.size(); i++) {
            PriceTier tier = tiers.get(i);
            MaterialRadioButton option = new MaterialRadioButton(this);
            option.setId(View.generateViewId());
            option.setTag(i);
            option.setText(getString(R.string.detail_tier, tier.name, money.format(tier.price),
                    tier.available, getString(tier.packageOfFour
                            ? R.string.detail_packages_unit : R.string.detail_tickets_unit)));
            option.setTextSize(16);
            option.setTextColor(colors);
            option.setButtonTintList(colors);
            // CompoundButton positions its graphic independently of the text padding.
            option.setButtonDrawable(new InsetDrawable(option.getButtonDrawable(),
                    dp(12), 0, dp(4), 0));
            option.setBackgroundResource(R.drawable.bg_tier);
            option.setPaddingRelative(dp(14), dp(16), dp(20), dp(16));
            option.setMinHeight(dp(78));
            option.setElevation(0);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = dp(8);
            group.addView(option, params);
            option.setChecked(i == selection.selected());
        }
        group.setOnCheckedChangeListener((view, checkedId) -> {
            View option = view.findViewById(checkedId);
            if (option == null) return;
            selection.select((Integer) option.getTag());
            updateSelection();
        });
    }

    private void changeQuantity(int delta) {
        selection.setQuantity(selection.quantity() + delta);
        updateSelection();
    }

    private void updateSelection() {
        text(R.id.quantity_value, String.valueOf(selection.quantity()));
        findViewById(R.id.quantity_value).setContentDescription(
                getString(R.string.detail_quantity, selection.quantity()));
        View minus = findViewById(R.id.quantity_minus);
        View plus = findViewById(R.id.quantity_plus);
        minus.setEnabled(selection.quantity() > 1);
        plus.setEnabled(selection.quantity() < selection.maximum());
        minus.setAlpha(minus.isEnabled() ? 1f : .35f);
        plus.setAlpha(plus.isEnabled() ? 1f : .35f);
        MaterialButton buy = findViewById(R.id.detail_buy);
        buy.setText(getString(R.string.detail_buy_total, money.format(selection.total())));
    }

    private void previewPurchase() {
        new MaterialAlertDialogBuilder(this).setTitle(R.string.detail_preview_title)
                .setMessage(getString(R.string.detail_preview, event.title, selection.tier().name,
                        selection.quantity(), money.format(selection.total())))
                .setPositiveButton(R.string.action_close, null).show();
    }

    private void share() {
        Intent share = new Intent(Intent.ACTION_SEND).setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.detail_share_text,
                        event.title, event.date, event.venue, event.city, event.price));
        startActivity(Intent.createChooser(share, getString(R.string.detail_share)));
    }

    private void openMaps() {
        Uri uri = Uri.parse("https://www.google.com/maps/search/").buildUpon()
                .appendQueryParameter("api", "1")
                .appendQueryParameter("query", event.venue + ", " + event.city + ", Honduras").build();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, R.string.about_no_browser, Toast.LENGTH_SHORT).show();
        }
    }

    private void configureMap() {
        // No real key is shipped. Keep the external Maps action usable without configuration.
        if (!BuildConfig.MAPS_CONFIGURED) return;
        text(R.id.map_status, getString(R.string.detail_map_loading));
        findViewById(R.id.map_placeholder).postDelayed(mapTimeout, 15000);
        SupportMapFragment fragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_map, fragment).commitNow();
        }
        fragment.getMapAsync(map -> {
            // Demo city-centre coordinates, explicitly labelled as a reference location in the UI.
            LatLng reference = event.city.equals("San Pedro Sula")
                    ? new LatLng(15.5042, -88.0250) : new LatLng(14.0723, -87.1921);
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.event_map_style));
            map.addMarker(new MarkerOptions().position(reference).title(event.city));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(reference, 13));
            map.getUiSettings().setAllGesturesEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.setOnMapLoadedCallback(() -> {
                mapLoaded = true;
                findViewById(R.id.map_placeholder).removeCallbacks(mapTimeout);
                findViewById(R.id.detail_map).setVisibility(View.VISIBLE);
                findViewById(R.id.map_placeholder).setVisibility(View.GONE);
            });
        });
    }

    private void text(int id, String value) { ((TextView) findViewById(id)).setText(value); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    @Override protected void onDestroy() {
        View placeholder = findViewById(R.id.map_placeholder);
        if (placeholder != null) placeholder.removeCallbacks(mapTimeout);
        super.onDestroy();
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        if (selection != null) {
            state.putInt("tier", selection.selected());
            state.putInt("quantity", selection.quantity());
        }
        super.onSaveInstanceState(state);
    }
}

