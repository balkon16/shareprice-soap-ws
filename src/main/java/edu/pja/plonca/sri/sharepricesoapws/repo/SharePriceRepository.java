package edu.pja.plonca.sri.sharepricesoapws.repo;

import edu.pja.plonca.sri.sharepricesoapws.model.SharePrice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SharePriceRepository extends CrudRepository<SharePrice, Long> {
    List<SharePrice> findAll();
    Optional<SharePrice> findFirstByTickerOrderByMeasurementDateDesc(String ticker);
}
