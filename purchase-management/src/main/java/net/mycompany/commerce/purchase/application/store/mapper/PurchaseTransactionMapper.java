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
    	return new PurchaseTransaction(
    			new TransactionId(idGenerator.nextId()),
    			dto.getAmount(),
    			null, // Currency will be set elsewhere
    			dto.getPurchaseDate(),
    			dto.getDescription());
    }

    public StorePurchaseResponseDto toDto(PurchaseTransaction purchaseTransaction) {
       	return StorePurchaseResponseDto.builder()
				.transactionId(purchaseTransaction.getTransactionId().getValue())
				.build();
    }
}