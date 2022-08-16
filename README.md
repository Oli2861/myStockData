# myStockData

myStockData is a prototypical implementation of an architecture for retrieving and managing stock price relevant data. Since it is a prototype rather than a production-ready software to manage stock data, the data retrieved, stored and managed by myStockData is to be treated with caution and is not suitable to base investment decisions on.

## Architecture
![architektur.drawio.png](architektur.drawio.png)

## Docker Hub


## Clone
`git clone https://github.com/Oli2861/myStockData --recursive`

## Build & Run

### 1. Build all services (requires gradle)
- GitBash: `cd financialreportservice && ./gradlew build && cd ../ && cd schedulingservice && ./gradlew build && cd ../ && cd stockdataservice && ./gradlew build && cd ../ && cd composerservice && ./gradlew build && cd ../ && cd gateway && ./gradlew build && cd ../`
- PowerShell: `cd financialreportservice; ./gradlew build; cd ../; cd schedulingservice; ./gradlew build; cd ../; cd stockdataservice; ./gradlew build; cd ../; cd gateway; ./gradlew build; cd ../; cd composerservice; ./gradlew build; cd ../`

### 2. Start all containers
`docker-compose up`

## Testing Guide
The following requests are part of TestRequests.http and can simplify the testing process when using them in conjunction with the IDE IntelliJ Idea.
The response should be similar to the content of https://github.com/Oli2861/myStockData/blob/main/example_response.csv
##### 1. Create companies and add them to your watchlist:
```
PUT http://localhost:8084/v1/stockdata/company?addToWatchList=true
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
            "system": "Yahoo Finance",
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
            "system": "Yahoo Finance",
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
            "system": "Yahoo Finance",
            "exchange": "XETRA"
          }
        ]
      }
    ]
  }
]
```
##### 2. Retrieve financial reports and aggregated price information for the companies:
- stockdataservice
  ```
  GET http://localhost:8080/v1/stockdata/aggregatedPriceInformation/retrieve?start=2012-08-01T09:15:29.442856700Z&end=2022-08-05T09:15:29.442856700Z
  Accept: application/stream+json
  ```
- financialreportservice: 
  ```
  GET http://localhost:8080/v1/financialreport/retrieveReports?lei=529900NNUPAGGOMPXZ31&lei=529900D6BF99LW9R2E68&lei=549300JSX0Z4CW0V5023
  Accept: application/stream+json
  ```
Make sure to wait ~2 minutes after retrieving the data since the financialreportservice sleeps for 20 seconds before retrieving a report.
##### 3. Get a CSV file of the just retrieved data. 
```
GET http://localhost:8080/v1/composedData/aggregatedPriceInformation/csv?lei=529900NNUPAGGOMPXZ31&lei=529900D6BF99LW9R2E68&lei=549300JSX0Z4CW0V5023&missingValueStrategy=LAST_VALUE&indicatorNames=SMA&indicatorNames=PER&ifrsFact=ifrs-full:DilutedEarningsLossPerShare&ifrsFact=ifrs-full:Equity&start=2018-08-01T09:15:29.442856700Z&end=2022-08-01T09:15:29.442856700Z
Accept: text/csv
```
Note that the PE-Ratio is calculated based on ifrs-full:BasicEarningsLossPerShare and the closing price. End prices of facts retrieved from financial reports are assigned by using their end of period date.

##### 4. Start retrieving precise price information for the companies on your watchlist:
```
GET http://localhost:8080/v1/stockdata/precisePriceInformation/start?lei=529900NNUPAGGOMPXZ31&lei=529900D6BF99LW9R2E68&lei=549300JSX0Z4CW0V5023
Accept: application/stream+json
```

##### 5. Get some retrieved precise price information:
```
GET http://localhost:8080/v1/stockdata/precisePriceInformation?lei=529900NNUPAGGOMPXZ31&lei=529900D6BF99LW9R2E68&lei=549300JSX0Z4CW0V5023
Accept: application/stream+json
```

## Service Description
### schedulingservice
The scheduling service triggers routines of other services by sending rabbitmq events.
- The collection of financial reports is scheduled for every monday at 8 am by the following CRON-expression: 
  ```0 0 8 * * MON```
- The collection of aggregated price information is scheduled daily at 11 pm by the following CRON-expression: 
  ```0 0 23 * * *```

### financialreportservice
The financialreportservice retrieves and stores financial reports in a local running MongoDB. Financial reports are retrieved after receiving an event from the scheduling service or an API call.  
Retrieved financial reports can also be viewed by using MongoDBCompass with the connection String ```mongodb://mongoUsername:mongoPassword@localhost:27017/?authMechanism=DEFAULT&authSource=admin``` .
##### OpenAPI Specification
```
  /v1/financialreports:
    get:
      summary: "GET v1/financialreport"
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
---
  /v1/financialreports/retrieveReports:
    get:
      summary: "GET v1/financialreport/retrieveReports"
      operationId: "retrieveReports"
      parameters:
      - name: "lei"
        in: "query"
      responses:
        "200":
          description: "OK"
```

### stockdataservice
The stockdataservice manages aggregated (open, high, low, close, volume, adjusted close of each day) and precise price information. Aggregated price information is retrieved after receiving an event from the scheduling service or an api call. The retrieval of precise price information takes place after receiving an api call.
Before price information can be retrieved, a company with symbol and associated system field must first be created. Price information can also be retrieved automatically by adding their legal entity identifiers to the watchlist.
The stockdataservice manages a watchlist which has to be populated in order to retrieve aggregated price information based on events.
Stored data can also be viewed by using the Influx web interface (http://localhost:8086/ username: user password: password).
##### OpenAPI Specification
When addressed via the gateway, ```stockdata/``` is inserted between ```/v1/``` and ```aggregatedPriceInfo```, ```precisePriceInfo```, ```company``` or ```watchlist``` respectively.
```
  /v1/aggregatedPriceInformation:
    get:
      summary: "GET v1/aggregatedPriceInformation"
      operationId: "get"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      responses:
        "200":
          description: "OK"
---
  /v1/aggregatedPriceInformation/retrieve:
    get:
      summary: "GET v1/aggregatedPriceInformation/retrieve"
      operationId: "retrieve"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      responses:
        "200":
          description: "OK"
---
  /v1/company:
    get:
      summary: "GET v1/company"
      operationId: "getCompanies"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
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
---
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
---
  /v1/precisePriceInformation:
    get:
      summary: "GET v1/precisePriceInformation"
      operationId: "get"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      - name: "start"
        in: "query"
      - name: "end"
        in: "query"
      responses:
        "200":
          description: "OK"
---
  /v1/precisePriceInformation/start:
    get:
      summary: "GET v1/precisePriceInformation/start"
      operationId: "start"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
---
  /v1/precisePriceInformation/stop:
    get:
      summary: "GET v1/precisePriceInformation/stop"
      operationId: "stop"
      responses:
        "200":
          description: "OK"
---
  /v1/watchlist:
    get:
      summary: "GET v1/watchlist"
      operationId: "getWatchlist"
      responses:
        "200":
          description: "OK"
    put:
      summary: "PUT v1/watchlist"
      operationId: "addToWatchlist"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
    delete:
      summary: "DELETE v1/watchlist"
      operationId: "removeFromWatchlist"
      parameters:
      - name: "lei"
        in: "query"
        required: true
      responses:
        "200":
          description: "OK"
```

### composerservice
The composerservice is used to compose csv files from data stored by other services. The current state of the composerservice is able to produce a csv file including aggregated price information, facts from financial reports and simple moving average and price to earnings ratio indicators. Financial facts and indicators are not yet implemented for precise price information csv files.
##### OpenAPI Specification
When addressed via the gateway, ```composedData/``` is inserted between ```/v1/``` and ```aggregatedPriceInfo/csv``` or ```precisePriceInfo/csv``` respectively.
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
      - name: "ifrsFact"
        in: "query"
      responses:
        "200":
          description: "OK"
---
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
- After the retrieval of precise price information is stopped, it cannot be started again without restarting the whole microservice.
- - Timestamps of retrieved price information concerning stops are 15 minutes behind
- Gateway does not forward json/stream data / only if the stream is terminated
- If indicators based on financial figures are part of the requested csv file from the composerservice and the financial figure is not present, a null column is present 
