package net.mycompany.commerce.purchase.store.queue.mock;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.store.dto.StorePurchaseResponse;

@RestController
@RequestMapping("/commerce/purchase/v1")
public class ProducerMock {

   
	private final QueueManagerServiceMock queueManager;

    public ProducerMock(QueueManagerServiceMock queueManager) {
        this.queueManager = queueManager;
    }

    // Recebe compra e enfileira
    @PostMapping
    public ResponseEntity<Map<String, String>> enqueuePurchase(@RequestBody @Valid StorePurchaseRequest purchase) {
        UUID transactionId = queueManager.enqueue(purchase);
        return ResponseEntity.accepted().body(Map.of("transactionId", transactionId.toString()));
    }

    // Consulta resultado (reply-to)
    @GetMapping("/{transactionId}")
    public ResponseEntity<StorePurchaseResponse> getResponse(@PathVariable UUID transactionId) {
        StorePurchaseResponse result = queueManager.getResponse(transactionId);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.PROCESSING).build();
        }
        return ResponseEntity.ok(result);
    }
}
