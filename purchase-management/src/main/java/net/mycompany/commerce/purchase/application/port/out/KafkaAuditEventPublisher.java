package net.mycompany.commerce.purchase.application.port.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaAuditEventPublisher implements AuditEventPublisher{

	private static final Logger log = LoggerFactory.getLogger(KafkaAuditEventPublisher.class);

    @Override
    public void publishAuditEvent(AuditEvent event) {
        // here this method should be posting in a real Kafka topic
        log.info("[FAKE QUEUE] Publishing audit event: {}", event);
        
    }
}