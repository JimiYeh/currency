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

    // 保存最近一次的 API 響應數據
    private CoinDeskResponse cachedCoinDeskResponse;

    // 獲取並轉換 CoinDesk API 數據
    public CurrencyResponseDTO getFormattedCoinDeskData() {
        // 如果沒有緩存數據，才調用 API
        if (cachedCoinDeskResponse == null) {
            cachedCoinDeskResponse = coinDeskApiClient.getCoinDeskData();
        }

        // 使用緩存的數據進行轉換
        return convertToResponseDTO(cachedCoinDeskResponse);
    }

    // 強制更新 CoinDesk 數據
    public CurrencyResponseDTO refreshCoinDeskData() {
        cachedCoinDeskResponse = coinDeskApiClient.getCoinDeskData();
        return convertToResponseDTO(cachedCoinDeskResponse);
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
        return currencyRepository.save(currency);
    }

    // 更新幣別
    public CurrencyEntity updateCurrency(String code, CurrencyEntity currency) {
        if (!currencyRepository.existsById(code)) {
            throw new RuntimeException("Currency not found: " + code);
        }
        currency.setCode(code);
        return currencyRepository.save(currency);
    }

    // 刪除幣別
    public void deleteCurrency(String code) {
        currencyRepository.deleteById(code);
    }
}