package net.mycompany.commerce.purchasemgmt.domain.port;

import org.junit.jupiter.api.Test;

import net.mycompany.commerce.purchasemgmt.domain.port.ExchangeRateProviderPort;
import net.mycompany.commerce.purchasemgmt.domain.valueobject.ExchangeRate;
import net.mycompany.commerce.purchasemgmt.infrastructure.integration.treasury.dto.TreasuryExchangeRateFilterDto;
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
