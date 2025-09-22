package net.mycompany.commerce.purchase.application.port.out;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mycompany.commerce.purchase.domain.valueobject.TransactionId;
import net.mycompany.commerce.purchase.infrastructure.config.audit.AuditOperation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {
    private TransactionId transactionId;
    private AuditOperation operation;
    private String changedBy; // should be a SystemUser entity
    private LocalDateTime changedDate;
}