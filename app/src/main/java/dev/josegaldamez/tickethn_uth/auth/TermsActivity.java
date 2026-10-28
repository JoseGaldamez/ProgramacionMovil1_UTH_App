package dev.josegaldamez.tickethn_uth.auth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import dev.josegaldamez.tickethn_uth.R;

public final class TermsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_terms);
        AuthWindow.configure(this);
        findViewById(R.id.close_terms).setOnClickListener(v -> finish());
        findViewById(R.id.terms_understood).setOnClickListener(v -> finish());
    }
}

