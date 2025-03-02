package com.example.currency.controller;

import com.example.currency.dto.CurrencyResponseDTO;
import com.example.currency.entity.CurrencyEntity;
import com.example.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "幣別管理", description = "幣別管理相關 API")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(summary = "獲取 CoinDesk 資料", description = "獲取並轉換 CoinDesk 的 Bitcoin 匯率資料")
    @GetMapping("/coindesk")
    public ResponseEntity<CurrencyResponseDTO> getCoinDeskData() {
        return ResponseEntity.ok(currencyService.getFormattedCoinDeskData());
    }

    @Operation(summary = "刷新 CoinDesk 資料", description = "強制更新 CoinDesk 的 Bitcoin 匯率資料")
    @GetMapping("/coindesk/refresh")
    public ResponseEntity<CurrencyResponseDTO> refreshCoinDeskData() {
        return ResponseEntity.ok(currencyService.refreshCoinDeskData());
    }

    @Operation(summary = "獲取所有幣別", description = "獲取所有已設定的幣別資料")
    @GetMapping
    public ResponseEntity<List<CurrencyEntity>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @Operation(summary = "新增幣別", description = "新增一個幣別設定")
    @PostMapping
    public ResponseEntity<CurrencyEntity> createCurrency(@RequestBody CurrencyEntity currency) {
        return ResponseEntity.ok(currencyService.createCurrency(currency));
    }

    @Operation(summary = "更新幣別", description = "更新指定幣別的設定")
    @PutMapping("/{code}")
    public ResponseEntity<CurrencyEntity> updateCurrency(
            @PathVariable String code,
            @RequestBody CurrencyEntity currency) {
        return ResponseEntity.ok(currencyService.updateCurrency(code, currency));
    }

    @Operation(summary = "刪除幣別", description = "刪除指定的幣別設定")
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String code) {
        currencyService.deleteCurrency(code);
        return ResponseEntity.ok().build();
    }
}