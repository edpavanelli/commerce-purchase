package net.mycompany.commerce.mock;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponse;

@Service
public class QueueManagerServiceMock {
	
	private static final Logger log = LoggerFactory.getLogger(QueueManagerServiceMock.class);
	
	private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Map<String, StorePurchaseResponse> responses = new ConcurrentHashMap<>();

	public String enqueue(StorePurchaseRequest request) {
		
		log.debug("Enqueuing purchase request: {}", request);
        //String transactionId = Utils.getNanoId();
        
        queue.offer(new Message(request));
        
        log.debug("Purchase request enqueued with statusCode: {}", "200");
        return "200";
    }

    public Message take() throws InterruptedException {
    	log.debug("Waiting to take a message from the queue...");
        return queue.take();
    }

    public void putResponse(StorePurchaseResponse response) {
    	log.debug("Storing response for transactionId {}: {}", response.getTransactionId(), response);
        responses.put(response.getTransactionId(), response);
    }

    public StorePurchaseResponse getResponse(String transactionId) {
    	log.debug("Retrieving response for transactionId: {}", transactionId);
        return responses.get(transactionId);
    }

    public record Message(StorePurchaseRequest request) {}
}