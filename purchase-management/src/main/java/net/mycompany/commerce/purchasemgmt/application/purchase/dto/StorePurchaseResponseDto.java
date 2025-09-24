package net.mycompany.commerce.purchasemgmt.application.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "StorePurchaseResponseDto", description = "Response data for storing a purchase transaction.")
public class StorePurchaseResponseDto {
    @Schema(description = "Unique identifier for the stored purchase transaction.", example = "TX987654321")
    private String transactionId;
}