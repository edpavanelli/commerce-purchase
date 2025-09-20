package net.mycompany.commerce.purchase.store.dto;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchase.model.PurchaseTransaction;

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

    public StorePurchaseResponse toResponseDto(PurchaseTransaction purchaseTransaction) {
       		return StorePurchaseResponse.builder()
				.transactionId(purchaseTransaction.getTransactionId())
				.build();
    }
}
