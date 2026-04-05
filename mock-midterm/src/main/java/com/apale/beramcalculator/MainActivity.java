package com.apale.beramcalculator;

// Amber Meigoumi S. Apale | Student ID: 24103140 | Nickname: beram
// CIS 2203N - Mobile Development | Midterm Project

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // --- UI Elements ---
    private TextView tvDisplay, tvExpression, tvHistory;

    // --- State Variables (saved on rotation in Milestone 3) ---
    private String displayValue = "0";
    private double operandA = 0;
    private String operator = "";
    private boolean hasOperator = false;
    private boolean justEvaluated = false;
    private boolean dotUsed = false;
    private String expressionStr = "";
    private StringBuilder historyLog = new StringBuilder();

    // --- BERAM Custom Operator ---
    // Student ID: 24103140 → last 3 digits = 140 → multiplier = 1.40
    // This is Amber's unique custom operator named after her nickname "beram"
    private static final double BERAM_MULTIPLIER = 1.40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore state if coming back from rotation
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        // Link UI elements
        tvDisplay    = findViewById(R.id.tvDisplay);
        tvExpression = findViewById(R.id.tvExpression);
        tvHistory    = findViewById(R.id.tvHistory);

        // Assign click listeners to every button
        int[] buttonIds = {
                R.id.btnAC, R.id.btnSign, R.id.btnMod,
                R.id.btnDiv, R.id.btnMul, R.id.btnSub, R.id.btnAdd,
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnEq, R.id.btnBeram
        };
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }

        updateDisplay();
    }

    // --- Handle all button clicks ---
    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        String label = btn.getText().toString();

        if (v.getId() == R.id.btnAC) {
            handleClear();
        } else if (v.getId() == R.id.btnSign) {
            handleSign();
        } else if (v.getId() == R.id.btnMod) {
            handlePercent();
        } else if (v.getId() == R.id.btnBeram) {
            handleBeramOperator();
        } else if (v.getId() == R.id.btnEq) {
            handleEquals();
        } else if (v.getId() == R.id.btnDiv || v.getId() == R.id.btnMul
                || v.getId() == R.id.btnSub || v.getId() == R.id.btnAdd) {
            handleOperator(label);
        } else if (v.getId() == R.id.btnDot) {
            handleDot();
        } else {
            // It's a number button (0-9)
            handleNumber(label);
        }

        updateDisplay();
    }

    // --- Input Handlers ---

    private void handleNumber(String digit) {
        if (justEvaluated) {
            displayValue = "0";
            justEvaluated = false;
            dotUsed = false;
        }
        if (displayValue.equals("0")) {
            displayValue = digit;
        } else {
            displayValue = displayValue + digit;
        }
    }

    private void handleDot() {
        if (dotUsed) return;
        dotUsed = true;
        if (justEvaluated) {
            displayValue = "0";
            justEvaluated = false;
        }
        displayValue = displayValue + ".";
    }

    private void handleOperator(String op) {
        operandA = Double.parseDouble(displayValue);
        operator = op;
        hasOperator = true;
        expressionStr = formatNumber(operandA) + " " + op;
        justEvaluated = false;
        dotUsed = false;
        displayValue = "0";
    }

    private void handleEquals() {
        if (!hasOperator) return;

        double operandB = Double.parseDouble(displayValue);
        String fullExpr = expressionStr + " " + formatNumber(operandB);
        double result;

        // Division by Zero check — required by Milestone 2
        if (operator.equals("÷") && operandB == 0) {
            tvDisplay.setTextColor(getResources().getColor(R.color.error_red, null));
            tvDisplay.setText("Cannot divide by zero");
            tvExpression.setText("");
            resetState();
            return;
        }

        // Perform arithmetic
        switch (operator) {
            case "÷": result = operandA / operandB; break;
            case "×": result = operandA * operandB; break;
            case "−": result = operandA - operandB; break;
            case "+": result = operandA + operandB; break;
            default:  result = operandB;
        }

        String resultStr = formatNumber(result);
        historyLog.insert(0, fullExpr + " = " + resultStr + "\n");

        expressionStr = fullExpr + " =";
        displayValue = resultStr;
        operandA = result;
        hasOperator = false;
        justEvaluated = true;
        dotUsed = displayValue.contains(".");
    }

    private void handleClear() {
        displayValue = "0";
        operandA = 0;
        operator = "";
        hasOperator = false;
        justEvaluated = false;
        dotUsed = false;
        expressionStr = "";
        tvDisplay.setTextColor(getResources().getColor(R.color.purple_text, null));
    }

    private void handleSign() {
        if (!displayValue.equals("0")) {
            if (displayValue.startsWith("-")) {
                displayValue = displayValue.substring(1);
            } else {
                displayValue = "-" + displayValue;
            }
        }
    }

    private void handlePercent() {
        double val = Double.parseDouble(displayValue);
        displayValue = formatNumber(val / 100.0);
        dotUsed = displayValue.contains(".");
    }

    // --- BERAM Custom Operator ---
    // Based on Student ID 24103140, last 3 digits = 140, so multiplier = 1.40
    // Named "BERAM" after Amber's nickname
    private void handleBeramOperator() {
        double val = Double.parseDouble(displayValue);
        double result = val * BERAM_MULTIPLIER;
        String expr = formatNumber(val) + " × 1.40 [BERAM]";
        String resultStr = formatNumber(result);
        historyLog.insert(0, expr + " = " + resultStr + "\n");
        expressionStr = expr + " =";
        displayValue = resultStr;
        justEvaluated = true;
        dotUsed = displayValue.contains(".");
    }

    // --- Helpers ---

    private void resetState() {
        operandA = 0;
        operator = "";
        hasOperator = false;
        justEvaluated = true;
    }

    // Formats doubles — removes trailing .0 for whole numbers
    private String formatNumber(double num) {
        if (num == (long) num) {
            return String.valueOf((long) num);
        } else {
            return String.valueOf(num);
        }
    }

    private void updateDisplay() {
        tvDisplay.setTextColor(getResources().getColor(R.color.purple_text, null));
        tvDisplay.setText(displayValue);
        tvExpression.setText(expressionStr);
        String histText = historyLog.toString().trim();
        tvHistory.setText(histText.length() > 0 ? histText.split("\n")[0] : "");
    }

    // --- Milestone 3: State Preservation ---
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("displayValue", displayValue);
        outState.putDouble("operandA", operandA);
        outState.putString("operator", operator);
        outState.putBoolean("hasOperator", hasOperator);
        outState.putBoolean("justEvaluated", justEvaluated);
        outState.putBoolean("dotUsed", dotUsed);
        outState.putString("expressionStr", expressionStr);
        outState.putString("historyLog", historyLog.toString());
    }

    private void restoreState(Bundle state) {
        displayValue   = state.getString("displayValue", "0");
        operandA       = state.getDouble("operandA", 0);
        operator       = state.getString("operator", "");
        hasOperator    = state.getBoolean("hasOperator", false);
        justEvaluated  = state.getBoolean("justEvaluated", false);
        dotUsed        = state.getBoolean("dotUsed", false);
        expressionStr  = state.getString("expressionStr", "");
        historyLog     = new StringBuilder(state.getString("historyLog", ""));
    }
}
