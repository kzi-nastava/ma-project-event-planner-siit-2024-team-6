package com.example.eventure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.dto.ReportDTO;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportDTO> reports = new ArrayList<>();
    private OnItemActionListener actionListener;

    public interface OnItemActionListener {
        void onSuspend(ReportDTO report);
        void onReject(ReportDTO report);
    }

    public ReportAdapter(OnItemActionListener listener) {
        this.actionListener = listener;
    }

    public void setReports(List<ReportDTO> newReports) {
        reports = newReports;
        notifyDataSetChanged();
    }

    public void addReports(List<ReportDTO> newReports) {
        int startPos = reports.size();
        reports.addAll(newReports);
        notifyItemRangeInserted(startPos, newReports.size());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_card, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        holder.bind(reports.get(position));
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvReporter, tvReportedUser, tvReason;
        Button btnSuspend, btnReject;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReporter = itemView.findViewById(R.id.tvReporter);
            tvReportedUser = itemView.findViewById(R.id.tvReported);
            tvReason = itemView.findViewById(R.id.tvReportReason);
            btnSuspend = itemView.findViewById(R.id.btnSuspend);
            btnReject = itemView.findViewById(R.id.btnReject);
        }

        void bind(ReportDTO report) {
            tvReporter.setText("Reported by: " + report.getReporterUsername());
            tvReportedUser.setText("Reported user: " + report.getReportedUsername());
            tvReason.setText("Reason: " + report.getReason());

            btnSuspend.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onSuspend(report);
            });

            btnReject.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onReject(report);
            });
        }
    }
}
