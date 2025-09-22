package net.mycompany.commerce.purchase.application.port.out;

public interface AuditEventPublisher {
	void publishAuditEvent(AuditEvent event);
}
