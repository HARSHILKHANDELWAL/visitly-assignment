package com.visitly.assignment.dto.response;

import lombok.Data;

@Data
public class SuccessResponse {
    private boolean success;
    private Object message;

    public SuccessResponse(boolean success, Object register) {
        this.success = success;
        this.message = register;
    }
}
