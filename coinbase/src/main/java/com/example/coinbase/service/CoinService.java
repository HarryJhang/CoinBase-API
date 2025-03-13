package com.example.coinbase.service;

import com.example.coinbase.dto.CoinDeskResponse;
import com.example.coinbase.dto.CustomCoinResponse;
import com.example.coinbase.entity.Currency;
import com.example.coinbase.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class CoinService {
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String COINDESK_API_URL = "https://kengp3.github.io/blog/coindesk.json";
    
    public Currency saveCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }
    
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
    
    public Optional<Currency> getCurrencyByCode(String code) {
        return currencyRepository.findByCode(code);
    }
    
    public void deleteCurrency(Long id) {
        currencyRepository.deleteById(id);
    }
    
    public CoinDeskResponse getOriginalCoinDeskData() {
        return restTemplate.getForObject(COINDESK_API_URL, CoinDeskResponse.class);
    }
    
    public CustomCoinResponse getCustomCoinData() {
        CoinDeskResponse coinDeskResponse = getOriginalCoinDeskData();
        CustomCoinResponse customResponse = new CustomCoinResponse();
        
        // 轉換時間格式
        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z", java.util.Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date date = inputFormat.parse(coinDeskResponse.getTime().getUpdated());
            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            customResponse.setUpdateTime(outputFormat.format(date));
        } catch (Exception e) {
            customResponse.setUpdateTime(coinDeskResponse.getTime().getUpdated());
        }
        
        // 轉換幣別訊息
        List<CustomCoinResponse.CoinInfo> coinInfoList = new ArrayList<>();
        coinDeskResponse.getBpi().forEach((code, bpiInfo) -> {
            CustomCoinResponse.CoinInfo coinInfo = new CustomCoinResponse.CoinInfo();
            coinInfo.setCode(code);
            coinInfo.setRate(bpiInfo.getRate_float());
            
            // 查詢幣別中文名稱
            Optional<Currency> currency = getCurrencyByCode(code);
            currency.ifPresent(c -> coinInfo.setChineseName(c.getChineseName()));
            
            coinInfoList.add(coinInfo);
        });
        
        customResponse.setCoins(coinInfoList);
        return customResponse;
    }
} 