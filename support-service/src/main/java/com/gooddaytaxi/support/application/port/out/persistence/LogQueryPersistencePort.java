package com.gooddaytaxi.support.application.port.out.persistence;

import com.gooddaytaxi.support.application.query.filter.AdminLogFilter;
import com.gooddaytaxi.support.domain.log.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogQueryPersistencePort {
    Page<Log> search(AdminLogFilter filter, Pageable pageable);
}
