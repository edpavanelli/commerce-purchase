package net.mycompany.commerce.purchase;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class Utils {
	
	
	public static String getNanoId() {
		return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET,
                16);
	}
}
