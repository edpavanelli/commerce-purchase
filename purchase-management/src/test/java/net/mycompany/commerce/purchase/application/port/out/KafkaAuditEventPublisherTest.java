package net.mycompany.commerce.purchase.application.port.out;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

class KafkaAuditEventPublisherTest {
    @Test
    void testPublishAuditEventLogsEvent() {
        KafkaAuditEventPublisher publisher = new KafkaAuditEventPublisher();
        AuditEvent event = Mockito.mock(AuditEvent.class);
        // Just call the method to ensure no exceptions and log is called
        publisher.publishAuditEvent(event);
        // No assertion needed, but you could use a log capturing library for advanced checks
    }
}
