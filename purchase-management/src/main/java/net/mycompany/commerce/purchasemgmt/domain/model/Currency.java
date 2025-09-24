package net.mycompany.commerce.purchasemgmt.domain.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "currency_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Currency", description = "Represents a currency used in purchase transactions.")
public class Currency implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the currency.", example = "1")
    private Long id;

    @Column(length = 3, nullable = false)
    @Size(min = 3, max = 3)
    @Schema(description = "ISO 4217 currency code.", example = "USD")
    private String code;

    @Column(length = 50, nullable = false)
    @Size(max = 50)
    @Schema(description = "Name of the currency.", example = "US Dollar")
    private String name;

    @Column(length = 50, nullable = false)
    @Size(max = 50)
    @NotBlank
    @Schema(description = "Country associated with the currency.", example = "United States")
    private String country;
}