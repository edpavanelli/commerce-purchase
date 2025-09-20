package net.mycompany.commerce.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.infrastructure.repository.CurrencyRepository;

@Component
public class CurrencyInitializer {

    private static final Logger log = LoggerFactory.getLogger(CurrencyInitializer.class);
    private CurrencyRepository currencyRepository;
    
    public CurrencyInitializer(CurrencyRepository currencyRepository) {
		this.currencyRepository = currencyRepository;
	}
    
    //mock representing a database already charged
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
