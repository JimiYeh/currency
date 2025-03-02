# 幣別轉換系統

這是一個使用 Spring Boot 開發的 demo，提供幣別中文名稱管理和匯率查詢功能。

## 功能特點

- 幣別管理（CRUD 操作）
- 整合 CoinDesk API
- 自動更新匯率資料（每 30 秒）
- RESTful API 設計
- Swagger UI 文檔
- H2 資料庫支持

## 加分項目
1. AOP 應用
   - 使用 AOP 記錄 API 調用日誌
   - 記錄外部 API 調用的請求和響應
2. 設計模式應用
   - Factory Pattern
     - Spring 的 BeanFactory
     - 管理和創建各種 Bean
   
   - Dependency Injection
     - Constructor Injection
   
   - Repository Pattern
     - CurrencyRepository
     - 數據訪問層的抽象


## 線上展示

- [線上展示網址](https://currency-production-d23d.up.railway.app/)
- [Swagger UI 文檔](https://currency-production-d23d.up.railway.app/swagger-ui/index.html)
- [操作展示影片](https://youtu.be/p00yyi9IJw0)

## 技術棧

- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database
- Swagger UI (SpringDoc OpenAPI)
- Lombok
- JUnit 5

## API 功能

1. 幣別管理
   - 新增幣別
   - 查詢幣別
   - 更新幣別
   - 刪除幣別

2. 匯率功能
   - 獲取即時匯率
   - 轉換匯率格式


## 部署

使用 Railway 進行部署