package net.mycompany.commerce.purchasemgmt.application.port.out;

public interface AuditEventPublisher {
	void publishAuditEvent(AuditEvent event);
}
