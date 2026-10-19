package com.example.moderncalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * CalculatorEngine
 * MainActivity (Separation of Concerns)
 */
public class CalculatorEngine {

    // ── State ──
    private String currentInput = "0";
    private String operator = "";
    private double firstOperand = 0;
    private boolean isNewInput = true;
    private boolean hasResult = false;
    private String expression = "";
    private double memory = 0;
    private boolean hasMemory = false;

    // ── Angle mode ──
    public enum AngleMode { DEG, RAD }
    private AngleMode angleMode = AngleMode.DEG;

    // ── History ──
    private final List<HistoryEntry> history = new ArrayList<>();
    private static final int MAX_HISTORY = 50;

    // ══════════════════════════════════════════
    //  DIGIT INPUT
    // ══════════════════════════════════════════
    public String inputDigit(String digit) {
        if (isNewInput) {
            currentInput = digit.equals("0") ? "0" : digit;
            isNewInput = false;
            hasResult = false;
        } else {
            if (currentInput.length() >= 15) return currentInput; // max digits
            if (currentInput.equals("0") && !digit.equals(".")) {
                currentInput = digit;
            } else {
                currentInput += digit;
            }
        }
        return currentInput;
    }

    // ══════════════════════════════════════════
    //  DOT INPUT
    // ══════════════════════════════════════════
    public String inputDot() {
        if (isNewInput) {
            currentInput = "0.";
            isNewInput = false;
            return currentInput;
        }
        if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        return currentInput;
    }

    // ══════════════════════════════════════════
    //  OPERATOR
    // ══════════════════════════════════════════
    public String setOperator(String op) {
        // Chain calculation: 5 + 3 then calculate 8 first
        if (!operator.isEmpty() && !isNewInput) {
            double result = computeResult();
            currentInput = formatNumber(result);
            firstOperand = result;
        } else {
            firstOperand = parseCurrentInput();
        }
        operator = op;
        expression = formatNumber(firstOperand) + " " + op;
        isNewInput = true;
        hasResult = false;
        return expression;
    }

    // ══════════════════════════════════════════
    //  CALCULATE (= button)
    // ══════════════════════════════════════════
    public CalcResult calculate() {
        if (operator.isEmpty()) {
            return new CalcResult(parseCurrentInput(), currentInput, expression);
        }

        double second = parseCurrentInput();
        double result = computeResult(firstOperand, second, operator);

        String fullExpr = formatNumber(firstOperand) + " " + operator + " " + formatNumber(second) + " =";

        // Save to history
        HistoryEntry entry = new HistoryEntry(
                formatNumber(firstOperand) + " " + operator + " " + formatNumber(second),
                formatNumber(result)
        );
        history.add(0, entry);
        if (history.size() > MAX_HISTORY) history.remove(history.size() - 1);

        // Update state
        expression = fullExpr;
        firstOperand = result;
        currentInput = isError(result) ? "Error" : formatNumber(result);
        operator = "";
        isNewInput = true;
        hasResult = true;

        return new CalcResult(result, currentInput, fullExpr);
    }

    // ══════════════════════════════════════════
    //  SCIENTIFIC FUNCTIONS
    // ══════════════════════════════════════════
    public String applyScientific(String func) {
        double v = parseCurrentInput();
        double result;
        String label;

        switch (func) {
            case "sin":
                result = Math.sin(toRadians(v));
                label = "sin(" + formatNumber(v) + ")";
                break;
            case "cos":
                result = Math.cos(toRadians(v));
                label = "cos(" + formatNumber(v) + ")";
                break;
            case "tan":
                result = Math.tan(toRadians(v));
                label = "tan(" + formatNumber(v) + ")";
                break;
            case "asin":
                result = fromRadians(Math.asin(v));
                label = "sin⁻¹(" + formatNumber(v) + ")";
                break;
            case "acos":
                result = fromRadians(Math.acos(v));
                label = "cos⁻¹(" + formatNumber(v) + ")";
                break;
            case "atan":
                result = fromRadians(Math.atan(v));
                label = "tan⁻¹(" + formatNumber(v) + ")";
                break;
            case "log":
                result = v > 0 ? Math.log10(v) : Double.NaN;
                label = "log(" + formatNumber(v) + ")";
                break;
            case "ln":
                result = v > 0 ? Math.log(v) : Double.NaN;
                label = "ln(" + formatNumber(v) + ")";
                break;
            case "pow2":
                result = v * v;
                label = "(" + formatNumber(v) + ")²";
                break;
            case "pow3":
                result = v * v * v;
                label = "(" + formatNumber(v) + ")³";
                break;
            case "sqrt":
                result = v >= 0 ? Math.sqrt(v) : Double.NaN;
                label = "√(" + formatNumber(v) + ")";
                break;
            case "cbrt":
                result = Math.cbrt(v);
                label = "∛(" + formatNumber(v) + ")";
                break;
            case "exp":
                result = Math.exp(v);
                label = "e^(" + formatNumber(v) + ")";
                break;
            case "inv":
                result = v != 0 ? 1.0 / v : Double.NaN;
                label = "1/(" + formatNumber(v) + ")";
                break;
            case "abs":
                result = Math.abs(v);
                label = "|" + formatNumber(v) + "|";
                break;
            case "fact":
                result = factorial((long) v);
                label = formatNumber(v) + "!";
                break;
            case "pi":
                result = Math.PI;
                label = "π";
                break;
            case "euler":
                result = Math.E;
                label = "e";
                break;
            default:
                return currentInput;
        }

        if (isError(result)) {
            currentInput = "Error";
            expression = label + " = Error";
        } else {
            currentInput = formatNumber(result);
            expression = label + " =";
            firstOperand = result;
        }

        isNewInput = true;
        hasResult = true;

        // Add scientific ops to history too
        if (!isError(result)) {
            history.add(0, new HistoryEntry(label, currentInput));
            if (history.size() > MAX_HISTORY) history.remove(history.size() - 1);
        }

        return currentInput;
    }

    // ══════════════════════════════════════════
    //  MODIFIER FUNCTIONS
    // ══════════════════════════════════════════
    public String toggleSign() {
        if (currentInput.equals("0") || currentInput.equals("Error")) return currentInput;
        if (currentInput.startsWith("-")) {
            currentInput = currentInput.substring(1);
        } else {
            currentInput = "-" + currentInput;
        }
        return currentInput;
    }

    public String percent() {
        try {
            double v = Double.parseDouble(currentInput);
            // If there's a first operand, calculate relative percent
            if (!operator.isEmpty() && (operator.equals("+") || operator.equals("−"))) {
                v = firstOperand * v / 100.0;
            } else {
                v = v / 100.0;
            }
            currentInput = formatNumber(v);
            return currentInput;
        } catch (NumberFormatException e) {
            return currentInput;
        }
    }

    public String backspace() {
        if (isNewInput || hasResult) return currentInput;
        if (currentInput.length() <= 1 || currentInput.equals("-0")) {
            currentInput = "0";
        } else {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            if (currentInput.equals("-")) currentInput = "0";
        }
        return currentInput;
    }

    public void reset() {
        currentInput = "0";
        operator = "";
        firstOperand = 0;
        isNewInput = true;
        hasResult = false;
        expression = "";
    }

    // ══════════════════════════════════════════
    //  MEMORY
    // ══════════════════════════════════════════
    public void memoryStore() {
        try {
            memory = Double.parseDouble(currentInput);
            hasMemory = true;
        } catch (NumberFormatException ignored) {}
    }

    public String memoryRecall() {
        if (!hasMemory) return currentInput;
        currentInput = formatNumber(memory);
        isNewInput = false;
        hasResult = false;
        return currentInput;
    }

    public void memoryClear() {
        memory = 0;
        hasMemory = false;
    }

    public boolean hasMemory() { return hasMemory; }

    // ══════════════════════════════════════════
    //  INTERNAL HELPERS
    // ══════════════════════════════════════════
    private double computeResult() {
        return computeResult(firstOperand, parseCurrentInput(), operator);
    }

    private double computeResult(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "−": return a - b;
            case "×": return a * b;
            case "÷": return b == 0 ? Double.NaN : a / b;
            default:  return b;
        }
    }

    private double parseCurrentInput() {
        try {
            return Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double toRadians(double v) {
        return angleMode == AngleMode.DEG ? Math.toRadians(v) : v;
    }

    private double fromRadians(double v) {
        return angleMode == AngleMode.DEG ? Math.toDegrees(v) : v;
    }

    private boolean isError(double v) {
        return Double.isNaN(v) || Double.isInfinite(v);
    }

    private long factorial(long n) {
        if (n < 0 || n > 20) return -1; // -1 signals error
        long r = 1;
        for (long i = 2; i <= n; i++) r *= i;
        return r;
    }

    public String formatNumber(double num) {
        if (Double.isNaN(num) || Double.isInfinite(num)) return "Error";
        if (num == Math.floor(num) && Math.abs(num) < 1e12) {
            return String.valueOf((long) num);
        }
        // Up to 10 significant digits, trim trailing zeros
        String s = String.format("%.10f", num);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    // ══════════════════════════════════════════
    //  GETTERS / SETTERS
    // ══════════════════════════════════════════
    public String getCurrentInput()   { return currentInput; }
    public String getExpression()     { return expression; }
    public String getOperator()       { return operator; }
    public List<HistoryEntry> getHistory() { return history; }
    public AngleMode getAngleMode()   { return angleMode; }

    public void setAngleMode(AngleMode mode) { this.angleMode = mode; }

    // ── Inner classes ──

    public static class CalcResult {
        public final double value;
        public final String displayText;
        public final String expression;

        public CalcResult(double value, String displayText, String expression) {
            this.value = value;
            this.displayText = displayText;
            this.expression = expression;
        }
    }

    public static class HistoryEntry {
        public final String expression;
        public final String result;

        public HistoryEntry(String expression, String result) {
            this.expression = expression;
            this.result = result;
        }
    }
}
