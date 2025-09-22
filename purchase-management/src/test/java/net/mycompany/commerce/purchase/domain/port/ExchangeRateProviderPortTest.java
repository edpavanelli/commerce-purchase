package net.mycompany.commerce.purchase.domain.port;

import net.mycompany.commerce.purchase.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
import net.mycompany.commerce.purchase.domain.valueobject.ExchangeRate;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateProviderPortTest {
    @Test
    void testMockExchangeRateProviderPort() {
        ExchangeRateProviderPort port = filter -> Mono.just(List.of(ExchangeRate.builder().build()));
        TreasuryExchangeRateFilterDto filter = TreasuryExchangeRateFilterDto.builder().build();
        List<ExchangeRate> result = port.getTreasuryExchangeRateFromRestClient(filter).block();
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
