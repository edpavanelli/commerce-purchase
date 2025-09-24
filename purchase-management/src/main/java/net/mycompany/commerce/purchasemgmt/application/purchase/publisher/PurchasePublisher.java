package net.mycompany.commerce.purchasemgmt.application.purchase.publisher;

import org.springframework.stereotype.Service;

import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import net.mycompany.commerce.mock.QueueManagerServiceMock;
import net.mycompany.commerce.purchasemgmt.application.purchase.dto.StorePurchaseResponseDto;


@Service
public class PurchasePublisher {

	private final QueueManagerServiceMock queueManager;
	
	public PurchasePublisher(QueueManagerServiceMock queueManager) {
		this.queueManager = queueManager;
	}
	
	@AsyncPublisher(
	        operation = @AsyncOperation(
	            channelName = "purchase-response-queue",
	            description = "Queue of purchase responses",
	            payloadType = StorePurchaseResponseDto.class
	        )
	    )
	public StorePurchaseResponseDto publishResponse(StorePurchaseResponseDto response) {
        queueManager.putResponse(response);
        return response;
    }
}
