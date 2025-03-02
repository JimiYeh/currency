package com.example.currency.service;

import com.example.currency.dto.CoinDeskResponse;
import com.example.currency.dto.CurrencyResponseDTO;
import com.example.currency.entity.CurrencyEntity;
import com.example.currency.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CurrencyServiceTest {

    @MockBean
    private CoinDeskApiClient coinDeskApiClient;

    @MockBean
    private CurrencyRepository currencyRepository;

    @Autowired
    private CurrencyService currencyService;

    private CoinDeskResponse mockCoinDeskResponse;
    private CurrencyEntity mockCurrencyEntity;

    @BeforeEach
    void setUp() {
        // 準備測試數據
        mockCoinDeskResponse = new CoinDeskResponse();

        // 設置時間
        CoinDeskResponse.Time time = new CoinDeskResponse.Time();
        time.setUpdatedISO("2024-02-25T10:30:00+00:00");
        mockCoinDeskResponse.setTime(time);

        // 設置幣別資訊
        HashMap<String, CoinDeskResponse.BpiInfo> bpi = new HashMap<>();
        CoinDeskResponse.BpiInfo usdInfo = new CoinDeskResponse.BpiInfo();
        usdInfo.setCode("USD");
        usdInfo.setRate("50,000.00");
        bpi.put("USD", usdInfo);
        mockCoinDeskResponse.setBpi(bpi);

        // 準備幣別實體
        mockCurrencyEntity = new CurrencyEntity();
        mockCurrencyEntity.setCode("USD");
        mockCurrencyEntity.setChineseName("美元");
    }

    @Test
    void testGetFormattedCoinDeskData() {
        // 設置 mock 行為
        when(coinDeskApiClient.getCoinDeskData()).thenReturn(mockCoinDeskResponse);
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(mockCurrencyEntity));

        // 執行測試
        CurrencyResponseDTO result = currencyService.getFormattedCoinDeskData();

        // 驗證結果
        assertNotNull(result);
        assertNotNull(result.getUpdateTime());
        assertFalse(result.getCurrencies().isEmpty());
        assertEquals("USD", result.getCurrencies().get(0).getCode());
        assertEquals("美元", result.getCurrencies().get(0).getChineseName());
        assertEquals("50,000.00", result.getCurrencies().get(0).getRate());
    }

    @Test
    void testCreateCurrency() {
        // 準備測試數據
        CurrencyEntity newCurrency = new CurrencyEntity();
        newCurrency.setCode("EUR");
        newCurrency.setChineseName("歐元");

        // 設置 mock 行為
        when(currencyRepository.existsById("EUR")).thenReturn(false);
        when(currencyRepository.save(any(CurrencyEntity.class))).thenReturn(newCurrency);

        // 執行測試
        CurrencyEntity result = currencyService.createCurrency(newCurrency);

        // 驗證結果
        assertNotNull(result);
        assertEquals("EUR", result.getCode());
        assertEquals("歐元", result.getChineseName());
    }

    @Test
    void testCreateCurrency_AlreadyExists() {
        // 準備測試數據
        CurrencyEntity existingCurrency = new CurrencyEntity();
        existingCurrency.setCode("USD");
        existingCurrency.setChineseName("美元");

        // 設置 mock 行為
        when(currencyRepository.existsById("USD")).thenReturn(true);

        // 驗證異常
        assertThrows(RuntimeException.class, () -> {
            currencyService.createCurrency(existingCurrency);
        });
    }

    @Test
    void testGetAllCurrencies() {
        // 準備測試數據
        List<CurrencyEntity> mockCurrencies = Arrays.asList(
                mockCurrencyEntity,
                new CurrencyEntity() {
                    {
                        setCode("EUR");
                        setChineseName("歐元");
                    }
                });

        // 設置 mock 行為
        when(currencyRepository.findAllByOrderByCodeAsc()).thenReturn(mockCurrencies);

        // 執行測試
        List<CurrencyEntity> result = currencyService.getAllCurrencies();

        // 驗證結果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("USD", result.get(0).getCode());
        assertEquals("EUR", result.get(1).getCode());
    }

    @Test
    void testUpdateCurrency() {
        // 準備測試數據
        CurrencyEntity updateCurrency = new CurrencyEntity();
        updateCurrency.setChineseName("美金");

        // 設置 mock 行為
        when(currencyRepository.existsById("USD")).thenReturn(true);
        when(currencyRepository.save(any(CurrencyEntity.class))).thenAnswer(i -> i.getArgument(0));

        // 執行測試
        CurrencyEntity result = currencyService.updateCurrency("USD", updateCurrency);

        // 驗證結果
        assertNotNull(result);
        assertEquals("USD", result.getCode());
        assertEquals("美金", result.getChineseName());
    }

    @Test
    void testDeleteCurrency() {
        // 設置 mock 行為
        when(currencyRepository.existsById("USD")).thenReturn(true);
        doNothing().when(currencyRepository).deleteById("USD");

        // 執行測試
        assertDoesNotThrow(() -> currencyService.deleteCurrency("USD"));

        // 驗證是否調用了刪除方法
        verify(currencyRepository, times(1)).deleteById("USD");
    }
}