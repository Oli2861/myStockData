# myStockData

myStockData is a prototypical implementation of an architecture for retrieving and managing stock price relevant data. Since it is a prototype rather than a production-ready software to manage stock data, the data retrieved, stored and managed by myStockData is to be treated with caution and is not suitable to base investment decisions on. Since the primary focus of this project is to retrieve and store data, the generated csv files might contain unexpected results.

## Build & Run

### 1. Build all services (requires gradle)
- GitBash: `cd financialreportservice && ./gradlew build && cd ../ && cd schedulingservice && ./gradlew build && cd ../ && cd stockdataservice && ./gradlew build && cd ../`
- PowerShell: `cd financialreportservice; ./gradlew build; cd ../; cd schedulingservice; ./gradlew build; cd ../; cd stockdataservice; ./gradlew build; cd ../`

### 2. Start all docker built container in docker-compose
`docker-compose up`

## Guide
#####1. Create companies and add them to your watchlist while doing so:
```
PUT http://localhost:8085/v1/company?addToWatchList=true
Content-Type: application/json

[
  {
    "lei": "529900NNUPAGGOMPXZ31",
    "securities": [
      {
        "isin": "DE0007664039",
        "symbols": [
          {
            "symbol": "VOW3.DE",
            "exchange": "XETRA"
          }
        ]
      }
    ]
  },
  {
    "lei": "529900D6BF99LW9R2E68",
    "securities": [
      {
        "isin": "DE0007164600",
        "symbols": [
          {
            "symbol": "SAP.DE",
            "exchange": "XETRA"
          }
        ]
      }
    ]
  },
  {
    "lei": "549300JSX0Z4CW0V5023",
    "securities": [
      {
        "isin": "DE000A1EWWW0",
        "symbols": [
          {
            "symbol": "ADS.DE",
            "exchange": "XETRA"
          }
        ]
      }
    ]
  }
]
```
##### 2. Retrieve financial reports and aggregated price information for the companies:
```
GET http://localhost:8080/v1/company/retrieveDataForCompanies?aggregatedPriceInfoStart=2012-08-01T09:15:29.442856700Z&aggregatedPriceInfoEnd=2022-08-05T09:15:29.442856700Z&lei=529900NNUPAGGOMPXZ31&lei=529900D6BF99LW9R2E68&lei=549300JSX0Z4CW0V5023
Content-Type: application/json
```

The composer will use the following endpoints of the financialreportservice and the stockdataservice in order to retrieve the desired data:
- financialreportservice: 
  ```
  GET http://localhost:8083/v1/financialreports/retrieveReports?lei=529900NNUPAGGOMPXZ31&lei=529900D6BF99LW9R2E68&lei=549300JSX0Z4CW0V5023
  ```
- stockdataservice
  ```
  GET http://localhost:8084/v1/aggregatedPriceInformation/retrieve?symbol=SAP.DE&symbol=VOW3.DE&symbol=ADS.DE&start=2012-08-01T09:15:29.442856700Z&end=2022-08-05T09:15:29.442856700Z
  ```

##### 3. Get a CSV file of the just retrieved data. 
```
GET http://localhost:8085/v1/aggregatedPriceInfo/csv?lei=529900NNUPAGGOMPXZ31&lei=529900D6BF99LW9R2E68&lei=549300JSX0Z4CW0V5023&missingValueStrategy=LAST_VALUE&indicatorNames=SMA&indicatorNames=PER&indicatorNames=EPS&start=2018-08-01T09:15:29.442856700Z&end=2022-08-01T09:15:29.442856700Z
Accept: text/csv
```
Note that the PE-Ratio is calculated based on ifrs-full:BasicEarningsLossPerShare and the closing price. End prices of facts retrieved from financial reports are assigned by using their end of period date.


##### 4. Start retrieving precise price information for the companies on your watchlist:
```
GET http://localhost:8084/v1/precisePriceInformation/start
Accept: application/stream+json
```

## Service Description
### schedulingservice
The scheduling service triggers routines of other services by sending rabbitmq events.
- The collection of financial reports is scheduled for every monday at 8 am by the following CRON-expression: ```0 0 8 * * MON```
- The collection of aggregated price information is scheduled daily at 11 pm by the following CRON-expression: ```0 0 23 * * *```

### financialreportservice
The financialreportservice retrieves and stores financial reports in a local running MongoDB. Financial reports are retrieved after receiving an event from the scheduling service or an API call.  
Retrieved financial reports can also be viewed by using MongoDBCompass with the connection String ```mongodb://mongoUsername:mongoPassword@localhost:27017/?authMechanism=DEFAULT&authSource=admin``` .
##### API Specification
1. Endpoint to retrieve financial reports from a remote data source. If no lei are specified, all available reports will be retrieved. This will take some as there is a 20-second sleep period between each retrieved report in order to reduce the load on the remote data source.
```
  /v1/financialreports:
    get:
      summary: "GET v1/financialreports"
      operationId: "getReports"
      parameters:
      - name: "lei"
        in: "query"
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      responses:
        "200":
          description: "OK"
```
2. Retrieve already stored reports. If no legal entity identifiers are specified, all present reports will be returned.
```
  /v1/financialreports/retrieveReports:
    get:
      summary: "GET v1/financialreports/retrieveReports"
      operationId: "retrieveReports"
      parameters:
      - name: "lei"
        in: "query"
      responses:
        "200":
          description: "OK"
```

### stockdataservice
The stockdataservice manages aggregated (open, high, low, close, volume, adjusted close of each day) and precise price information. Aggregated price information is retrieved after receiving an event from the scheduling service or an api call. The retrieval of precise price information takes place after receiving an api call. The stockdataservice manages a watchlist which has to be populated in order to retrieve aggregated price information based on events. 
Stored data can also be viewed by using the Influx web interface (http://localhost:8086/ username: user password: password).
##### API Specification
1. Retrieve aggregated price information. If no symbols are specified, the watchlist will be used instead.
```
  /v1/aggregatedPriceInformation:
    get:
      summary: "GET v1/aggregatedPriceInformation"
      operationId: "get"
      parameters:
      - name: "symbol"
        in: "query"
        required: true
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      responses:
        "200":
          description: "OK"
```
2. Get already retrieved aggregated price information.
```
  /v1/aggregatedPriceInformation/retrieve:
    get:
      summary: "GET v1/aggregatedPriceInformation/retrieve"
      operationId: "retrieve"
      parameters:
      - name: "symbol"
        in: "query"
        required: true
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      responses:
        "200":
          description: "OK"
```
3. Start retrieving precise price information from a remote data source. If no symbols are specified the watchlist will be used instead.
```
  /v1/precisePriceInformation/start:
    get:
      summary: "GET v1/precisePriceInformation/start"
      operationId: "start"
      parameters:
      - name: "symbol"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
```
4. Stop retrieving precise price information.
```
  /v1/precisePriceInformation/stop:
    get:
      summary: "GET v1/precisePriceInformation/stop"
      operationId: "stop"
      responses:
        "200":
          description: "OK"
```
5. Get already retrieved precise price information from the local running InfluxDB.
```
  /v1/precisePriceInformation:
    get:
      summary: "GET v1/precisePriceInformation"
      operationId: "get"
      parameters:
      - name: "symbol"
        in: "query"
        required: true
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      responses:
        "200":
          description: "OK"
```
6. Get the watchlist.
```
  /v1/watchlist:
    get:
      summary: "GET v1/watchlist"
      operationId: "getWatchlist"
      responses:
        "200":
          description: "OK"
```
7. Add symbols to the watchlist.
```
  /v1/watchlist:
    put:
      summary: "PUT v1/watchlist"
      operationId: "addToWatchlist"
      parameters:
      - name: "symbol"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
```
8. Delete symbols from the watchlist.
```
  /v1/watchlist:
    delete:
      summary: "DELETE v1/watchlist"
      operationId: "removeFromWatchlist"
      parameters:
      - name: "symbol"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
```
### composerservice
The composerservice is used to compose csv files from stored data. It also manages data about companies which is necessary to link financial data (identified by a legal entity identifier) to stock prices (identified by a symbol). The current state of the composerservice is able to produce a csv file including aggregated price information, facts from financial reports and simple moving average and price to earnings ratio indicators.
##### API Specification
1. Adds a company which will be used to link financial reports to stock price information. By appending the query-parameter addToWatchList=true the symbols of the companies will be added to the watchlist of the stockdataservice.
```
  /v1/company:
    put:
      summary: "PUT v1/company"
      operationId: "addCompany"
      parameters:
      - name: "addToWatchList"
        in: "query"
        required: true
        schema:
          type: "boolean"
      responses:
        "200":
          description: "OK"
```
2. Get information about a stored company.
```
  /v1/company/{lei}:
    get:
      summary: "GET v1/company/{lei}"
      operationId: "getCompany"
      parameters:
      - name: "lei"
        in: "path"
        required: true
        schema:
          type: "string"
      responses:
        "200":
          description: "OK"
```
3. Add symbols of a company to the watchlist of the stockdataservice.
```
  /v1/company/toWatchList:
    put:
      summary: "PUT v1/company/toWatchList"
      operationId: "addCompaniesToWatchList"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
```
4. Retrieve aggregated price information and financial reports for a company. The provided start and end do not concern the collection of financial reports.
```
  /v1/company/retrieveDataForCompanies:
    get:
      summary: "GET v1/company/retrieveDataForCompanies"
      operationId: "retrieveDataForCompanies"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      - name: "aggregatedPriceInfoStart"
        in: "query"
        required: true
      - name: "aggregatedPriceInfoEnd"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
```
5. Get a csv file based on aggregated price information.
```
  /v1/aggregatedPriceInfo/csv:
    get:
      summary: "GET v1/aggregatedPriceInfo/csv"
      operationId: "getAggregatedPriceInfoCSV"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      - name: "indicatorNames"
        in: "query"
      - name: "missingValueStrategy"
        in: "query"
        schema:
          type: "string"
      responses:
        "200":
          description: "OK"
```
6. Get a csv file based on precise price information.
```
  /v1/precisePriceInfo/csv:
    get:
      summary: "GET v1/precisePriceInfo/csv"
      operationId: "getPrecisePriceCSV"
      parameters:
      - name: "symbols"
        in: "query"
        required: true
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      - name: "indicatorNames"
        in: "query"
      - name: "missingValueStrategy"
        in: "query"
        schema:
          type: "string"
      responses:
        "200":
          description: "OK"
```


## Know issues
- Partially retrieved reports are not extended.
- When adding creating new companies and directly adding them to the watchlist of the stockdataservice the Response received by the StockDataServiceAdapter WebClient contains a single String containing the added symbols rather than a list of strings. This is behavior is also described in https://github.com/spring-projects/spring-framework/issues/24734. The mentioned workaround does not seam to apply for this project. 
  - Expected Response: ```["SAP.DE", "VOW3.DE", "ADS.DE"]```
  - Actual Response: ```"[\"SAP.DE\",\"VOW3.DE\",\"ADS.DE\"]"```
- After the retrieval of precise price information is stopped, it cannot be started again without restarting the whole microservice.