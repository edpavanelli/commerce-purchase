package net.mycompany.commerce.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchasemgmt.domain.model.Currency;
import net.mycompany.commerce.purchasemgmt.infrastructure.repository.CurrencyRepository;

@Component
public class CurrencyInitializerMock {

    private static final Logger log = LoggerFactory.getLogger(CurrencyInitializerMock.class);
    private CurrencyRepository currencyRepository;
    
    public CurrencyInitializerMock(CurrencyRepository currencyRepository) {
		this.currencyRepository = currencyRepository;
	}
    
    // Mock representing a database already populated
    @EventListener(ApplicationReadyEvent.class)
    public void insertDefaultCurrency() {
    	log.info("Checking if default currency USD exists and creating it...");
        currencyRepository.findByCode("USD").ifPresentOrElse(
            c -> {},
            () -> {
                Currency currency = new Currency();
                currency.setCode("USD");
                currency.setName("United States Dollar");
                currency.setCountry("United States");
                currencyRepository.save(currency);
            }
        );
        log.info("Currency USD is ready to use.");
    }
}