package net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.common;

import java.time.format.DateTimeFormatter;

public final class TreasuryApiConstants {
	
	private TreasuryApiConstants() {}

    public static final DateTimeFormatter TREASURY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
