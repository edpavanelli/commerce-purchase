package net.mycompany.commerce.purchase.domain.valueobject;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString	
@Embeddable
public class TransactionId implements Serializable {
    @Column(name = "transaction_id")
    private String value;

    protected TransactionId() {}

    public TransactionId(String value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionId)) return false;
        TransactionId that = (TransactionId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}