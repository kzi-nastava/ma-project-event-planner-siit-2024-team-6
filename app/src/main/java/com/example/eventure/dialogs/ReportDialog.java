package com.example.eventure.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.NewReportDTO;
import com.example.eventure.dto.ReportDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportDialog extends DialogFragment {
    private static final String ARG_REPORTED_ID = "reported_id";

    private Integer reportedId;

    public static ReportDialog newInstance(Integer reportedId) {
        ReportDialog dialog = new ReportDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_REPORTED_ID, reportedId);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_report, container, false);

        if (getArguments() != null) {
            reportedId = getArguments().getInt(ARG_REPORTED_ID);
        }

        EditText editReason = view.findViewById(R.id.edit_reason);
        ImageButton btnExit = view.findViewById(R.id.btn_exit);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        btnExit.setOnClickListener(v -> dismiss());
        btnCancel.setOnClickListener(v -> dismiss());

        btnSubmit.setOnClickListener(v -> {
            String reason = editReason.getText().toString().trim();
            if (reason.isEmpty()) {
                editReason.setError("Please enter a reason");
                return;
            }

            Integer reporterId = ClientUtils.getAuthService().getUserId();
            if (reporterId == null) {
                Toast.makeText(getContext(), "You must be logged in to report.", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("ReportTag",String.valueOf(reporterId));
            NewReportDTO newReport = new NewReportDTO(reason, reporterId, reportedId);

            ClientUtils.reportService.reportUser(newReport).enqueue(new Callback<ReportDTO>() {
                @Override
                public void onResponse(Call<ReportDTO> call, Response<ReportDTO> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Report submitted successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to submit report.", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }

                @Override
                public void onFailure(Call<ReportDTO> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });

        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
