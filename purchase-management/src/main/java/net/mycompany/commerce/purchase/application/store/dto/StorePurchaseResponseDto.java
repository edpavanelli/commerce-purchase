package net.mycompany.commerce.purchase.application.store.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorePurchaseResponseDto {
    private String transactionId;

    
}
