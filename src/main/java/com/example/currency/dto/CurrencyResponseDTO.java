package com.example.currency.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class CurrencyResponseDTO {
    private String updateTime; // 更新時間 (1990/01/01 00:00:00 格式)
    private List<CurrencyInfo> currencies; // 幣別相關資訊列表

    @Data
    @Builder
    public static class CurrencyInfo {
        private String code; // 幣別代碼
        private String chineseName; // 幣別中文名稱
        private String rate; // 匯率
    }
}