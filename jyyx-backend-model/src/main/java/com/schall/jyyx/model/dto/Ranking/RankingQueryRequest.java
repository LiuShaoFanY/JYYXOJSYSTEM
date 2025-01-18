package com.schall.jyyx.model.dto.Ranking;

import com.schall.jyyxblackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RankingQueryRequest extends PageRequest {
    private String sortField = "acceptedNum"; // 默认按通过次数排序
    private String sortOrder = "desc";        // 默认降序排列
}