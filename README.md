### Coinbase API 專案

這是一個Spring Boot 的專案，用於查詢和轉換 Coindesk 的比特幣價格資訊，並提供幣別管理功能。

## 相關技術

- Java 8
- Spring Boot 2.3.12.RELEASE
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5
- Mockito

## 功能特點

1. **幣別管理（CRUD 操作）**
   - 新增幣別
   - 查詢幣別
   - 更新幣別
   - 刪除幣別

2. **Coindesk API 整合**
   - 查詢原始 Coindesk 數據
   - 提供轉換後的自定義格式數據

## API 端

### 幣別管理

- **GET** `/api/currencies` - 查詢所有幣別
- **GET** `/api/currencies/{code}` - 查詢指定幣別
- **POST** `/api/currencies` - 新增新幣別
- **PUT** `/api/currencies/{code}` - 更新幣別
- **DELETE** `/api/currencies/{code}` - 刪除幣別

### Coindesk API

- **GET** `/api/coin/original` - 原始 Coindesk 數據
- **GET** `/api/coin/custom` - 查詢Coindesk原始資料轉換後的數據

## Units Test

專案包含單元測試與整合測試：

- `CoinServiceTest.java` - 服務層單元測試
- `CoinControllerIntegrationTest.java` - 控制器層整合測試

**運行測試**：

mvn test
