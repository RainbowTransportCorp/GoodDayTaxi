package com.gooddaytaxi.dispatch.application.commend;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DispatchCancelCommand {
    private final UUID dispatchId;
}
