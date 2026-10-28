package dev.josegaldamez.tickethn_uth.auth;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import dev.josegaldamez.tickethn_uth.R;

/** Mock authentication UI. Never persists credentials or creates a real session. */
public final class LoginActivity extends AppCompatActivity {
    private TextInputEditText email, password;
    private TextInputLayout emailField, passwordField;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_login);
        AuthWindow.configure(this);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        View.OnFocusChangeListener dismissKeyboard = (view, hasFocus) -> {
            if (!hasFocus) view.post(() -> {
                if (!(getCurrentFocus() instanceof EditText)) hideKeyboard();
            });
        };
        email.setOnFocusChangeListener(dismissKeyboard);
        password.setOnFocusChangeListener(dismissKeyboard);
        findViewById(R.id.close_login).setOnClickListener(v -> finish());
        findViewById(R.id.login_submit).setOnClickListener(v -> submit());
        password.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) { submit(); return true; }
            return false;
        });
        findViewById(R.id.login_google).setOnClickListener(v -> message(R.string.auth_google_demo));
        findViewById(R.id.forgot_password).setOnClickListener(v -> {
            if (validateEmail()) message(R.string.auth_recovery_demo);
        });
        bindTermsLink();
    }
    private boolean validateEmail() {
        boolean valid = Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches();
        emailField.setError(valid ? null : getString(R.string.auth_invalid_email));
        if (!valid) email.requestFocus();
        return valid;
    }
    private void submit() {
        boolean validEmail = validateEmail();
        boolean validPassword = password.getText() != null
                && !password.getText().toString().trim().isEmpty();
        passwordField.setError(validPassword ? null : getString(R.string.auth_missing_password));
        if (!validEmail) return;
        if (!validPassword) { password.requestFocus(); return; }
        password.setText("");
        hideKeyboard();
        setResult(RESULT_OK);
        finish();
    }
    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && getCurrentFocus() instanceof EditText
                && !containsTouch(emailField, event) && !containsTouch(passwordField, event)) {
            findViewById(R.id.auth_root).requestFocus();
            hideKeyboard();
        }
        return super.dispatchTouchEvent(event);
    }
    private boolean containsTouch(View view, MotionEvent event) {
        Rect bounds = new Rect();
        return view.getGlobalVisibleRect(bounds)
                && bounds.contains((int) event.getRawX(), (int) event.getRawY());
    }
    private void hideKeyboard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(findViewById(R.id.auth_root).getWindowToken(), 0);
    }
    private void message(int body) {
        hideKeyboard();
        new MaterialAlertDialogBuilder(this).setTitle(R.string.auth_demo_title)
                .setMessage(body).setPositiveButton(R.string.action_close, null).show();
    }
    private void bindTermsLink() {
        String label = getString(R.string.terms_title);
        String text = getString(R.string.auth_consent, label);
        SpannableString link = new SpannableString(text);
        int start = text.indexOf(label);
        link.setSpan(new ClickableSpan() {
            @Override public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, TermsActivity.class));
            }
            @Override public void updateDrawState(@NonNull TextPaint paint) {
                paint.setColor(getColor(R.color.home_secondary));
                paint.setUnderlineText(true);
            }
        }, start, start + label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView terms = findViewById(R.id.terms_link);
        terms.setText(link);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

