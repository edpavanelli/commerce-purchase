package net.mycompany.commerce.purchase.application.store.consumer;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import net.mycompany.commerce.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponse;
import net.mycompany.commerce.purchase.application.store.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.application.store.service.StorePurchaseService;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;

@Service
@Validated
public class PurchaseConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(PurchaseConsumer.class);

    private final QueueManagerServiceMock queueManager;
    private final StorePurchaseService purchaseService;
    private ApplicationContext applicationContext;
    

    public PurchaseConsumer(QueueManagerServiceMock queueManager, 
    		StorePurchaseService purchaseService, 
    		ApplicationContext applicationContext) {
        this.queueManager = queueManager;
        this.purchaseService = purchaseService;
        this.applicationContext = applicationContext;
        
    }
    

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // chama via proxy para @Async funcionar após contexto estar pronto
    	applicationContext.getBean(PurchaseConsumer.class).consumeMessages();
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
                storePurchase(msg.request());
                
                log.debug("Mensagem de compra processada: {}", msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void storePurchase(@Valid StorePurchaseRequest request) {
    	
    	log.debug("Processando compra: {}", request);
    		
    	StorePurchaseResponse resp = purchaseService.storePurchase(request);
        
        log.debug("Armazenando resposta para a compra: {}", resp);
        queueManager.putResponse(resp);
    }
}