# How to run the service

- via command line
```
$PROJECT_PATH/run.sh

or

mvn clean install
mvn spring-boot:run
```

> **_NOTE:_**  default port changed to **_server.port=9000_**

# How to use the API

> **_NOTE:_**  Make sure to point to correct path of trades.csv file for curl
> 
> **_NOTE:_**  CURL fails when reading file > 2GB

```
curl -v -X POST -H 'Content-Type: text/csv' --data-binary '@trades.csv' "http://127.0.0.1:9000/api/v1/enrich"
```

Sample response:
```
(base) dzenin@pl-m-dzenin trade-enrichment-task-main % curl -v -X POST -H 'Content-Type: text/csv' --data-binary '@trades.csv' "http://127.0.0.1:9000/api/v1/enrich"
Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 127.0.0.1:9000...
* Connected to 127.0.0.1 (127.0.0.1) port 9000
> POST /api/v1/enrich HTTP/1.1
> Host: 127.0.0.1:9000
> User-Agent: curl/8.4.0
> Accept: */*
> Content-Type: text/csv
> Content-Length: 113
> 
< HTTP/1.1 200 OK
< Connection: keep-alive
< Transfer-Encoding: chunked
< Content-Type: text/csv;charset=UTF-8
< Date: Mon, 27 May 2024 15:57:19 GMT
< 
date,product_name,currency,price
20160101,Treasury Bills Domestic,EUR,10.0
20160101,Corporate Bonds Domestic,EUR,20.1
20160101,REPO Domestic,EUR,30.34
20160101,Missing Product Name,EUR,35.34
```

# Any limitations of the code
- Domain model could be simplified
- Need to check if stream csv line parsing could be optimized  

# Any discussion/comment on the design
- Incoming data is read by chunks of data, profiler shows no memory degradation or huge spikes
- Caching of productId->productName mapping done at once, since quantity of records is not so huge(need to clarify available resources). It makes lookup faster.
- Need to tune functional style and immutability
- Favour clean architecture (adapters, application services, domain services and models)

# Any ideas for improvement if there were more time available
- API error handling
- Need to think about modification to then workflow itself, split huge file to blocks, enrich blocks in parallel and combine at the end. However, it depends on the expected number of executions of workflows and other conditions and the nature of data.