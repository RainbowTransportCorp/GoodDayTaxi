package com.gooddaytaxi.dispatch.domain.exception;

import com.gooddaytaxi.dispatch.domain.model.enums.AssignmentStatus;

public class InvalidAssignmentStatusException extends RuntimeException {

    private static final String DEFAULT_MESSAGE =
            "assignmentLog 상태 전이가 허용되지 않습니다. 현재 상태 : ";

    public InvalidAssignmentStatusException(AssignmentStatus status) {
        super(DEFAULT_MESSAGE + status);
    }

    public InvalidAssignmentStatusException(String message) {
        super(message);
    }
}
