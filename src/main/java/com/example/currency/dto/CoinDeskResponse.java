package com.example.currency.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@Data
public class CoinDeskResponse {
    private Time time;
    private String disclaimer;
    private String chartName;
    private Map<String, BpiInfo> bpi;

    @Data
    public static class Time {
        private String updated;
        private String updatedISO;

        @JsonProperty("updateduk")
        private String updatedUK;
    }

    @Data
    public static class BpiInfo {
        private String code;
        private String symbol;
        private String rate;
        private String description;

        @JsonProperty("rate_float")
        private Double rateFloat;
    }
}