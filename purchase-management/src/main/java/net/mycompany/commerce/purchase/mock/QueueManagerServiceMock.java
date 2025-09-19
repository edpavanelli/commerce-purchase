package net.mycompany.commerce.purchase.mock;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;

@Service
public class QueueManagerServiceMock {
	
	private static final Logger log = LoggerFactory.getLogger(QueueManagerServiceMock.class);
	
	private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Map<UUID, StorePurchaseResponse> responses = new ConcurrentHashMap<>();

	public UUID enqueue(StorePurchaseRequest request) {
		
		log.debug("Enqueuing purchase request: {}", request);
        UUID transactionId = UUID.randomUUID();
        
        log.debug("Generated transactionId: {}", transactionId);
        queue.offer(new Message(transactionId, request));
        
        log.debug("Purchase request enqueued with transactionId: {}", transactionId);
        return transactionId;
    }

    public Message take() throws InterruptedException {
    	log.debug("Waiting to take a message from the queue...");
        return queue.take();
    }

    public void putResponse(UUID transactionId, StorePurchaseResponse response) {
    	log.debug("Storing response for transactionId {}: {}", transactionId, response);
        responses.put(transactionId, response);
    }

    public StorePurchaseResponse getResponse(UUID transactionId) {
    	log.debug("Retrieving response for transactionId: {}", transactionId);
        return responses.get(transactionId);
    }

    public record Message(UUID transactionId, StorePurchaseRequest request) {}
}