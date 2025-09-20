package net.mycompany.commerce.purchase.store.consumer;

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

import net.mycompany.commerce.purchase.model.PurchaseTransaction;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import net.mycompany.commerce.purchase.domain.Purchase;
import net.mycompany.commerce.purchase.mapper.PurchaseTransactionMapper;
import net.mycompany.commerce.purchase.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;

@Service
@Validated
public class PurchaseConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(PurchaseConsumer.class);

    private final QueueManagerServiceMock queueManager;
    private final Purchase purchaseService;
    private ApplicationContext applicationContext;
    private final PurchaseTransactionMapper purchaseTransactionMapper;

    public PurchaseConsumer(QueueManagerServiceMock queueManager, 
    		Purchase purchaseService, 
    		ApplicationContext applicationContext,
    		PurchaseTransactionMapper purchaseTransactionMapper) {
        this.queueManager = queueManager;
        this.purchaseService = purchaseService;
        this.applicationContext = applicationContext;
        this.purchaseTransactionMapper = purchaseTransactionMapper;
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
    		
    	PurchaseTransaction purchaseTransaction = purchaseTransactionMapper.toDomain(request);
    	
    	purchaseTransaction = purchaseService.storePurchase(purchaseTransaction);
        
        // cria e armazena resposta
        log.debug("Criando resposta para a compra: {}", request);
        StorePurchaseResponse resp = purchaseTransactionMapper.toResponseDto(purchaseTransaction);
        
        log.debug("Armazenando resposta para a compra: {}", resp);
        queueManager.putResponse(resp);
    }
}