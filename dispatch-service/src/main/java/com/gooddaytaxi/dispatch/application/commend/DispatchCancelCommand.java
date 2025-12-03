package com.gooddaytaxi.dispatch.application.commend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchCancelCommand {
    private UUID dispatchId;
}
