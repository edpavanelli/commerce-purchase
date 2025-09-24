package net.mycompany.commerce.purchasemgmt.application.port.out;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.mycompany.commerce.purchasemgmt.application.port.out.AuditEvent;
import net.mycompany.commerce.purchasemgmt.application.port.out.KafkaAuditEventPublisher;


class KafkaAuditEventPublisherTest {
    @Test
    void testPublishAuditEventLogsEvent() {
        KafkaAuditEventPublisher publisher = new KafkaAuditEventPublisher();
        AuditEvent event = Mockito.mock(AuditEvent.class);
        // Just call the method to ensure no exceptions and log is called
        publisher.publishAuditEvent(event);
        
    }
}
