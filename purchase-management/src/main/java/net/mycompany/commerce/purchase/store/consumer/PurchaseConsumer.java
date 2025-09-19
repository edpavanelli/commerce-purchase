package net.mycompany.commerce.purchase.store.consumer;

import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;
import net.mycompany.commerce.purchase.store.queue.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.store.service.PurchaseService;

@Service
@Validated
public class PurchaseConsumer {

    private final QueueManagerServiceMock queueManager;
    private final PurchaseService purchaseService;
    private final PurchaseConsumer self;

    public PurchaseConsumer(QueueManagerServiceMock queueManager, PurchaseService purchaseService, PurchaseConsumer self) {
        this.queueManager = queueManager;
        this.purchaseService = purchaseService;
        this.self = self;
  
    }
    
    @PostConstruct
    public void init() {
        // chama via proxy para @Async funcionar
        self.consumeMessages();
    }

    @Async
    public void consumeMessages() {
    	int i = 0;
        while (true) {
        	i++;
	    	if (i  == Integer.MIN_VALUE) { 
	    		break;
	    	}
	    	
            try {
                QueueManagerServiceMock.Message msg = queueManager.take();
                // chama método validado para processar
                processMessage(msg.transactionId(), msg.request());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void processMessage(UUID transactionId, @Valid StorePurchaseRequest request) {
        // salva a compra
        purchaseService.newPurchase(request);

        // cria e armazena resposta
        StorePurchaseResponse resp = new StorePurchaseResponse();
        resp.setTransactionId(transactionId);
        queueManager.putResponse(transactionId, resp);
    }
}
