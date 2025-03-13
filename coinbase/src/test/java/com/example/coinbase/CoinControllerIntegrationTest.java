package com.example.coinbase;

import com.example.coinbase.entity.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CoinControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCurrencyCRUDOperations() throws Exception {
        // 測試新增幣別
        Currency currency = new Currency();
        currency.setCode("JPY");
        currency.setChineseName("日圓");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currency)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("JPY"))
                .andExpect(jsonPath("$.chineseName").value("日圓"));

        // 測試查詢幣別
        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies/JPY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chineseName").value("日圓"));

        // 測試更新幣別
        currency.setChineseName("日元");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/currencies/JPY")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currency)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chineseName").value("日元"));

        // 測試刪除幣別
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/currencies/JPY"))
                .andExpect(status().isOk());
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