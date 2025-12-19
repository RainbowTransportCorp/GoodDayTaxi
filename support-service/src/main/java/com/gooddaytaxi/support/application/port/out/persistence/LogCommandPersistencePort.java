package com.gooddaytaxi.support.application.port.out.persistence;

import com.gooddaytaxi.support.domain.log.model.Log;

public interface LogCommandPersistencePort {
    Log save(Log log);
}