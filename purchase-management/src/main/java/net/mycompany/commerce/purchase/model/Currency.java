package net.mycompany.commerce.purchase.model;

import java.io.Serializable;

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
public class Currency implements Serializable {
	
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3, nullable = false)
    @Size(min = 3, max = 3)
    @NotBlank
    private String code;

    @Column(length = 50, nullable = false)
    @Size(max = 50)
    @NotBlank
    private String name;

    @Column(length = 50, nullable = false)
    @Size(max = 50)
    @NotBlank
    private String country;
    

}