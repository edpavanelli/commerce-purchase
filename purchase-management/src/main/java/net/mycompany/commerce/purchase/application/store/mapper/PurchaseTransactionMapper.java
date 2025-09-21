package net.mycompany.commerce.purchase.application.store.mapper;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;

@Component
public class PurchaseTransactionMapper {
    public PurchaseTransaction toDomain(StorePurchaseRequestDto dto) {
    	return PurchaseTransaction.builder()
				.transactionId(null)
				.amount(dto.getAmount())
				.purchaseDate(dto.getPurchaseDate())
				.description(dto.getDescription())
				.build();
    }

    public StorePurchaseResponseDto toDto(PurchaseTransaction purchaseTransaction) {
       		return StorePurchaseResponseDto.builder()
				.transactionId(purchaseTransaction.getTransactionId())
				.build();
    }
}
