package com.gooddaytaxi.dispatch.domain.service;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DispatchDomainService {

    /**
     * Accept 처리 (Dispatch + AssignmentLog 동시 처리)
     */
    public void processAccept(Dispatch dispatch,
                              DispatchAssignmentLog assignmentLog, UUID driverId) {
        assignmentLog.accept();
        dispatch.assignedTo(driverId);
        dispatch.accept();
    }

    /**
     * Reject 처리 (Dispatch + AssignmentLog 동시 처리)
     */
    public void processReject(Dispatch dispatch,
                              DispatchAssignmentLog assignmentLog, UUID driverId) {
        assignmentLog.reject();
        dispatch.rejectedByDriver(driverId);
    }
}