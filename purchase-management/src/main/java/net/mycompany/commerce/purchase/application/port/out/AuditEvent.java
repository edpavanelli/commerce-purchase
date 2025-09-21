package net.mycompany.commerce.purchase.application.port.out;

import java.time.LocalDateTime;

public record AuditEvent (
	    String transactionId,
	    String operation,
	    String changedBy,
	    LocalDateTime changedDate
	) {}
