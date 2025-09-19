package net.mycompany.commerce.purchase.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

@Entity
@Table(name = "currency_tb")
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

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Currency() {}
    public Currency(Long id, String code, String name, String country) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.country = country;
    }
}