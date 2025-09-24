package net.mycompany.commerce.purchasemgmt.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.mycompany.commerce.purchasemgmt.application.port.out.AuditEvent;
import net.mycompany.commerce.purchasemgmt.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.audit.AuditOperation;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.audit.PurchaseTransactionSubject;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.audit.TransactionObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PurchaseTransactionSubjectTest {
    

    @Test
    void testNotifyObserversOnPurchaseAsync() throws InterruptedException {
        PurchaseTransactionSubject subject = new PurchaseTransactionSubject(); 
        TransactionObserver observer = mock(TransactionObserver.class);
        subject.addObserver(observer);
        PurchaseTransaction transaction = mock(PurchaseTransaction.class);
        AuditOperation operation = AuditOperation.CREATE;
        AuditEvent event = mock(AuditEvent.class);

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> { latch.countDown(); return null; })
            .when(observer).onPurchaseTransactionChanged(event);

        subject.notifyObserversOnPurchaseAsync(event);
        boolean called = latch.await(5, TimeUnit.SECONDS);
        verify(observer, atLeastOnce()).onPurchaseTransactionChanged(event);
        assertTrue(called, "Observer should be called asynchronously");
    }
}