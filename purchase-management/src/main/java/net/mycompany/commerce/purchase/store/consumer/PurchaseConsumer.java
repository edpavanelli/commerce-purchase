package net.mycompany.commerce.purchase.store.consumer;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import net.mycompany.commerce.purchase.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.store.domain.Purchase;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;

@Service
@Validated
public class PurchaseConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(PurchaseConsumer.class);

    private final QueueManagerServiceMock queueManager;
    private final Purchase purchaseService;
    private final PurchaseConsumer self;

    public PurchaseConsumer(QueueManagerServiceMock queueManager, Purchase purchaseService, PurchaseConsumer self) {
        this.queueManager = queueManager;
        this.purchaseService = purchaseService;
        this.self = self;
  
    }
    
    @PostConstruct
    public void init() {
        // chama via proxy para @Async funcionar
    	log.info("Iniciando consumidor de mensagens de compra...");
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
            	
            	log.debug("Aguardando mensagem de compra...");
                QueueManagerServiceMock.Message msg = queueManager.take();
                
                
                log.debug("Mensagem de compra recebida: {}", msg);
                processMessage(msg.transactionId(), msg.request());
                
                log.debug("Mensagem de compra processada: {}", msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void processMessage(UUID transactionId, @Valid StorePurchaseRequest request) {
        // salva a compra
    	
    	log.debug("Processando compra: {}", request);
        purchaseService.newPurchase(request, transactionId);
        
        // cria e armazena resposta
        log.debug("Criando resposta para a compra: {}", request);
        StorePurchaseResponse resp = new StorePurchaseResponse();
        resp.setTransactionId(transactionId);
        
        log.debug("Armazenando resposta para a compra: {}", resp);
        queueManager.putResponse(transactionId, resp);
    }
}
