payment app



H2
autenticação segurança
actuator para observabilidade
difusor de logs
auditoria
swagger
circuitbreaker (resilience)
filas e topicos

executar o https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange quando subir a aplicação e salvar em um redis(h2 no caso)



Requirement #1: Store a Purchase Transaction
Your application must be able to accept and store (i.e., persist) a purchase transaction with a description, transaction date, and a purchase amount in United States dollars. When the transaction is stored, it will be assigned a unique identifier.

Field requirements
● Description: must not exceed 50 characters
● Transaction date: must be a valid date format
● Purchase amount: must be a valid positive amount rounded to the nearest cent
● Unique identifier: must uniquely identify the purchase

Requirement #2: Retrieve a Purchase Transaction in a Specified Country’s Currency
Based upon purchase transactions previously submitted and stored, your application must provide a way to retrieve the stored purchase transactions converted to currencies supported by the Treasury Reporting Rates of Exchange API based
upon the exchange rate active for the date of the purchase.
https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange
The retrieved purchase should include the identifier, the description, the transaction date, the original US dollar purchase amount, the exchange rate used, and the converted amount based upon the specified currency’s exchange rate for the date of the purchase.

Currency conversion requirements

● When converting between currencies, you do not need an exact date match, but must use a currency conversion rate less than or equal to the purchase date from within the last 6 months.
● If no currency conversion rate is available within 6 months equal to or before the purchase date, an error should be returned stating the purchase cannot be converted to the target currency.
● The converted purchase amount to the target currency should be rounded to two decimal places (i.e., cent).

ASK ASK ASK ASK
Can I assume that currency conversion requests for purchase transactions dated within the last six months are significantly more common than requests for transactions older than six months?”
E posso assumir que o arredondamento do valor final é pra cima?

Entity:

user
  -id
  -login
  -pass

purchase_transaction
  -id(database)
  -uuId(Unique identifier)
  -amount(use monetary java type)
  -currency(entity, but just dollar) 
  -transactionDate(callendar?)
  -purchaseDate
  -description(String 50 characters)

purchase_transaction_audit
  -id
  -user_id
  -purchase_transaction_id
  -operation(CREATE, UPDATE, DELETE)
  -changed_by(systemUser)
  -changedDate()

currency(enum?)
  -id
  -nemotecnico
  -description



StorePurchaseRequestDTO
  -userTOken
  -amount
  -description  
  -purchaseDate


StorePurchaseResponseDTO
  -uuid
  -data

currencyAvaliableRequestDTO


currencyAvaliableResponseDTO


PurchaseretrieveRequestDTO
  -userTOken
  -uuId
  -newCurrency


PurchaseretrieveResponsetDTO
  -uuId
  -description
  -date
  -currency(original)
  -amount
  -currency(new)
  -exchangeRate
  -convertedAmount


Validations


tasks:

-create entity (JPA anotations)
-configure logs

-StorePurchase
   -create DTOS
   -create Controller (topic, queue, logs)
   -create Service(logs)
   -implement entities methods

-PurchaseRetrieve
   -create DTOS
   -create Controller (REST, logs)
   -create Service(logs)
   -implement entities methods
   -CurrencyAvaliable


-auth
  -token
  -mTLS