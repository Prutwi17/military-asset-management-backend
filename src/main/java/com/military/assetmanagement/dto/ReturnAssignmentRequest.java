package com.military.assetmanagement.dto;

import java.time.LocalDate;

public class ReturnAssignmentRequest {

    private LocalDate returnDate;
    private String returnCondition; // GOOD, DAMAGED, NORMAL_WEAR
    private String notes;

    public ReturnAssignmentRequest() {
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnCondition() {
        return returnCondition;
    }

    public void setReturnCondition(String returnCondition) {
        this.returnCondition = returnCondition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
