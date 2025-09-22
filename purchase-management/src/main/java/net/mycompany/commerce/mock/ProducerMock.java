package net.mycompany.commerce.mock;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;

@RestController
@RequestMapping("/commerce/purchase/v1")
public class ProducerMock {

	private static final Logger log = LoggerFactory.getLogger(ProducerMock.class);
   
	private final QueueManagerServiceMock queueManager;

    public ProducerMock(QueueManagerServiceMock queueManager) {
        this.queueManager = queueManager;
        
    }

   
    @PostMapping(path = "/store" , consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> enqueuePurchase(@RequestBody StorePurchaseRequestDto purchase) {
    	log.debug("Received purchase request: {}", purchase);
        String statusCode = queueManager.enqueue(purchase);
        log.debug("Enqueued purchase with status code: {}", statusCode);
        return ResponseEntity.accepted().body(Map.of("statusCode", statusCode));
    }

    
    @GetMapping("/{transactionId}")
    public ResponseEntity<StorePurchaseResponseDto> getResponse(@PathVariable("transactionId") String transactionId) {
    	log.debug("Checking response for transactionId: {}", transactionId);
        StorePurchaseResponseDto result = queueManager.getResponse(transactionId);
        log.debug("Response for transactionId {}: {}", transactionId, result);
        if (result == null) {
        	log.debug("Response for transactionId {} is still processing", transactionId);
            return ResponseEntity.status(HttpStatus.PROCESSING).build();
        }
        log.debug("Response for transactionId {} is ready", transactionId);
        return ResponseEntity.ok(result);
    }
}