package com.visitly.assignment.dto.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private boolean error;
    private String message;

    public ErrorResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
    }
}
