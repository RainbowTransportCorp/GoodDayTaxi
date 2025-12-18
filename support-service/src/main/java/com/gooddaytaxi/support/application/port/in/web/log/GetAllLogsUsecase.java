package com.gooddaytaxi.support.application.port.in.web.log;

import com.gooddaytaxi.support.application.dto.output.log.LogResponse;
import com.gooddaytaxi.support.application.query.filter.AdminLogFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetAllLogsUsecase {
    Page<LogResponse> execute(UUID userId, String userRole,
                              AdminLogFilter filter,        // 필터링
                              Pageable pageable             // 페이지네이션
    );
}
