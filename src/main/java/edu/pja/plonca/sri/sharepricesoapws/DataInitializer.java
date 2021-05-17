package edu.pja.plonca.sri.sharepricesoapws;

import edu.pja.plonca.sri.sharepricesoapws.model.SharePrice;
import edu.pja.plonca.sri.sharepricesoapws.repo.SharePriceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    private final SharePriceRepository sharePriceRepository;

    // TODO: dodać więcej przypadków
    public void initData() {
        SharePrice sp1 = SharePrice.builder()
                .ticker("CCC")
                .currency("PLN")
                .price(121.04)
                .measurementDate(LocalDateTime.of(2021, 5, 15, 12, 31, 5))
                .stockExchange("WSE")
                .index("WIG20")
                .build();
        SharePrice sp2 = SharePrice.builder()
                .ticker("AAPL")
                .currency("USD")
                .price(127.45)
                .measurementDate(LocalDateTime.of(2021, 5, 15, 12, 31, 5))
                .stockExchange("NYSE")
                .index("NASDAQ")
                .build();
        SharePrice sp3 = SharePrice.builder()
                .ticker("CCC")
                .currency("PLN")
                .price(190.14)
                .measurementDate(LocalDateTime.of(2020, 5, 1, 12, 31, 5))
                .stockExchange("WSE")
                .index("WIG20")
                .build();
        SharePrice sp4 = SharePrice.builder()
                .ticker("CCC")
                .currency("PLN")
                .price(70.14)
                .measurementDate(LocalDateTime.of(2019, 5, 1, 12, 31, 5))
                .stockExchange("WSE")
                .index("WIG20")
                .build();
        sharePriceRepository.saveAll(Arrays.asList(sp1, sp2, sp3, sp4));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initData();
    }

}