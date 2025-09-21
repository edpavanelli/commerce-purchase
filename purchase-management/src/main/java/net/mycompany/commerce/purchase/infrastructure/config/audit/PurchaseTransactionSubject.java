package net.mycompany.commerce.purchase.infrastructure.config.audit;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchase.application.port.out.AuditEvent;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;

import org.springframework.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class PurchaseTransactionSubject {
    private static final Logger log = LoggerFactory.getLogger(PurchaseTransactionSubject.class);
    private final List<TransactionObserver> observers = new CopyOnWriteArrayList<>();

    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    public void notifyObserversOnPurchase(AuditEvent event) {
        for (TransactionObserver observer : observers) {
            observer.onPurchaseTransactionChanged(event);
        }
    }

    //@Async
    public void notifyObserversOnPurchaseAsync(AuditEvent event) {
        try {
            notifyObserversOnPurchase(event);
        } catch (Exception e) {
            log.error("Async audit notification failed", e);
        }
    }
}