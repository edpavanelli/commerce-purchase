package net.mycompany.commerce.mock;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;

@Service
public class QueueManagerServiceMock {
	
	private static final Logger log = LoggerFactory.getLogger(QueueManagerServiceMock.class);
	
	private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Map<String, StorePurchaseResponseDto> responses = new ConcurrentHashMap<>();

	public String enqueue(StorePurchaseRequestDto request) {
		
		log.debug("Enqueuing purchase request: {}", request);
        queue.offer(new Message(request));
        log.debug("Purchase request enqueued with statusCode: {}", "200");
        return "200";
    }

    public Message take() throws InterruptedException {
    	log.debug("Waiting to take a message from the queue...");
        return queue.take();
    }

    public void putResponse(StorePurchaseResponseDto response) {
    	log.debug("Storing response for transactionId {}: {}", response.getTransactionId(), response);
        responses.put(response.getTransactionId(), response);
    }

    public StorePurchaseResponseDto getResponse(String transactionId) {
    	log.debug("Retrieving response for transactionId: {}", transactionId);
        return responses.get(transactionId);
    }

    public record Message(StorePurchaseRequestDto request) {}
}