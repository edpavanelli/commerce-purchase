package net.mycompany.commerce.common.util;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.mycompany.commerce.purchase.infrastructure.integration.treasury.TreasuryExchangeRateProvider;

public class DateUtils {
	
	private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

	private DateUtils() {
	}
	
	public static LocalDate getDateSixMonthsBack(LocalDate referenceDate) {
        return referenceDate.minusMonths(6);
    }
	
	public static boolean isDateToday(LocalDate date) {
		
    	LocalDate today = LocalDate.now();
    	log.debug("Today's date: {}", today);
    	
    	return date.isEqual(today);
			
    	
    
    }
}
