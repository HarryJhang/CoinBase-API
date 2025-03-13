package com.example.coinbase.controller;

import com.example.coinbase.dto.CoinDeskResponse;
import com.example.coinbase.dto.CustomCoinResponse;
import com.example.coinbase.entity.Currency;
import com.example.coinbase.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CoinController {

    @Autowired
    private CoinService coinService;

    // 幣別CRUD API
    @GetMapping("/currencies")
    public List<Currency> getAllCurrencies() {
        return coinService.getAllCurrencies();
    }

    @GetMapping("/currencies/{code}")
    public ResponseEntity<Currency> getCurrencyByCode(@PathVariable String code) {
        return coinService.getCurrencyByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/currencies")
    public Currency createCurrency(@RequestBody Currency currency) {
        return coinService.saveCurrency(currency);
    }

    @PutMapping("/currencies/{code}")
    public ResponseEntity<Currency> updateCurrency(@PathVariable String code, @RequestBody Currency currency) {
        return coinService.getCurrencyByCode(code)
                .map(existingCurrency -> {
                    currency.setId(existingCurrency.getId());
                    currency.setCode(code);
                    currency.setCreatedDate(existingCurrency.getCreatedDate());
                    return ResponseEntity.ok(coinService.saveCurrency(currency));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/currencies/{code}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String code) {
        return coinService.getCurrencyByCode(code)
                .map(currency -> {
                    coinService.deleteCurrency(currency.getId());
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // CoinDesk API
    @GetMapping("/coin/original")
    public CoinDeskResponse getOriginalCoinDeskData() {
        return coinService.getOriginalCoinDeskData();
    }

    @GetMapping("/coin/custom")
    public CustomCoinResponse getCustomCoinData() {
        return coinService.getCustomCoinData();
    }
} 