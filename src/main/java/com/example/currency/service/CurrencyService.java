package com.example.currency.service;

import com.example.currency.dto.CoinDeskResponse;
import com.example.currency.dto.CurrencyResponseDTO;
import com.example.currency.entity.CurrencyEntity;
import com.example.currency.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CoinDeskApiClient coinDeskApiClient;
    private final Object currencyLock = new Object(); // 幣別操作的鎖

    // 使用 volatile 確保可見性
    private volatile CoinDeskResponse cachedCoinDeskResponse;

    // 用於同步的對象
    private final Object lock = new Object();

    // 獲取並轉換 CoinDesk API 數據
    public CurrencyResponseDTO getFormattedCoinDeskData() {
        CoinDeskResponse response = cachedCoinDeskResponse;
        if (response == null) {
            // 使用 double-checked locking 模式
            synchronized (lock) {
                response = cachedCoinDeskResponse;
                if (response == null) {
                    response = coinDeskApiClient.getCoinDeskData();
                    cachedCoinDeskResponse = response;
                }
            }
        }
        return convertToResponseDTO(response);
    }

    // 強制更新 CoinDesk 數據
    public CurrencyResponseDTO refreshCoinDeskData() {
        synchronized (lock) {
            CoinDeskResponse response = coinDeskApiClient.getCoinDeskData();
            cachedCoinDeskResponse = response;
            return convertToResponseDTO(response);
        }
    }

    // 轉換數據格式
    private CurrencyResponseDTO convertToResponseDTO(CoinDeskResponse coinDeskResponse) {
        // 轉換時間格式
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(coinDeskResponse.getTime().getUpdatedISO(), inputFormatter);
        String formattedTime = dateTime.format(outputFormatter);

        // 轉換幣別資訊
        List<CurrencyResponseDTO.CurrencyInfo> currencyInfoList = new ArrayList<>();

        coinDeskResponse.getBpi().forEach((key, bpiInfo) -> {
            // 查詢對應的中文名稱
            CurrencyEntity currencyEntity = currencyRepository.findById(key)
                    .orElse(new CurrencyEntity()); // 如果找不到，返回空實體

            CurrencyResponseDTO.CurrencyInfo currencyInfo = CurrencyResponseDTO.CurrencyInfo.builder()
                    .code(bpiInfo.getCode())
                    .chineseName(currencyEntity.getChineseName())
                    .rate(bpiInfo.getRate())
                    .build();

            currencyInfoList.add(currencyInfo);
        });

        return CurrencyResponseDTO.builder()
                .updateTime(formattedTime)
                .currencies(currencyInfoList)
                .build();
    }

    // 查詢所有幣別（按代碼排序）
    public List<CurrencyEntity> getAllCurrencies() {
        return currencyRepository.findAllByOrderByCodeAsc();
    }

    // 新增幣別
    public CurrencyEntity createCurrency(CurrencyEntity currency) {
        synchronized (currencyLock) {
            // 檢查是否已存在
            if (currencyRepository.existsById(currency.getCode())) {
                throw new RuntimeException("Currency already exists: " + currency.getCode());
            }
            return currencyRepository.save(currency);
        }
    }

    // 更新幣別
    public CurrencyEntity updateCurrency(String code, CurrencyEntity currency) {
        synchronized (currencyLock) {
            if (!currencyRepository.existsById(code)) {
                throw new RuntimeException("Currency not found: " + code);
            }
            currency.setCode(code);
            return currencyRepository.save(currency);
        }
    }

    // 刪除幣別
    public void deleteCurrency(String code) {
        synchronized (currencyLock) {
            if (!currencyRepository.existsById(code)) {
                throw new RuntimeException("Currency not found: " + code);
            }
            currencyRepository.deleteById(code);
        }
    }
}