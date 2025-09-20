package net.mycompany.commerce.purchase.audit;

import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.infrastructure.config.audit.AuditOperation;
import net.mycompany.commerce.purchase.infrastructure.config.audit.PurchaseTransactionSubject;
import net.mycompany.commerce.purchase.infrastructure.config.audit.TransactionObserver;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PurchaseTransactionSubjectTest {
    // Removed @Autowired and SpringBootTest, instantiate subject directly

    @Test
    void testNotifyObserversOnPurchaseAsync() throws InterruptedException {
        PurchaseTransactionSubject subject = new PurchaseTransactionSubject(); // fresh instance
        TransactionObserver observer = mock(TransactionObserver.class);
        subject.addObserver(observer);
        PurchaseTransaction transaction = mock(PurchaseTransaction.class);
        AuditOperation operation = AuditOperation.CREATE;

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> { latch.countDown(); return null; })
            .when(observer).onPurchaseTransactionChanged(transaction, operation);

        subject.notifyObserversOnPurchaseAsync(transaction, operation);
        boolean called = latch.await(5, TimeUnit.SECONDS);
        verify(observer, atLeastOnce()).onPurchaseTransactionChanged(transaction, operation);
        assertTrue(called, "Observer should be called asynchronously");
    }
}