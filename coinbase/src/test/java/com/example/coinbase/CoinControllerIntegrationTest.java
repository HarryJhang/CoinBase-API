package com.example.coinbase;

import com.example.coinbase.entity.Currency;
import com.example.coinbase.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CoinControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CurrencyRepository currencyRepository;

    @BeforeEach
    void setUp() {
        currencyRepository.deleteAll();
    }

    @Test
    public void testCurrencyCRUDOperations() throws Exception {
        Currency currency = new Currency();
        currency.setCode("JPY");
        currency.setChineseName("日圓");

        // 創建貨幣
        String createResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currency)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Currency createdCurrency = objectMapper.readValue(createResult, Currency.class);

        // 讀取貨幣
        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies/" + currency.getCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(currency.getCode()))
                .andExpect(jsonPath("$.chineseName").value(currency.getChineseName()));

        // 更新貨幣
        currency.setChineseName("日元");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/currencies/" + currency.getCode())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currency)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(currency.getCode()))
                .andExpect(jsonPath("$.chineseName").value("日元"));

        // 刪除貨幣
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/currencies/" + currency.getCode()))
                .andExpect(status().isOk());

        // 確認刪除成功
        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies/" + currency.getCode()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOriginalCoinDeskData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/coin/original"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.bpi").exists());
    }

    @Test
    void testGetCustomCoinData() throws Exception {
        // 新增資料庫測試幣別
        Currency currency = new Currency();
        currency.setCode("JPY");
        currency.setChineseName("日圓");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currency)))
                .andExpect(status().isOk());

        // 測試CoinBase客製API
        mockMvc.perform(MockMvcRequestBuilders.get("/api/coin/custom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updateTime").exists())
                .andExpect(jsonPath("$.coins").exists());
    }
} 