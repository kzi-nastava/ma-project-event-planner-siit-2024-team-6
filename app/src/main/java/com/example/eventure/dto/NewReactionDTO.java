package com.example.eventure.dto;

public class NewReactionDTO {
    private String text;
    private Integer rating;
    private Integer eventId;  // optional
    private Integer offerId;  // optional

    public NewReactionDTO(){}
    public NewReactionDTO(String text, Integer r, Integer e, Integer o){
        this.eventId = e;
        this.offerId = o;
        this.rating = r;
        this.text = text;
    }
}
