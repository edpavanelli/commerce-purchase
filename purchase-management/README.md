# eCommerce Purchase Management Service

First of all, I would like to thank you for the opportunity. This project was hard work but also very entertaining.

**This project has a particular way to be tested, please read the all documentation to understand.**



#Security

This project has **JWT** and **mTLS** implemented.

Under `src/main/resources/certs` there are 5 certificates:
 - client-cert.pem
 - client-key.pem
 - client-keystore.p12
 - server-keystore.p12
 - server-trustsrore.p12

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

Once this project is an opportunity to show you what I know, I felt free to add a Requirement #3 in order to implement Cache in this project:

 - **Today Exchange Rates from the countries MyCompany is placed must be retrieved every day and must be cached due high demand. ** 

So when a client purchase and right away asks for Exchange, the Exchange Rate will be retrieved from the Cache.

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

The Rest documentation can be seeing in the browser at this location `https://localhost:8443/swagger-ui/index.html` and called by API `https://localhost:8443/v3/api-docs`. 

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


#Services

There are two services implemented:

The **Store Purchase** Service is to save a new Purchase in the database. 

Like the Cache, I add a Requirement #4 in order to implement messaging in this project:

- ** Queue must be implemented to Store new Purchases due to high demand **  


The complete logic are implemented in:

``` 
net.mycompany.commerce.purchase
├── application
    ├── port
    │   └── out
    │       ├── AuditEvent.java                  # Represents an audit event for transaction changes
    │       ├── AuditEventPublisher.java         # Interface for publishing audit events
    │       └── KafkaAuditEventPublisher.java    # Mock implementation for publishing audit events to Kafka
    └── store
        ├── consumer
        │   └── PurchaseConsumer.java            # Consumes purchase requests from a queue and processes them
        ├── dto
        │   ├── StorePurchaseRequestDto.java     # DTO for purchase request data (amount, description, date)
        │   └── StorePurchaseResponseDto.java    # DTO for purchase response (transactionId)
        ├── mapper
        │   └── PurchaseTransactionMapper.java   # Maps between StorePurchase DTOs and domain PurchaseTransaction
        ├── publisher
        │   └── PurchasePublisher.java           # Publishes purchase responses to a queue (mock integration)
        └── service
            └── StorePurchaseService.java        # Service for handling purchase transactions (business logic)
```


And the **Currency Exchange** Service is to convert the purchase amount between different currencies. 

The complete logic are implemented in:

``` 
net.mycompany.commerce.purchase
├── application
│   ├── exchange
│       ├── controller
│       │   └── ExchangeController.java           # REST controller for currency conversion endpoints
│       ├── dto
│       │   ├── ExchangeRateRequestDto.java       # DTO for currency exchange conversion requests
│       │   └── ExchangeRateResponseDto.java      # DTO for currency exchange conversion responses
│       └── service
│           └── CurrencyExchangeService.java      # Service for handling currency conversion logic
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
    ├── integration
        └── treasury
            ├── TreasuryExchangeRateProvider.java    # Implements ExchangeRateProviderPort, fetches rates from Treasury API
            ├── common
            │   └── TreasuryApiConstants.java        # Constants for Treasury API integration
            ├── dto
            │   ├── TreasuryExchangeRateFilterDto.java # DTO for filtering exchange rate queries
            │   ├── ExchangeRateDto.java             # DTO representing exchange rate data
            │   ├── TreasuryExchangeRateResponseDto.java # DTO for Treasury API response
            │   └── TreasuryExchangeRateSortDto.java # Enum for sorting exchange rate queries
            └── mapper
                └── ExchangeRateMapper.java          # Maps Treasury API DTOs to domain ExchangeRate objects
```
 
 
This services could be split into 2 different Micro Services, but I decided to put it together for 3 reasons:

 - Implementation Time: I wanted to focus first in other aspects like, auth, log, exception, cache, documentation, etc...
 - testing: 
 - a well made monolith can be better then split into Micro Services from the beggining: 
 
#Production

The repository **main** branch should have all set to be deployed in production:
	
 - Logs at Error level.
 - Certificates externalized in a secure storage service.
 - Security properties, like JWT and client secrets, in a HSM. 
 - Environment properties 

but, this could make harder for you to test, so this application is **not ready yet** to be productive. Consider the **main** branch just a Q&A approved branch.

This project will be closer to production when the next features are implemented. 
 
 
#Next Features

I hadn't have time to implement all basic functions for a production environment so this are the main features that are missing:

- **Schedule read the ExchageRates every day and cache then**
- **actuator**
- **dockerfile**
- **Circuit Breaker**
- **Observability Integration**
- **Helm charts**
- **CI/CD pipelines**

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
<br>`POST (/commerce/purchase/v1/store)` emulating a request queue, so this method do not return any data. 

and a method:
<br>`GET (/commerce/purchase/v1/{transactionId})`  emulating a response queue, you may call this method only if you wish to emulate a external system receiving the response from another queue.

**but**, to execute the `GET (/commerce/purchase/v1/{transactionId})` and `POST (/purchase/exchange/v1/convertCurrency)` method from the **ExchangeController** class **you need to retrieve the transactionId from the logs**

Right after calling the `enqueuePurchase` the transactionId will be logged more then once. 

I am attaching my Postman environment and collection and also .json requests under'testing' folder in the repository root to make it easier.



#Executing

This is a Spring Boot project with Gradle, so to startup the project, run `./gradlew bootRun` in the project root folder. 

This project uses **Lombok**, so if you do not already have installed the Entities, DTOs and ObjectValues can present errors in where they are called. 
Install [Lombok](https://projectlombok.org/download) to stop seeing these errors.    




#All project structure

``` 
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
```

---

# C4 Model — Purchase Management

**Project:** `net.mycompany.commerce.purchase`

**Overview:** C4 documentation (Context, Containers, Components, Code + flows and sequence diagrams) of the Purchase Management microservice.

---

## 1. System Context (C1)

### 1.1 Functional Summary
The **Purchase Management** is responsible for processing and persisting purchase transactions requests publishing audit events, and converting amounts between currencies by providing endpoints for currency conversion and integrations with external providers (e.g. U.S. Treasury Fiscal Data). It includes components for authentication (JWT), cryptographic protocol (mTls), Cache aside, publish auditing events and Message Queue integration, REST and Event-Driven API documentation. 

### 1.2 External Actors
- **External systems**: Web or mobile app, other commerce microservices or external systems that stores purchases transactions and requests currency conversion.
- **Queue System**: Message broker used to enqueue purchases requests/responses.
- **Treasury API**: External service that provides exchange rates.
- **Audit System**: Publisher of audit events.


### 1.3 Context Diagram

![Context Diagram](doc/c4-images/1.png)

---

## 2. Containers (C2)

### 2.1 Main Containers
- **purchase-management (Spring Boot app)** — main executable exposing REST endpoints and queue consumers.
  - Entrypoint: `PurchaseManagementApplication.java`
  - Cache daily Exchange Rates (Treasury API integration)
  - Exposes HTTP API To Currency Conversion (Auth + Internal endpoints + Treasury API integration)
  - Consumes and publishes messages in Queues to store purchase transactions (Auth + Internal consumer and publisher + Audit transaction)
  - Publishes audit events in external brokers
  - Saves Purchase Transactions in RDBMS Database
  - Provides Technical Documentation for REST API and Queue consumers and publishers
   

- **Database (RDBMS)** — stores `Currency` and `PurchaseTransaction` entities.

- **Queue Events** — queue for purchase requests/responses; `PurchaseConsumer.java ` and `PurchasePublisher.java`.

- **Audit Events** — Publishes requests into external broker; `AuditEvent.java `.

- **Treasury API (external)** — external service to fetch exchange rates `TreasuryExchangeRateProvider.java`.

- **Exchange Rates Cache** — Consumes Treasury API and Cache results `TreasuryExchangeRateProvider.java`.

- **Logging** — Masks sensitive data.

### 2.2 Container Diagram

![Container Diagram](doc/c4-images/2.png)

---

## 3. Components (C3)

### 3.1 Component Map of `application` module (store + exchange)

**Exchange Subsystem**
- `ExchangeController` — REST endpoints for conversion (request/response DTOs: `ExchangeRateRequestDto`, `ExchangeRateResponseDto`).
- `CurrencyExchangeService` — conversion flow: get purchase transaction, retrieves Exchange Rate via `CacheService` or `ExchangeRateProviderPort`, orchestrates domain validations (via `PurchaseDomainService`)and returns `ConvertedCurrency`.

**Store Subsystem**
- `PurchaseConsumer` — listener/consumer receiving `StorePurchaseRequestDto` from the request queue, transforms with `PurchaseTransactionMapper` and calls `StorePurchaseService`.
- `StorePurchaseService` — generates `TransactionId` (via `TransactionIdGeneratorPort`), persists `PurchaseTransaction` and publishes `StorePurchaseResponseDto` to queue via `PurchasePublisher`.
- `PurchasePublisher` — sends the response to response queue.
- `KafkaAuditEventPublisher` — implements `AuditEventPublisher` and sends audit events to Kafka broker.

**Infrastructure/Adapters**
- `NanoIdTransactionIdGeneratorAdapter` — generates IDs (NanoId) implementing `TransactionIdGeneratorPort`.
- `TreasuryExchangeRateProvider` — implements `ExchangeRateProviderPort`, makes calls to Treasury API via `WebClient`.
- `CacheService` — encapsulates cache (configured by `CacheConfig`).
- `TraceIdFilter`, `JwtAuthenticationFilter`, `SecurityConfig` — cross-cutting concerns.

**Repositories / Mappers**
- `CurrencyRepository`, `PurchaseTransactionRepository` — JPA repositories.
- `CurrencyMapper`, `PurchaseTransactionMapper`, `ExchangeRateMapper` — conversions between DTOs/Entities/Domain VOs.

### 3.2 Supporting Components (common + mock)
- `CurrencyDto`, `PaginationFiltersDto`, `CurrencyMapper` — shared DTOs and mappers.

### 3.3 Component Diagram

![Component Diagram](doc/c4-images/3.png)

---

## 4. Main Flows (sequence and events)

### 4.1 Flow: Currency conversion (synchronous HTTP)
1. Client calls `POST /exchange/convert` on `ExchangeController` with `ExchangeRateRequestDto`.
2. `ExchangeController` validates request and calls `CurrencyExchangeService`.
3. `CurrencyExchangeService` checks `CacheService` for rate.
   - If not found, calls `ExchangeRateProviderPort` (`TreasuryExchangeRateProvider`) to fetch rate from Treasury.
   - Maps response to `ExchangeRate` VO and stores in cache.
4. Calculates `ConvertedCurrency` and returns `ExchangeRateResponseDto`.
5. `ExchangeController` responds to client.

**Sequence**

![SequenceOne](doc/c4-images/4.png)

### 4.2 Flow: Purchase processing (asynchronous via queue)
1. External system (Producer/Front) publishes `StorePurchaseRequestDto` to the queue.
2. `PurchaseConsumer` consumes the message.
3. `PurchaseConsumer` maps DTO to `PurchaseTransaction` using `PurchaseTransactionMapper`.
4. `StorePurchaseService` calls `PurchaseDomainService` to validate rules (e.g. fields, limits, currency conversion if needed via `ExchangeRateProviderPort`).
5. `TransactionIdGeneratorPort` (NanoId adapter) generates `TransactionId`.
6. Persists `PurchaseTransaction` via `PurchaseTransactionRepository`.
7. Publishes `StorePurchaseResponseDto` to the queue with `PurchasePublisher`.
8. Publishes `AuditEvent` through `AuditEventPublisher` (Kafka).

**Sequence**

![SequenceTwo](doc/c4-images/5.png)

---

## 5. Data Models & Contracts

### 5.1 Main Entities
- `Currency` (id, code, name, country, updatedAt)
- `PurchaseTransaction` (id, transactionId, amount, currencyCode, description, date, status, createdAt, updatedAt)

### 5.2 DTOs / Contracts
- `ExchangeRateRequestDto` { fromCurrency, toCurrency, amount, date? }
- `ExchangeRateResponseDto` { originalAmount, convertedAmount, rate, timestamp }
- `StorePurchaseRequestDto` { amount, currency, description, date }
- `StorePurchaseResponseDto` { transactionId }
- `AuditEvent` { entityId, operation, timestamp, payload }

### 5.3 Queue Events
- `store.purchase.request` — payload `StorePurchaseRequestDto`
- `store.purchase.response` — payload `StorePurchaseResponseDto`
- `audit.event` — payload `AuditEvent`

---

## 6. Non-functional & Cross-cutting Concerns

### 6.1 Security
- JWT authentication via `JwtAuthenticationFilter`.
- Role-based authorization for sensitive endpoints (e.g. admin endpoints).
- TraceId propagation via `TraceIdFilter` for log/request correlation.

### 6.2 Resilience
- Timeouts and retries on calls to `Treasury` with `WebClient` and exponential backoff.
- Circuit Breaker (e.g. Resilience4j) recommended when calling `Treasury`.
- Validation and dead-letter queue (DLQ) for messages that fail repeatedly.

### 6.3 Observability
- Structured logs (traceId, spanId, transactionId)
- Metrics (request count, latency, queue lag, consumer throughput)
- Export metrics to Prometheus and dashboards in Grafana

### 6.4 Performance & Cache
- Cache for exchange rates (TTL configurable in `CacheConfig`).
- DB indices on `transactionId`, `date` for fast queries.

### 6.5 Tests
- Mocks (`mock` package) for queueing and currency initialization.
- Unit tests for `PurchaseDomainService`, integration tests for `TreasuryExchangeRateProvider` (using WireMock) and contract tests for the queue.

---

## 7. Deployment / Infrastructure (C4 Deployment suggestions)

- **Kubernetes (recommended)**
  - Deployment `purchase-management` with readiness/liveness probes.
  - ConfigMap/Secrets for `Treasury` credentials, JWT secrets, DB connection.
  - Stateful/Deployment for DB (or managed RDS service).
  - Kafka cluster (or managed Kafka) for queues and audit topics.
  - Horizontal Pod Autoscaler based on CPU/latency.

- **Topology**

![Topology](doc/c4-images/6.png)

---

## 8. Architectural Recommendations & Improvements

1. **Circuit Breaker + Bulkhead** for external calls (Treasury).
2. **DLQ and Retry Policy** for queue consumers; monitor failed messages.
3. **Contracts / Schema Registry** (Avro/JSON Schema) for queue messages to avoid contract break on deploy.
4. **API Gateway** to centralize authentication and routing.
5. **Domain Events explicit**: normalize `AuditEvent` and other events in a common schema.
6. **Migrations (Flyway/Liquibase)** to manage DB schema.
7. **Feature flags** to release experimental behaviors (e.g. new rate logic).

---

## 9. Glossary and Responsibilities (quick mapping)
- **ExchangeController**: responsible for exposing exchange endpoints and light validation.
- **CurrencyExchangeService**: retrieve/cache rate and calculate conversion.
- **PurchaseConsumer**: orchestrate message processing from the queue.
- **StorePurchaseService**: application logic for creating transactions.
- **PurchaseDomainService**: pure business rules (validations and invariants).
- **NanoIdTransactionIdGeneratorAdapter**: responsible for creating unique IDs.
- **KafkaAuditEventPublisher**: publish audit events.

---

## 10. Possible Next Steps (delivery suggestions)
- Generate C4 visual diagrams with [PlantUML / Structurizr] exporting the above mermaid to visual tools.
- Create operation playbooks (playbook for queue failures, treasury failures, DB recovery).
- Map concrete endpoints (urls, verbs, payloads) if you want generated OpenAPI documentation.

---

_If you want, I can:_
- transform these mermaid diagrams into PlantUML (for Structurizr)
- generate a README + summarized diagram in PNG (if you ask me to create files)
- list REST endpoints complete with payload examples

