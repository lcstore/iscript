package com.lezo.iscript.match;

import com.lezo.iscript.service.crawler.dto.SimilarJobDto;

public interface SimilarJobHandler {
    void handle(SimilarJobDto jobDto);
}
