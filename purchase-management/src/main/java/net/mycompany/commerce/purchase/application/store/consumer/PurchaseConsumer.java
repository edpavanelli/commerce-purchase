package net.mycompany.commerce.purchase.application.store.consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import io.github.springwolf.core.asyncapi.annotations.AsyncListener;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import jakarta.validation.Valid;
import net.mycompany.commerce.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;
import net.mycompany.commerce.purchase.application.store.service.StorePurchaseService;

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
            	
            	log.debug("Waiting for purchase message...");
                QueueManagerServiceMock.Message msg = queueManager.take();
                
                
                log.debug("Purchase message received: {}", msg);
                storePurchase(msg.request());
                
                log.debug("Purchase message processed: {}", msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    
    @AsyncListener(
            operation = @AsyncOperation(
                channelName = "purchase-request-queue",
                description = "Queue of purchase requests",
                payloadType = StorePurchaseRequestDto.class
            )
        )
    public void storePurchase(@Valid StorePurchaseRequestDto request) {
    	
    	log.debug("Processing purchase: {}", request);
    		
    	purchaseService.storePurchase(request);
        
    }
}