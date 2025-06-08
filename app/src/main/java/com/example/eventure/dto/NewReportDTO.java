package com.example.eventure.dto;

public class NewReportDTO {
    private String reason;
    private Integer reporterId;
    private Integer reportedId;

    public NewReportDTO(String reason, Integer reporterId, Integer reportedId) {
        this.reason = reason;
        this.reporterId = reporterId;
        this.reportedId = reportedId;
    }
}
