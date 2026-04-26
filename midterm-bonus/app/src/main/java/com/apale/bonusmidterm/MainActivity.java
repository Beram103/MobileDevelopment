/**
 * Compound Interest Calculator
 * Name: Amber Apale (Ber)
 * Student ID: 24103140
 * Favorite Color: Purple
 * Favorite Food: Spicy Fried Chicken 🍗
 *
 * Formula: A = P(1 + r/n)^(nt)
 * where n = 365 for daily compounding
 */

package com.apale.bonusmidterm;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // === UI Components ===
    private EditText etPrincipal, etRate, etTime;
    private Spinner spinnerFrequency;
    private Button btnCalculate, btnClear;
    private TextView tvResult, tvPrincipal, tvInterest, tvFormula;
    private TextView tvWhatIfLabel, tvWhatIfResult;
    private LinearLayout layoutResults, layoutChart;
    private ProgressBar barPrincipal, barInterest;

    // === Compounding frequencies ===
    private final String[] frequencies = {"Daily (365)", "Monthly (12)", "Quarterly (4)", "Annually (1)"};
    private final int[] freqValues = {365, 12, 4, 1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupSpinner();
        setupRealTimeUpdate();
        setupButtons();
    }

    private void initViews() {
        etPrincipal       = findViewById(R.id.etPrincipal);
        etRate            = findViewById(R.id.etRate);
        etTime            = findViewById(R.id.etTime);
        spinnerFrequency  = findViewById(R.id.spinnerFrequency);
        btnCalculate      = findViewById(R.id.btnCalculate);
        btnClear          = findViewById(R.id.btnClear);
        tvResult          = findViewById(R.id.tvResult);
        tvPrincipal       = findViewById(R.id.tvPrincipalAmount);
        tvInterest        = findViewById(R.id.tvInterestEarned);
        tvFormula         = findViewById(R.id.tvFormulaDisplay);
        tvWhatIfLabel     = findViewById(R.id.tvWhatIfLabel);
        tvWhatIfResult    = findViewById(R.id.tvWhatIfResult);
        layoutResults     = findViewById(R.id.layoutResults);
        layoutChart       = findViewById(R.id.layoutChart);
        barPrincipal      = findViewById(R.id.barPrincipal);
        barInterest       = findViewById(R.id.barInterest);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, frequencies);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFrequency.setAdapter(adapter);
    }

    private void setupRealTimeUpdate() {
        // Results update in real-time as user types
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (allFieldsFilled()) calculate();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        etPrincipal.addTextChangedListener(watcher);
        etRate.addTextChangedListener(watcher);
        etTime.addTextChangedListener(watcher);

        spinnerFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (allFieldsFilled()) calculate();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void setupButtons() {
        btnCalculate.setOnClickListener(v -> {
            if (validateInputs()) {
                calculate();
                layoutResults.setVisibility(View.VISIBLE);
                layoutChart.setVisibility(View.VISIBLE);
            }
        });

        btnClear.setOnClickListener(v -> clearAll());
    }

    /**
     * Core calculation: A = P(1 + r/n)^(nt)
     * Amber's compound interest formula — Student ID: 24103140
     */
    private void calculate() {
        try {
            double P = Double.parseDouble(etPrincipal.getText().toString());
            double r = Double.parseDouble(etRate.getText().toString()) / 100.0;
            double t = Double.parseDouble(etTime.getText().toString());
            int n    = freqValues[spinnerFrequency.getSelectedItemPosition()];

            // Validate: no negatives
            if (P < 0 || r < 0 || t < 0) {
                showError("Values cannot be negative.");
                return;
            }

            // A = P(1 + r/n)^(nt)
            double A = P * Math.pow(1 + (r / n), n * t);
            double I = A - P;

            displayResults(P, A, I, r, n, t);
            displayWhatIf(P, r, n, t);
            updateVisualChart(P, I, A);

        } catch (NumberFormatException e) {
            // Silent — user still typing
        }
    }

    private void displayResults(double P, double A, double I, double r, int n, double t) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        // Animate the main result
        animateValue(0, A, value -> tvResult.setText(currency.format(value)));

        tvPrincipal.setText("Principal:  " + currency.format(P));
        tvInterest.setText("Interest Earned:  " + currency.format(I));

        // Show formula used
        String freqName = frequencies[spinnerFrequency.getSelectedItemPosition()];
        tvFormula.setText(String.format(Locale.US,
                "A = %.2f × (1 + %.4f / %d)^(%d × %.1f)", P, r, n, n, t));

        layoutResults.setVisibility(View.VISIBLE);
    }

    /**
     * "What If" feature — What if I waited 5 more years?
     * Demonstrates the power of compounding — Ber, 24103140
     */
    private void displayWhatIf(double P, double r, int n, double t) {
        double extraYears = 5;
        double A_now  = P * Math.pow(1 + (r / n), n * t);
        double A_later = P * Math.pow(1 + (r / n), n * (t + extraYears));
        double extra  = A_later - A_now;

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);
        tvWhatIfLabel.setText(String.format(Locale.US,
                "💡 What if you waited %.0f more years?", extraYears));
        tvWhatIfResult.setText(String.format(Locale.US,
                "You'd earn an extra %s — compounding really works!",
                currency.format(extra)));

        tvWhatIfLabel.setVisibility(View.VISIBLE);
        tvWhatIfResult.setVisibility(View.VISIBLE);
    }

    /**
     * Visual growth chart using ProgressBars for Principal vs Interest
     */
    private void updateVisualChart(double P, double I, double A) {
        if (A <= 0) return;
        int pPercent = (int) ((P / A) * 100);
        int iPercent = (int) ((I / A) * 100);

        barPrincipal.setProgress(pPercent);
        barInterest.setProgress(iPercent);

        layoutChart.setVisibility(View.VISIBLE);
    }

    private boolean allFieldsFilled() {
        return !etPrincipal.getText().toString().isEmpty()
                && !etRate.getText().toString().isEmpty()
                && !etTime.getText().toString().isEmpty();
    }

    private boolean validateInputs() {
        if (etPrincipal.getText().toString().isEmpty()) {
            etPrincipal.setError("Enter principal amount");
            return false;
        }
        if (etRate.getText().toString().isEmpty()) {
            etRate.setError("Enter annual interest rate");
            return false;
        }
        if (etTime.getText().toString().isEmpty()) {
            etTime.setError("Enter time in years");
            return false;
        }
        try {
            double P = Double.parseDouble(etPrincipal.getText().toString());
            double r = Double.parseDouble(etRate.getText().toString());
            double t = Double.parseDouble(etTime.getText().toString());
            if (P < 0) { etPrincipal.setError("Must be positive"); return false; }
            if (r < 0) { etRate.setError("Must be positive"); return false; }
            if (t < 0) { etTime.setError("Must be positive"); return false; }
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers.");
            return false;
        }
        return true;
    }

    private void clearAll() {
        etPrincipal.setText("");
        etRate.setText("");
        etTime.setText("");
        spinnerFrequency.setSelection(0);
        tvResult.setText("₱ 0.00");
        tvPrincipal.setText("");
        tvInterest.setText("");
        tvFormula.setText("");
        tvWhatIfLabel.setVisibility(View.GONE);
        tvWhatIfResult.setVisibility(View.GONE);
        layoutResults.setVisibility(View.GONE);
        layoutChart.setVisibility(View.GONE);
        barPrincipal.setProgress(0);
        barInterest.setProgress(0);
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Smooth number animation for the result display
     */
    private void animateValue(double from, double to, ValueCallback cb) {
        ValueAnimator animator = ValueAnimator.ofFloat((float) from, (float) to);
        animator.setDuration(800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(anim -> cb.onValue((double)(float) anim.getAnimatedValue()));
        animator.start();
    }

    interface ValueCallback {
        void onValue(double value);
    }
}