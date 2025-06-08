package com.example.eventure.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventure.R;
import com.example.eventure.adapters.ImageCarouselAdapter;
import com.example.eventure.model.Provider;

import java.util.Arrays;

public class CompanyDetailsDialog extends DialogFragment {

    private static final String ARG_PROVIDER = "provider";
    private Provider provider;

    public static CompanyDetailsDialog newInstance(Provider provider) {
        CompanyDetailsDialog fragment = new CompanyDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROVIDER, provider);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.provider_company_view, container, false);

        if (getArguments() != null) {
            provider = (Provider) getArguments().getSerializable(ARG_PROVIDER);
        }

        if (provider == null) {
            dismiss();
            return view;
        }

        setupUI(view);

        Button btnReport = view.findViewById(R.id.btn_report);
        btnReport.setOnClickListener(v -> {
            ReportDialog reportDialog = ReportDialog.newInstance(provider.getId());
            reportDialog.show(getParentFragmentManager(), "report_dialog");
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

    private void setupUI(View view) {
        ImageButton btnExit = view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(v -> dismiss());

        // Set up photo carousel
        ViewPager2 photoCarousel = view.findViewById(R.id.photo_carousel);
        if (provider.getCompanyPhotos() != null && provider.getCompanyPhotos().length > 0) {
            ImageCarouselAdapter adapter = new ImageCarouselAdapter(getContext(), Arrays.asList(provider.getCompanyPhotos()));
            photoCarousel.setAdapter(adapter);
        } else {
            photoCarousel.setVisibility(View.GONE);
        }

        TextView tvCompanyName = view.findViewById(R.id.tv_company_name);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvEmail = view.findViewById(R.id.tv_company_email);
        TextView tvAddress = view.findViewById(R.id.tv_company_address);
        TextView workingHours = view.findViewById(R.id.tv_working_hours);

        tvCompanyName.setText(provider.getCompanyName());
        tvDescription.setText(provider.getDescription());
        tvEmail.setText("Email: "+provider.getCompanyEmail());
        tvAddress.setText("Address: "+provider.getCompanyAddress());
        workingHours.setText(provider.getOpeningTime()+" - "+provider.getClosingTime()+"h");
    }
}
