package net.mycompany.commerce.purchase;

import net.mycompany.commerce.purchase.model.Currency;
import net.mycompany.commerce.purchase.repository.CurrencyRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Initializer {

    
    private CurrencyRepository currencyRepository;
    
    public Initializer(CurrencyRepository currencyRepository) {
		this.currencyRepository = currencyRepository;
	}
    
    //mock representing a database already charged
    @EventListener(ApplicationReadyEvent.class)
    public void insertDefaultCurrency() {
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
    }
}
