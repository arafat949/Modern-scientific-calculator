package com.example.moderncalculator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // ── Display views ──
    private TextView tvResult, tvExpression, tvHistory;
    private TextView tvDeg, tvRad, tvMemIndicator, tvHistoryIcon;

    // ── Mode toggle buttons ──
    private Button btnModeBasic, btnModeSci, btnModeShift;

    // ── Scientific section ──
    private TableLayout sciSection;

    // ── Main buttons ──
    private Button btnClear, btnBack, btnPercent;
    private Button btnDivide, btnMultiply, btnSubtract, btnAdd;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnDot, btnEquals;

    // ── Scientific buttons ──
    private Button btnSin, btnCos, btnTan, btnLog, btnLn;
    private Button btnAsin, btnAcos, btnAtan, btnPow2, btnSqrt;
    private Button btnPow3, btnCbrt, btnExp, btnPi, btnEuler;
    private Button btnInv, btnAbs, btnFact, btnMS, btnMR;

    // ── Engine ──
    private CalculatorEngine engine;

    // ── State ──
    private boolean isSciMode = false;
    private boolean isShiftMode = false;
    private String activeOperator = "";

    // ── Vibrator ──
    private Vibrator vibrator;

    // ══════════════════════════════════════════
    //  LIFECYCLE
    // ══════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(android.R.id.content),
                (v, insets) -> {
                    int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                    v.setPadding(0, top, 0, 0);
                    return insets;
                });

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        engine = new CalculatorEngine();

        initViews();
        setListeners();
    }

    // ══════════════════════════════════════════
    //  INIT VIEWS
    // ══════════════════════════════════════════
    private void initViews() {
        // Display
        tvResult       = findViewById(R.id.tvResult);
        tvExpression   = findViewById(R.id.tvExpression);
        tvHistory      = findViewById(R.id.tvHistory);
        tvDeg          = findViewById(R.id.tvDeg);
        tvRad          = findViewById(R.id.tvRad);
        tvMemIndicator = findViewById(R.id.tvMemIndicator);
        tvHistoryIcon  = findViewById(R.id.tvHistoryIcon);

        // Mode toggles
        btnModeBasic = findViewById(R.id.btnModeBasic);
        btnModeSci   = findViewById(R.id.btnModeSci);
        btnModeShift = findViewById(R.id.btnModeShift);

        // Scientific table
        sciSection = findViewById(R.id.sciSection);

        // Main buttons
        btnClear    = findViewById(R.id.btnClear);
        btnBack     = findViewById(R.id.btnBack);
        btnPercent  = findViewById(R.id.btnPercent);
        btnDivide   = findViewById(R.id.btnDivide);
        btnMultiply = findViewById(R.id.btnMultiply);
        btnSubtract = findViewById(R.id.btnSubtract);
        btnAdd      = findViewById(R.id.btnAdd);
        btn0 = findViewById(R.id.btn0);  btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);  btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);  btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);  btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);  btn9 = findViewById(R.id.btn9);
        btnDot    = findViewById(R.id.btnDot);
        // btnToggle = findViewById(R.id.btnToggle);
        btnEquals = findViewById(R.id.btnEquals);

        // Scientific buttons
        btnSin  = findViewById(R.id.btnSin);  btnCos  = findViewById(R.id.btnCos);
        btnTan  = findViewById(R.id.btnTan);  btnLog  = findViewById(R.id.btnLog);
        btnLn   = findViewById(R.id.btnLn);   btnAsin = findViewById(R.id.btnAsin);
        btnAcos = findViewById(R.id.btnAcos); btnAtan = findViewById(R.id.btnAtan);
        btnPow2 = findViewById(R.id.btnPow2); btnSqrt = findViewById(R.id.btnSqrt);
        btnPow3 = findViewById(R.id.btnPow3); btnCbrt = findViewById(R.id.btnCbrt);
        btnExp  = findViewById(R.id.btnExp);  btnPi   = findViewById(R.id.btnPi);
        btnEuler= findViewById(R.id.btnEuler);btnInv  = findViewById(R.id.btnInv);
        btnAbs  = findViewById(R.id.btnAbs);  btnFact = findViewById(R.id.btnFact);
        btnMS   = findViewById(R.id.btnMS);   btnMR   = findViewById(R.id.btnMR);
    }

    // ══════════════════════════════════════════
    //  LISTENERS
    // ══════════════════════════════════════════
    private void setListeners() {

        // ── Number buttons ──
        View.OnClickListener numListener = v -> {
            haptic();
            Button b = (Button) v;
            String result = engine.inputDigit(b.getText().toString());
            updateResultDisplay(result);
            updateClearBtn();
            animateResult();
        };

        btn0.setOnClickListener(numListener); btn1.setOnClickListener(numListener);
        btn2.setOnClickListener(numListener); btn3.setOnClickListener(numListener);
        btn4.setOnClickListener(numListener); btn5.setOnClickListener(numListener);
        btn6.setOnClickListener(numListener); btn7.setOnClickListener(numListener);
        btn8.setOnClickListener(numListener); btn9.setOnClickListener(numListener);

        btnDot.setOnClickListener(v -> {
            haptic();
            updateResultDisplay(engine.inputDot());
        });

        // ── Operator buttons ──
        View.OnClickListener opListener = v -> {
            haptic();
            Button b = (Button) v;
            String op = b.getText().toString();
            String expr = engine.setOperator(op);
            tvExpression.setText(expr);
            activeOperator = op;
            updateOperatorHighlight();
            btnClear.setText("C");
        };

        btnAdd.setOnClickListener(opListener);
        btnSubtract.setOnClickListener(opListener);
        btnMultiply.setOnClickListener(opListener);
        btnDivide.setOnClickListener(opListener);

        // ── Equals ──
        btnEquals.setOnClickListener(v -> {
            hapticStrong();
            CalculatorEngine.CalcResult res = engine.calculate();
            tvExpression.setText(res.expression);
            updateResultDisplay(res.displayText);
            updateHistoryDisplay();
            activeOperator = "";
            updateOperatorHighlight();
            btnClear.setText("AC");
            animateResult();
        });

        // ── Clear ──
        btnClear.setOnClickListener(v -> {
            haptic();
            engine.reset();
            updateResultDisplay("0");
            tvExpression.setText("");
            activeOperator = "";
            updateOperatorHighlight();
            btnClear.setText("AC");
        });

        // ── Backspace ──
        btnBack.setOnClickListener(v -> {
            haptic();
            updateResultDisplay(engine.backspace());
        });

        // ── Percent ──
        btnPercent.setOnClickListener(v -> {
            haptic();
            updateResultDisplay(engine.percent());
        });

        // ── Toggle sign ──
        // btnToggle.setOnClickListener(v -> {
        //     haptic();
        //     updateResultDisplay(engine.toggleSign());
        // });

        // ── Scientific buttons ──
        setupSciButton(btnSin,   "sin");
        setupSciButton(btnCos,   "cos");
        setupSciButton(btnTan,   "tan");
        setupSciButton(btnLog,   "log");
        setupSciButton(btnLn,    "ln");
        setupSciButton(btnAsin,  "asin");
        setupSciButton(btnAcos,  "acos");
        setupSciButton(btnAtan,  "atan");
        setupSciButton(btnPow2,  "pow2");
        setupSciButton(btnSqrt,  "sqrt");
        setupSciButton(btnPow3,  "pow3");
        setupSciButton(btnCbrt,  "cbrt");
        setupSciButton(btnExp,   "exp");
        setupSciButton(btnInv,   "inv");
        setupSciButton(btnAbs,   "abs");
        setupSciButton(btnFact,  "fact");

        btnPi.setOnClickListener(v -> {
            haptic();
            String result = engine.applyScientific("pi");
            updateResultDisplay(result);
            tvExpression.setText("π =");
        });

        btnEuler.setOnClickListener(v -> {
            haptic();
            String result = engine.applyScientific("euler");
            updateResultDisplay(result);
            tvExpression.setText("e =");
        });

        // ── Memory ──
        btnMS.setOnClickListener(v -> {
            haptic();
            engine.memoryStore();
            tvMemIndicator.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.memory_stored), Toast.LENGTH_SHORT).show();
        });

        btnMR.setOnClickListener(v -> {
            haptic();
            if (engine.hasMemory()) {
                updateResultDisplay(engine.memoryRecall());
            } else {
                Toast.makeText(this, getString(R.string.memory_empty), Toast.LENGTH_SHORT).show();
            }
        });

        // ── Mode toggles ──
        btnModeBasic.setOnClickListener(v -> setMode(false));
        btnModeSci.setOnClickListener(v   -> setMode(true));
        btnModeShift.setOnClickListener(v -> toggleShift());

        // ── Angle mode ──
        tvDeg.setOnClickListener(v -> setAngleMode(CalculatorEngine.AngleMode.DEG));
        tvRad.setOnClickListener(v -> setAngleMode(CalculatorEngine.AngleMode.RAD));

        // ── History icon ──
        tvHistoryIcon.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        // ── Bottom nav ──
        findViewById(R.id.navHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }

    // ══════════════════════════════════════════
    //  SCIENTIFIC BUTTON HELPER
    // ══════════════════════════════════════════
    private void setupSciButton(Button btn, String func) {
        btn.setOnClickListener(v -> {
            haptic();
            String result = engine.applyScientific(func);
            updateResultDisplay(result);
            tvExpression.setText(engine.getExpression());
            updateHistoryDisplay();
            animateResult();
        });
    }

    // ══════════════════════════════════════════
    //  MODE SWITCHING
    // ══════════════════════════════════════════
    private void setMode(boolean sci) {
        isSciMode = sci;
        if (sci) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            sciSection.startAnimation(anim);
            sciSection.setVisibility(View.VISIBLE);
            applyModeStyle(btnModeSci, true);
            applyModeStyle(btnModeBasic, false);
        } else {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            sciSection.startAnimation(anim);
            sciSection.setVisibility(View.GONE);
            applyModeStyle(btnModeBasic, true);
            applyModeStyle(btnModeSci, false);
        }
    }

    private void applyModeStyle(Button btn, boolean active) {
        if (active) {
            btn.setTextColor(getColor(R.color.accent_blue));
        } else {
            btn.setTextColor(getColor(R.color.mode_inactive_text));
        }
    }

    private void toggleShift() {
        isShiftMode = !isShiftMode;
        applyModeStyle(btnModeShift, isShiftMode);
        // Swap sin↔sin⁻¹, cos↔cos⁻¹, tan↔tan⁻¹
        if (isShiftMode) {
            btnSin.setText("sin⁻¹"); btnCos.setText("cos⁻¹"); btnTan.setText("tan⁻¹");
            btnAsin.setText("sin");  btnAcos.setText("cos");  btnAtan.setText("tan");
            btnSin.setOnClickListener(v -> { haptic(); updateResultDisplay(engine.applyScientific("asin")); tvExpression.setText(engine.getExpression()); });
            btnCos.setOnClickListener(v -> { haptic(); updateResultDisplay(engine.applyScientific("acos")); tvExpression.setText(engine.getExpression()); });
            btnTan.setOnClickListener(v -> { haptic(); updateResultDisplay(engine.applyScientific("atan")); tvExpression.setText(engine.getExpression()); });
            btnAsin.setOnClickListener(v -> { haptic(); updateResultDisplay(engine.applyScientific("sin")); tvExpression.setText(engine.getExpression()); });
            btnAcos.setOnClickListener(v -> { haptic(); updateResultDisplay(engine.applyScientific("cos")); tvExpression.setText(engine.getExpression()); });
            btnAtan.setOnClickListener(v -> { haptic(); updateResultDisplay(engine.applyScientific("tan")); tvExpression.setText(engine.getExpression()); });
        } else {
            btnSin.setText("sin");   btnCos.setText("cos");   btnTan.setText("tan");
            btnAsin.setText("sin⁻¹");btnAcos.setText("cos⁻¹");btnAtan.setText("tan⁻¹");
            setupSciButton(btnSin, "sin");  setupSciButton(btnCos, "cos");  setupSciButton(btnTan, "tan");
            setupSciButton(btnAsin,"asin"); setupSciButton(btnAcos,"acos"); setupSciButton(btnAtan,"atan");
        }
    }

    // ══════════════════════════════════════════
    //  ANGLE MODE
    // ══════════════════════════════════════════
    private void setAngleMode(CalculatorEngine.AngleMode mode) {
        engine.setAngleMode(mode);
        boolean isDeg = mode == CalculatorEngine.AngleMode.DEG;
        tvDeg.setBackgroundResource(isDeg ? R.drawable.badge_active_bg : R.drawable.badge_inactive_bg);
        tvDeg.setTextColor(getColor(isDeg ? R.color.accent_blue : R.color.text_dim));
        tvRad.setBackgroundResource(isDeg ? R.drawable.badge_inactive_bg : R.drawable.badge_active_bg);
        tvRad.setTextColor(getColor(isDeg ? R.color.text_dim : R.color.accent_blue));
    }

    // ══════════════════════════════════════════
    //  DISPLAY HELPERS
    // ══════════════════════════════════════════
    private void updateResultDisplay(String text) {
        tvResult.setText(text);
        // Dynamic font size
        if (text.length() > 12) tvResult.setTextSize(28);
        else if (text.length() > 8) tvResult.setTextSize(40);
        else tvResult.setTextSize(56);

        // Error color
        if (text.equals("Error")) {
            tvResult.setTextColor(getColor(R.color.btn_func_text));
        } else {
            tvResult.setTextColor(getColor(R.color.text_main));
        }
    }

    private void updateClearBtn() {
        String cur = engine.getCurrentInput();
        btnClear.setText(cur.equals("0") ? "AC" : "C");
    }

    private void updateHistoryDisplay() {
        var hist = engine.getHistory();
        if (!hist.isEmpty()) {
            CalculatorEngine.HistoryEntry e = hist.get(0);
            tvHistory.setText(e.expression + " = " + e.result);
        }
    }

    private void updateOperatorHighlight() {
        // Reset all operator buttons
        int defaultColor = getColor(R.color.btn_op_text);
        btnAdd.setTextColor(defaultColor);
        btnSubtract.setTextColor(defaultColor);
        btnMultiply.setTextColor(defaultColor);
        btnDivide.setTextColor(defaultColor);

        // Highlight active
        if (!activeOperator.isEmpty()) {
            Button active = null;
            switch (activeOperator) {
                case "+": active = btnAdd; break;
                case "−": active = btnSubtract; break;
                case "×": active = btnMultiply; break;
                case "÷": active = btnDivide; break;
            }
            if (active != null) {
                active.setTextColor(getColor(R.color.accent_blue));
            }
        }
    }

    // ══════════════════════════════════════════
    //  ANIMATIONS
    // ══════════════════════════════════════════
    private void animateResult() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.result_pulse);
        tvResult.startAnimation(anim);
    }

    // ══════════════════════════════════════════
    //  HAPTIC FEEDBACK
    // ══════════════════════════════════════════
    @SuppressWarnings("deprecation")
    private void haptic() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(18);
        }
    }

    @SuppressWarnings("deprecation")
    private void hapticStrong() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(35);
        }
    }
}
