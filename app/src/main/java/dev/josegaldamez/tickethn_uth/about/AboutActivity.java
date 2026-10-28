package dev.josegaldamez.tickethn_uth.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import dev.josegaldamez.tickethn_uth.R;

public final class AboutActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_about);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_root), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        findViewById(R.id.about_back).setOnClickListener(view -> finish());
        findViewById(R.id.about_contact).setOnClickListener(view -> {
            Intent contact = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_contact_url)));
            try {
                startActivity(contact);
            } catch (ActivityNotFoundException exception) {
                Toast.makeText(this, R.string.about_no_browser, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

