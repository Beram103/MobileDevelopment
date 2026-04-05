# BeramCalculator
**Amber Meigoumi S. Apale | Student ID: 24103140 | Nickname: beram**
CIS 2203N - Mobile Development | Midterm Project

## Implementation Choices

### UI Layout (Milestone 1)
I used `LinearLayout` with `GridLayout` for the button grid.
GridLayout with `columnCount=4` and `layout_columnWeight` lets each
button share space evenly without nesting extra layouts.
The color theme is purple — my favorite color — applied through
a custom style resource (`CalcBtnBase` and its children).

### Core Logic (Milestone 2)
All logic is in `MainActivity.java` written in Java.
I store `operandA` (a double) and `operator` (a String) when an
operator button is pressed. On `=`, I switch on the operator string
and compute the result. Division by zero is caught with an explicit
`if (operandB == 0)` check before any division happens, displaying
"Cannot divide by zero" in red instead of crashing.

### State Preservation (Milestone 3)
I used `onSaveInstanceState(Bundle)` to save all 8 state variables
(display value, operands, operator flag, dot flag, expression string,
history). On `onCreate`, I call `restoreState(savedInstanceState)`
if the bundle is non-null. This keeps the calculator state intact
through rotation and backgrounding.

### Custom BERAM Operator
My Student ID is 24103140. The last 3 digits are **140**, so the
multiplier is **1.40**. The button is named **BERAM** (my nickname).
Pressing it multiplies the current display value by 1.40.
```java
private static final double BERAM_MULTIPLIER = 1.40;
private void handleBeramOperator() {
    double result = Double.parseDouble(displayValue) * BERAM_MULTIPLIER;
    ...
}
```
