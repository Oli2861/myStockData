# myStockData

##### Start gradle build
 `.\gradlew build`
 
##### Start Docker Containers 
`docker-compose up`

##### Start all gradle builds (requires gradle)
- GitBash: `cd financialreportservice && ./gradlew build && cd ../ && cd schedulingservice && ./gradlew build && cd ../ && cd stockdataservice && ./gradlew build && cd ../`
- PowerShell: `cd financialreportservice; ./gradlew build; cd ../; cd schedulingservice; ./gradlew build; cd ../; cd stockdataservice; ./gradlew build; cd ../`
