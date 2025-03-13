package com.example.coinbase.dto;

import lombok.Data;
import java.util.List;

@Data
public class CustomCoinResponse {
    private String updateTime;
    private List<CoinInfo> coins;

    @Data
    public static class CoinInfo {
        private String code;
        private String chineseName;
        private Double rate;
    }
} 