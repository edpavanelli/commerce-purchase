package net.mycompany.commerce.purchase.store.queue.mock;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Service;

import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;

@Service
public class QueueManagerServiceMock {
	
	private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Map<UUID, StorePurchaseResponse> responses = new ConcurrentHashMap<>();

	public UUID enqueue(StorePurchaseRequest request) {
        UUID transactionId = UUID.randomUUID();
        queue.offer(new Message(transactionId, request));
        return transactionId;
    }

    public Message take() throws InterruptedException {
        return queue.take();
    }

    public void putResponse(UUID transactionId, StorePurchaseResponse response) {
        responses.put(transactionId, response);
    }

    public StorePurchaseResponse getResponse(UUID transactionId) {
        return responses.get(transactionId);
    }

    public record Message(UUID transactionId, StorePurchaseRequest request) {}
}