package net.mycompany.commerce.purchase.application.store.mapper;

import org.springframework.stereotype.Component;

import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseRequestDto;
import net.mycompany.commerce.purchase.application.store.dto.StorePurchaseResponseDto;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.port.TransactionIdGeneratorPort;
import net.mycompany.commerce.purchase.domain.valueobject.TransactionId;

@Component
public class PurchaseTransactionMapper {
	
	private final TransactionIdGeneratorPort idGenerator;
	
	public PurchaseTransactionMapper(TransactionIdGeneratorPort idGenerator) {	
		this.idGenerator = idGenerator;
	}
	
    public PurchaseTransaction toDomain(StorePurchaseRequestDto dto) {
    	return PurchaseTransaction.builder()
				.transactionId(new TransactionId(idGenerator.nextId()))
				.amount(dto.getAmount())
				.purchaseDate(dto.getPurchaseDate())
				.description(dto.getDescription())
				.build();
    }

    public StorePurchaseResponseDto toDto(PurchaseTransaction purchaseTransaction) {
       		return StorePurchaseResponseDto.builder()
				.transactionId(purchaseTransaction.getTransactionId().getValue())
				.build();
    }
}
