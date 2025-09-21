package net.mycompany.commerce.common.util;

import java.time.LocalDate;

public class DateUtils {

	private DateUtils() {
	}
	
	public static LocalDate getDateSixMonthsBack(LocalDate referenceDate) {
        return referenceDate.minusMonths(6);
    }
	
	public static boolean isDateWithin6MonthsFromNow(LocalDate date) {
    	
    	LocalDate today = LocalDate.now();
		
    	if(date.isEqual(today)) {
			return true;
    		
    	}
    	
        LocalDate sixMonthsBackDate = DateUtils.getDateSixMonthsBack(LocalDate.now());
        
        if(date.isEqual(sixMonthsBackDate)) {
			return true;
    		
    	}
        
        return (date.isBefore(today) && date.isAfter(sixMonthsBackDate));
    }
}
