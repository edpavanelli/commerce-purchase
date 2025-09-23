# eCommerce Purchase Management Service

First of all, I would like to thank you for the opportunity. This project was hard work but also very entertaining.

**This project has a particular way to be tested, please read the all documentation to understand.**



#Security

This project has **JWT** and **mTLS** implemented.

Under `src/main/resources/certs` there are 5 certificates:
	<br>- client-cert.pem
	<br>- client-key.pem
	<br>- client-keystore.p12
	<br>- server-keystore.p12
	<br>- server-trustsrore.p12

They should be placed together with the code? Of course not, but it makes easier for us to test.
<br>You can use the **client-keystore.p12** in your browser to see the Swagger and the **client-cert.pem** and the **client-key.pem** in your test tool.

All security configurations are placed in  

``` 
net.mycompany.commerce.purchase
├── infrastructure
    ├── config
        ├── security
            ├── AuthController.java              # Controller for authentication endpoints
            ├── AuthRequest.java                 # DTO for authentication requests
            ├── JwtAuthenticationFilter.java     # Filter for JWT authentication
            ├── JwtService.java                  # Service for JWT operations
            └── SecurityConfig.java              # Security configuration for the application
```


#Audit

This project has a **Mock Audit** simulation. 

Every time a purchase is stored in the database, a (mock) Kafka event runs asynchrony to the Audit Service audit the purchase. 
(the request publish is just a log line)
  
The Audit logic is placed in

``` 
net.mycompany.commerce.purchase
├── infrastructure
    ├── config
        ├── audit
            ├── AuditObserver.java               # Observes and handles audit events
            ├── AuditOperation.java              # Enum for audit operations
            ├── PurchaseTransactionSubject.java  # Subject for purchase transaction audit events
            └── TransactionObserver.java         # Observes transaction events for auditing
```


#Cache

This project has a **Spring Cache** implementation to emulate a **cache aside pattern** with a real key/value memory database.

I felt free to add a technical requisite in order to implement Cache in this project:
	<br> - **Today Exchange Rates from the countries MyCompany is placed must be retrieved every day and must be cached due high demand. ** 

So when a client purchase and right away asks for Exchange (for some reason) the Exchange Rate will be retrieved from the Cache.

The Cache is configured in

``` 
net.mycompany.commerce.purchase
├── infrastructure
    ├── config
        ├── cache
            ├── CacheConfig.java                 # Configuration for caching
            └── CacheService.java                # Service for cache operations
```

and called after application startup in 

``` 
net.mycompany.commerce.purchase
├── infrastructure
    ├── integration
        └── treasury
            ├── TreasuryExchangeRateProvider.java    # Implements ExchangeRateProviderPort, fetches rates from Treasury API
```

#Logs

This project **Masks sensitive data in the logs**.

The Purchase amount is set to be masked just as an example. 


#Documentation

This project has **Swagger** with **OpenAPI** Documentation for REST API and **Springwolf** for Event-Driven API implemented.

The Rest documentation can be seeing in the browser at this location `https://localhost:8443/swagger-ui/index.html` and calling `https://localhost:8443/v3/api-docs/`. 

The Event-Driven Documentation can be seeing calling `https://localhost:8443/springwolf/docs` but can not be seeing in a browser because Springwolf need a real server with a real protocol running. Which is not our case.  

The documentation configuration are in 

``` 
net.mycompany.commerce.purchase
├── infrastructure
    ├── config
        ├── swagger
            └── OpenApiConfig.java               # Configuration for OpenAPI/Swagger documentation
```

and all over the DTOs, Controllers, Consumers and Publishers 

There is no configuration for the Springwolf outside the application.properties


#Tests

This project has **JUnit** Tests implemented.

This is a stand-alone project, which means there is not other tools supporting the execution. 

Therefore, to emulate a MessageQueue tool, two classes was implemented:
	   <br>- ProducerMock
	   <br>- QueueManagerServiceMock

The **ProducerMock** is a simulation of an other system sending a message through a queue and then listening another queue to get the response.
<br>The **QueueManagerServiceMock** is a simulation of a QueueManager with the Request and Response queues running. 
<br>Also, there is the **CurrencyInitializerMock** class that create a currency record in the database emulating a already loaded database. 

These classes exist just to help us to test, they are not part of the Purchase Management Project. These should not be in a production environment.  

```
net.mycompany.commerce.mock
│
├── CurrencyInitializerMock.java        # Initializes default currency data for dev/testing
├── ProducerMock.java                   # Mock REST controller for enqueuing purchase requests
└── QueueManagerServiceMock.java        # Simulates queue manager for purchase requests/responses
```

Inside the ProducerMock.java there is a method: 
<br>`public ResponseEntity<Map<String, String>> enqueuePurchase(@RequestBody StorePurchaseRequestDto purchase)` emulating a request queue, so this method do not return any data. 

and a method:
<br>`public ResponseEntity<StorePurchaseResponseDto> getResponse(@PathVariable("transactionId") String transactionId)`  emulating a response queue, you may call this method only if you wish to emulate a external system receiving the response from another queue.

**but**, to execute the `getResponse` method and the 
<br>`public ResponseEntity<ExchangeRateResponseDto> convertCurrency(@Valid @RequestBody ExchangeRateRequestDto request)` from the **ExchangeController** class **you need to retrieve the transactionId from the logs**

Right after calling the `enqueuePurchase` the transactionId will be logged more then once. 

I am attaching my Postman environment and collection and also .json requests under'testing' folder in the repository root to make it easier.



#Executing

to run the server...

lombok library

gradle

    



#Next Features:

 <br> - **Schedule read the ExchageRates every day and cache then**
 <br> - **Observability Mock**
 <br> - **Circuit Breaker**
 <br> - **Helm charts**
 <br> - **CI/CD pipelines**



net.mycompany.commerce.common
│
├── dto
│   ├── CurrencyDto.java                # DTO for currency details (code, name, country)
│   └── PaginationFiltersDto.java       # DTO for pagination parameters (page number, size)
│
├── mapper
│   └── CurrencyMapper.java             # Maps between Currency domain model and CurrencyDto
│
├── util
│   ├── DateUtils.java                  # Utility for date operations (calculations, checks)
│   └── StringUtils.java                # Utility for string operations (e.g., capitalization)
│
net.mycompany.commerce.mock
│
├── CurrencyInitializerMock.java        # Initializes default currency data for dev/testing
├── ProducerMock.java                   # Mock REST controller for enqueuing purchase requests
└── QueueManagerServiceMock.java        # Simulates queue manager for purchase requests/responses

net.mycompany.commerce.purchase
│
├── PurchaseManagementApplication.java  # Main Spring Boot application class
│
├── application
│   ├── exchange
│   │   ├── controller
│   │   │   └── ExchangeController.java           # REST controller for currency conversion endpoints
│   │   ├── dto
│   │   │   ├── ExchangeRateRequestDto.java       # DTO for currency exchange conversion requests
│   │   │   └── ExchangeRateResponseDto.java      # DTO for currency exchange conversion responses
│   │   └── service
│   │       └── CurrencyExchangeService.java      # Service for handling currency conversion logic
│   ├── port
│   │   └── out
│   │       ├── AuditEvent.java                  # Represents an audit event for transaction changes
│   │       ├── AuditEventPublisher.java         # Interface for publishing audit events
│   │       └── KafkaAuditEventPublisher.java    # Mock implementation for publishing audit events to Kafka
│   └── store
│       ├── consumer
│       │   └── PurchaseConsumer.java            # Consumes purchase requests from a queue and processes them
│       ├── dto
│       │   ├── StorePurchaseRequestDto.java     # DTO for purchase request data (amount, description, date)
│       │   └── StorePurchaseResponseDto.java    # DTO for purchase response (transactionId)
│       ├── mapper
│       │   └── PurchaseTransactionMapper.java   # Maps between StorePurchase DTOs and domain PurchaseTransaction
│       ├── publisher
│       │   └── PurchasePublisher.java           # Publishes purchase responses to a queue (mock integration)
│       └── service
│           └── StorePurchaseService.java        # Service for handling purchase transactions (business logic)
│
├── domain
│   ├── model
│   │   ├── AuditOperationType.java              # Enum for audit operation types (CREATE, UPDATE, DELETE)
│   │   ├── Currency.java                        # Entity representing a currency
│   │   └── PurchaseTransaction.java             # Entity representing a purchase transaction
│   ├── port
│   │   ├── ExchangeRateProviderPort.java        # Interface for fetching exchange rates
│   │   └── TransactionIdGeneratorPort.java      # Interface for generating transaction IDs
│   ├── service
│   │   └── PurchaseDomainService.java           # Domain service for purchase business logic
│   └── valueobject
│       ├── ConvertedCurrency.java               # Value object for converted currency
│       ├── ExchangeRate.java                    # Value object for exchange rate
│       └── TransactionId.java                   # Value object for transaction ID
│
├── infrastructure
│   ├── adapter
│   │   └── NanoIdTransactionIdGeneratorAdapter.java # Adapter for generating transaction IDs using NanoId
│   ├── config
│   │   ├── audit
│   │   │   ├── AuditObserver.java               # Observes and handles audit events
│   │   │   ├── AuditOperation.java              # Enum for audit operations
│   │   │   ├── PurchaseTransactionSubject.java  # Subject for purchase transaction audit events
│   │   │   └── TransactionObserver.java         # Observes transaction events for auditing
│   │   ├── cache
│   │   │   ├── CacheConfig.java                 # Configuration for caching
│   │   │   └── CacheService.java                # Service for cache operations
│   │   ├── exception
│   │   │   ├── ApiError.java                    # Represents API error details
│   │   │   ├── ApiServiceUnavaliableException.java # Exception for unavailable API services
│   │   │   ├── DataBaseNotFoundException.java   # Exception for missing database entries
│   │   │   ├── GlobalExceptionHandler.java      # Handles global exceptions in the application
│   │   │   ├── PurchaseDomainException.java     # Exception for domain errors in purchases
│   │   │   ├── PurchaseExceptionsHandler.java   # Handles purchase-related exceptions
│   │   │   ├── TraceIdFilter.java               # Filter for tracing request IDs
│   │   │   └── UnauthorizedException.java       # Exception for unauthorized access
│   │   ├── rest
│   │   │   ├── BaseApiProperties.java           # Base class for API configuration properties
│   │   │   ├── TreasuryApiProperties.java       # Configuration for Treasury API
│   │   │   ├── WebClientFactory.java            # Factory for creating WebClient instances
│   │   │   └── WebClientLoggingFilters.java     # Logging filters for WebClient
│   │   ├── security
│   │   │   ├── AuthController.java              # Controller for authentication endpoints
│   │   │   ├── AuthRequest.java                 # DTO for authentication requests
│   │   │   ├── JwtAuthenticationFilter.java     # Filter for JWT authentication
│   │   │   ├── JwtService.java                  # Service for JWT operations
│   │   │   └── SecurityConfig.java              # Security configuration for the application
│   │   ├── swagger
│   │   │   └── OpenApiConfig.java               # Configuration for OpenAPI/Swagger documentation
│   │   └── validator
│   │       ├── USDateTimeFormat.java            # Annotation for US date format validation
│   │       └── USDateTimeFormatValidator.java   # Validator for US date format
│   ├── integration
│   │   └── treasury
│   │       ├── TreasuryExchangeRateProvider.java    # Implements ExchangeRateProviderPort, fetches rates from Treasury API
│   │       ├── common
│   │       │   └── TreasuryApiConstants.java        # Constants for Treasury API integration
│   │       ├── dto
│   │       │   ├── TreasuryExchangeRateFilterDto.java # DTO for filtering exchange rate queries
│   │       │   ├── ExchangeRateDto.java             # DTO representing exchange rate data
│   │       │   ├── TreasuryExchangeRateResponseDto.java # DTO for Treasury API response
│   │       │   └── TreasuryExchangeRateSortDto.java # Enum for sorting exchange rate queries
│   │       └── mapper
│   │           └── ExchangeRateMapper.java          # Maps Treasury API DTOs to domain ExchangeRate objects
│   └── repository
│       ├── CurrencyRepository.java                  # JPA repository for Currency entities
│       └── PurchaseTransactionRepository.java       # JPA repository for PurchaseTransaction entities

# Notes
- All main source classes are listed; test classes are excluded.
- Each class is briefly described after its name.