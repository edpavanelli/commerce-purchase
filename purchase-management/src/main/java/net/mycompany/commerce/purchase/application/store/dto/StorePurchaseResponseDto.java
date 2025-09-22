package net.mycompany.commerce.purchase.application.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorePurchaseResponseDto {
    private String transactionId;
}