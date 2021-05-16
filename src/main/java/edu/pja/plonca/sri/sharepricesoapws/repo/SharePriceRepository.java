package edu.pja.plonca.sri.sharepricesoapws.repo;

import edu.pja.plonca.sri.sharepricesoapws.model.SharePrice;
import org.apache.tomcat.jni.Local;
import org.springframework.data.repository.CrudRepository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SharePriceRepository extends CrudRepository<SharePrice, Long> {
    List<SharePrice> findAll();

    Optional<SharePrice> findFirstByTickerOrderByMeasurementDateDesc(String ticker);

    List<SharePrice> findAllByStockExchange(String stockExchange);

    List<SharePrice> findAllByTickerAndMeasurementDateBetweenOrderByMeasurementDateDesc(
            String ticker, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Optional<SharePrice> findFirstByTickerAndMeasurementDateBetweenOrderByPriceDesc(
            String ticker, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
