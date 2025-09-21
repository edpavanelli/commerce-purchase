package net.mycompany.commerce.purchase.application.port.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaAuditEventPublisher implements AuditEventPublisher{

	
	private static final Logger log = LoggerFactory.getLogger(KafkaAuditEventPublisher.class);

    @Override
    public void publishAuditEvent(AuditEvent event) {
        // Aqui você simula enviar para uma fila (Kafka, Rabbit…)
        log.info("[FAKE QUEUE] Publicando evento de auditoria: {}", event);
        // poderia serializar em JSON, etc.
    }
}
