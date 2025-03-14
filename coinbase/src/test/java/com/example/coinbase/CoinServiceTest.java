package com.example.coinbase;

import com.example.coinbase.dto.CoinDeskResponse;
import com.example.coinbase.dto.CustomCoinResponse;
import com.example.coinbase.entity.Currency;
import com.example.coinbase.repository.CurrencyRepository;
import com.example.coinbase.service.CoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CoinServiceTest {

    @InjectMocks
    private CoinService coinService;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOriginalCoinDeskData() {
        // 測試案例
        CoinDeskResponse mockResponse = new CoinDeskResponse();
        CoinDeskResponse.Time time = new CoinDeskResponse.Time();
        time.setUpdated("Mar 11, 2024 00:00:00 UTC");
        mockResponse.setTime(time);

        when(restTemplate.getForObject(any(String.class), eq(CoinDeskResponse.class)))
                .thenReturn(mockResponse);

        // 測試
        CoinDeskResponse result = coinService.getOriginalCoinDeskData();

        // 驗證結果
        assertNotNull(result);
        assertEquals("Mar 11, 2024 00:00:00 UTC", result.getTime().getUpdated());
    }

    @Test
    void testGetCustomCoinData() {
        // 測試案例
        CoinDeskResponse mockResponse = new CoinDeskResponse();
        CoinDeskResponse.Time time = new CoinDeskResponse.Time();
        time.setUpdated("Mar 11, 2024 00:00:00 UTC");
        mockResponse.setTime(time);

        HashMap<String, CoinDeskResponse.BpiInfo> bpi = new HashMap<>();
        CoinDeskResponse.BpiInfo usdInfo = new CoinDeskResponse.BpiInfo();
        usdInfo.setCode("USD");
        usdInfo.setRate_float(50000.0);
        bpi.put("USD", usdInfo);
        mockResponse.setBpi(bpi);

        Currency usdCurrency = new Currency();
        usdCurrency.setCode("USD");
        usdCurrency.setChineseName("美元");

        when(restTemplate.getForObject(any(String.class), eq(CoinDeskResponse.class)))
                .thenReturn(mockResponse);
        when(currencyRepository.findByCode("USD"))
                .thenReturn(Optional.of(usdCurrency));

        // 測試
        CustomCoinResponse result = coinService.getCustomCoinData();

        //驗證結果
        assertNotNull(result);
        assertEquals("2024/03/11 00:00:00", result.getUpdateTime());
        assertEquals(1, result.getCoins().size());
        assertEquals("USD", result.getCoins().get(0).getCode());
        assertEquals("美元", result.getCoins().get(0).getChineseName());
        assertEquals(50000.0, result.getCoins().get(0).getRate());
    }

    @Test
    void testCurrencyCRUD() {
        // 測試案例
        Currency currency = new Currency();
        currency.setCode("USD");
        currency.setChineseName("美元");

        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(currency));
        when(currencyRepository.findAll()).thenReturn(Arrays.asList(currency));

        // 測試新增
        Currency savedCurrency = coinService.saveCurrency(currency);
        assertNotNull(savedCurrency);
        assertEquals("USD", savedCurrency.getCode());
        assertEquals("美元", savedCurrency.getChineseName());

        // 測試查詢
        Optional<Currency> foundCurrency = coinService.getCurrencyByCode("USD");
        assertTrue(foundCurrency.isPresent());
        assertEquals("美元", foundCurrency.get().getChineseName());

        // 測試查詢全部
        assertEquals(1, coinService.getAllCurrencies().size());
    }
} 