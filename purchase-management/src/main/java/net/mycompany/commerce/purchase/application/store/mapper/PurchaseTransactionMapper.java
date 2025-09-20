package net.mycompany.commerce.purchase.application.store.mapper;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequest;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponse;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;

@Component
public class PurchaseTransactionMapper {
    public PurchaseTransaction toDomain(StorePurchaseRequest dto) {
    	return PurchaseTransaction.builder()
				.transactionId(null)
				.amount(dto.getAmount())
				.purchaseDate(dto.getPurchaseDate())
				.description(dto.getDescription())
				.build();
    }

    public StorePurchaseResponse toDto(PurchaseTransaction purchaseTransaction) {
       		return StorePurchaseResponse.builder()
				.transactionId(purchaseTransaction.getTransactionId())
				.build();
    }
}
