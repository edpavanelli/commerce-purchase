package net.mycompany.commerce.common.util;

public class StringUtils {

	
	public static String capitalizeFirstLetter(String input) {
	    if (input == null || input.isEmpty()) {
	        return input; 
	    }
	    return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}
}
