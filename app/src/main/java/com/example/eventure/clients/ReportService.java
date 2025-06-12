package com.example.eventure.clients;

import com.example.eventure.dto.ReportDTO;
import com.example.eventure.dto.NewReportDTO;
import com.example.eventure.model.PagedResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportService {

    @POST(ClientUtils.REPORTS)
    Call<ReportDTO> reportUser(@Body NewReportDTO newReport);

    @GET(ClientUtils.REPORTS)
    Call<PagedResponse<ReportDTO>> getReports(
            @Query("page") int page,
            @Query("size") int size
    );

    @POST(ClientUtils.APPROVE_REPORT)
    Call<Map<String, String>> approveReport(@Path("id") int reportId);

    @DELETE(ClientUtils.REJECT_REPORT)
    Call<Map<String, String>> rejectReport(@Path("id") int reportId);
}
