# myStockData

## Build & Run

### 1. Build all services (requires gradle)
- GitBash: `cd financialreportservice && ./gradlew build && cd ../ && cd schedulingservice && ./gradlew build && cd ../ && cd stockdataservice && ./gradlew build && cd ../`
- PowerShell: `cd financialreportservice; ./gradlew build; cd ../; cd schedulingservice; ./gradlew build; cd ../; cd stockdataservice; ./gradlew build; cd ../`

### 2. Start all docker built container in docker-compose
`docker-compose up`

## Test
1. Add something to your watchlist:
```
PUT http://localhost:8080/v1/watchlist?lei=SAP.DE
Content-Type: application/json`
```
2. Retrieve financial reports for companies in your watchlist: # TODO --> Composer
```
GET http://localhost:8080/v1/financialreports/loadReports?watchlist=true
Accept: application/stream+json`
```
3. Retrieve aggregated price information for the past year:
```
GET http://localhost:8080/v1/aggregatedPriceInformation/retrieve?months=1
Accept: application/json
```
4. Start retrieving precise price information:
```
GET http://localhost:8084/v1/precisePriceInformation/start
Accept: application/stream+json
```

## Endpoint description
#### Watchlist
Get symbols of all tracked stocks.
```
GET http://localhost:8084/v1/watchlist\
Content-Type: application/json
```

Add a symbol to the watchlist.
```
PUT http://localhost:8084/v1/watchlist?lei=test
Content-Type: application/json
```

Remove a symbol from the watchlist.
```
DELETE http://localhost:8084/v1/watchlist?lei=test
Content-Type: application/json
```

#### Aggregated Price Information
Retrieve aggregated price information from the remote data source for the provided symbols, start and end instants.
```
GET http://localhost:8084/v1/aggregatedPriceInformation/retrieve?symbols=SAP.DE&symbols=TSLA&symbols=AMC&start=2021-08-01T09:15:29.442856700Z&end=2022-08-01T09:15:29.442856700Z
Accept: application/json
```

Retrieve aggregated price information from the database for the provided symbols, start and end instants.
```
GET http://localhost:8084/v1/aggregatedPriceInformation?symbols=SAP.DE&symbols=TSLA&symbols=AMC
Accept: application/json
```

#### Precise Price Information
Start retrieving precise price information from the remote data source.
```
GET http://localhost:8084/v1/precisePriceInformation/start?symbols=SAP.DE&symbols=TSLA&symbols=AMC&symbols=GE&symbols=ADS.DE&symbols=ALV.DE&symbols=BMW.DE&symbols=PAH3.DE
Accept: application/stream+json
```

Stop retrieving precise price information from the remote data source.
```
GET http://localhost:8084/v1/precisePriceInformation/stop
Accept: application/stream+json
```

Read precise price information from the database.
```
GET http://localhost:8084/v1/aggregatedPriceInformation?symbols=SAP.DE&symbols=TSLA&symbols=AMC
Accept: application/json
```

## Financial Report Service
Retrieve reports with the specified lei.
```
GET http://localhost:8080/v1/financialreports/loadReports?lei=549300CSLHPO6Y1AZN37&lei=529900D6BF99LW9R2E68
Accept: application/stream+json
```

Retrieve all available reports.
```
GET http://localhost:8083/v1/financialreports/loadReports
Accept: application/stream+json
```

Read reports with the specified lei from the database.
```
GET http://localhost:8083/v1/financialreports?lei=549300CSLHPO6Y1AZN37&start=2019-12-29&end=2022-01-01
Accept: application/stream+json
```

## Know issues
- Partially retrieved reports are not extended.