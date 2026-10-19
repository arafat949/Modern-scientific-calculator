package com.example.moderncalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.historyRecycler);
        tvEmpty      = findViewById(R.id.tvEmpty);

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Clear button
        findViewById(R.id.btnClearHistory).setOnClickListener(v -> {
            HistoryStore.getInstance().clear();
            updateUI(HistoryStore.getInstance().getHistory());
        });

        List<CalculatorEngine.HistoryEntry> history = HistoryStore.getInstance().getHistory();
        adapter = new HistoryAdapter(history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateUI(history);
    }

    private void updateUI(List<CalculatorEngine.HistoryEntry> hist) {
        if (hist == null || hist.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    // ══ Adapter ══
    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

        private final List<CalculatorEngine.HistoryEntry> items;

        HistoryAdapter(List<CalculatorEngine.HistoryEntry> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CalculatorEngine.HistoryEntry e = items.get(position);
            holder.tvExpr.setText(e.expression);
            holder.tvResult.setText("= " + e.result);
        }

        @Override
        public int getItemCount() { return items == null ? 0 : items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvExpr, tvResult;
            VH(@NonNull View v) {
                super(v);
                tvExpr   = v.findViewById(R.id.tvHistExpr);
                tvResult = v.findViewById(R.id.tvHistResult);
            }
        }
    }
}
