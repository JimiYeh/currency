package com.example.currency.service;

import com.example.currency.dto.CoinDeskResponse;

public interface CoinDeskApiClient {
    CoinDeskResponse getCoinDeskData();
}