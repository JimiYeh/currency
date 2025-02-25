package com.example.currency.controller;

import com.example.currency.dto.CurrencyResponseDTO;
import com.example.currency.entity.CurrencyEntity;
import com.example.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
@CrossOrigin
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/coindesk")
    public ResponseEntity<CurrencyResponseDTO> getCoinDeskData() {
        return ResponseEntity.ok(currencyService.getFormattedCoinDeskData());
    }

    @GetMapping("/coindesk/refresh")
    public ResponseEntity<CurrencyResponseDTO> refreshCoinDeskData() {
        return ResponseEntity.ok(currencyService.refreshCoinDeskData());
    }

    @GetMapping
    public ResponseEntity<List<CurrencyEntity>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @PostMapping
    public ResponseEntity<CurrencyEntity> createCurrency(@RequestBody CurrencyEntity currency) {
        return ResponseEntity.ok(currencyService.createCurrency(currency));
    }

    @PutMapping("/{code}")
    public ResponseEntity<CurrencyEntity> updateCurrency(
            @PathVariable String code,
            @RequestBody CurrencyEntity currency) {
        return ResponseEntity.ok(currencyService.updateCurrency(code, currency));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String code) {
        currencyService.deleteCurrency(code);
        return ResponseEntity.ok().build();
    }
}