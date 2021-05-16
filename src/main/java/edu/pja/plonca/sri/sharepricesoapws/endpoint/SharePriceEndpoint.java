package edu.pja.plonca.sri.sharepricesoapws.endpoint;

import edu.pja.plonca.sri.sharepricesoapws.config.SoapWSConfig;
import edu.pja.plonca.sri.sharepricesoapws.model.SharePrice;
import edu.pja.plonca.sri.sharepricesoapws.repo.SharePriceRepository;
import edu.pja.plonca.sri.sharepricesoapws.shareprices.*;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
@RequiredArgsConstructor
public class SharePriceEndpoint {

    private final SharePriceRepository sharePriceRepository;

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "getSharePriceByIdRequest")
    @ResponsePayload
    public GetSharePriceByIdResponse getSharePriceById(@RequestPayload GetSharePriceByIdRequest req) {
        Long sharePriceId = req.getSharePriceId().longValue();
        Optional<SharePrice> sp = sharePriceRepository.findById(sharePriceId);
        GetSharePriceByIdResponse res = new GetSharePriceByIdResponse();
        res.setSharePrice(convertToDto(sp.orElse(null)));
        return res;
    }

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "addSharePriceRequest")
    @ResponsePayload
    public AddSharePriceResponse addSharePrice(@RequestPayload AddSharePriceRequest req) {
        SharePriceDto spDto = req.getSharePrice();
        SharePrice sp = convertToEntity(spDto);
        sharePriceRepository.save(sp);
        AddSharePriceResponse response = new AddSharePriceResponse();
        response.setSharePriceId(new BigDecimal(sp.getId()));
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "addBulkRequest")
    @ResponsePayload
    public AddBulkResponse addBulk(@RequestPayload AddBulkRequest req) {
        for (SharePriceDto spDto : req.getSharePrice()) {
            SharePrice sp = convertToEntity(spDto);
            sharePriceRepository.save(sp);
        }
        AddBulkResponse response = new AddBulkResponse();
        response.setIsSuccessful(true);
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "getLatestByTickerRequest")
    @ResponsePayload
    public GetLatestByTickerResponse getLatestByTicker(@RequestPayload GetLatestByTickerRequest req) {
        String ticker = req.getTicker();
        Optional<SharePrice> sp = sharePriceRepository.findFirstByTickerOrderByMeasurementDateDesc(ticker);
        GetLatestByTickerResponse res = new GetLatestByTickerResponse();
        res.setSharePrice(convertToDto(sp.orElse(null)));
        return res;
    }

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "updateSharePriceRequest")
    @ResponsePayload
    public UpdateSharePriceResponse updateSharePrice(@RequestPayload UpdateSharePriceRequest req) {
        SharePriceDto spDto = req.getSharePrice();
        SharePrice spNew = convertToEntity(spDto);

        Long sharePriceId = req.getSharePrice().getId().longValue();
        Optional<SharePrice> spOld = sharePriceRepository.findById(sharePriceId);

        UpdateSharePriceResponse response = new UpdateSharePriceResponse();
        if (spOld.isPresent()) {
            SharePrice spOldExtracted = spOld.get();
            spOldExtracted.setTicker(spNew.getTicker());
            spOldExtracted.setCurrency(spNew.getCurrency());
            spOldExtracted.setPrice(spNew.getPrice());
            spOldExtracted.setMeasurementDate(spNew.getMeasurementDate());
            spOldExtracted.setStockExchange(spNew.getStockExchange());
            spOldExtracted.setIndex(spNew.getIndex());
            sharePriceRepository.save(spOldExtracted);
            response.setIsSuccessful(true);
        } else {
            response.setIsSuccessful(false);
        }
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "getSharePricesByStockExchangeRequest")
    @ResponsePayload
    public GetSharePricesByStockExchangeResponse getSharePricesByStockExchange(@RequestPayload GetSharePricesByStockExchangeRequest req) {
        List<SharePrice> spList = sharePriceRepository.findAllByStockExchange(req.getStockExchange());
        List<SharePriceDto> dtoList = spList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        GetSharePricesByStockExchangeResponse res = new GetSharePricesByStockExchangeResponse();
        res.getSharePrices().addAll(dtoList);
        return res;
    }

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "getSharePricesByTickerAndBetweenMeasurementDateRequest")
    @ResponsePayload
    public GetSharePricesByTickerAndBetweenMeasurementDateResponse getSharePricesByTickerAndBetweenMeasurementDate
            (@RequestPayload GetSharePricesByTickerAndBetweenMeasurementDateRequest req) {

        LocalDateTime startDateTime = LocalDateTime.parse(req.getStartDateTime().toString());
        LocalDateTime endDateTime = LocalDateTime.parse(req.getEndDateTime().toString());
        String ticker = req.getTicker();

        List<SharePrice> spList = sharePriceRepository.
                findAllByTickerAndMeasurementDateBetweenOrderByMeasurementDateDesc(ticker, startDateTime, endDateTime);
        List<SharePriceDto> dtoList = spList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        GetSharePricesByTickerAndBetweenMeasurementDateResponse res = new GetSharePricesByTickerAndBetweenMeasurementDateResponse();
        res.getSharePrices().addAll(dtoList);
        return res;
    }

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "getMaxSharePriceByTickerAndBetweenMeasurementDateRequest")
    @ResponsePayload
    public GetMaxSharePriceByTickerAndBetweenMeasurementDateResponse getMaxSharePriceByTickerAndBetweenMeasurementDate
            (@RequestPayload GetMaxSharePriceByTickerAndBetweenMeasurementDateRequest req) {
        LocalDateTime startDateTime = LocalDateTime.parse(req.getStartDateTime().toString());
        LocalDateTime endDateTime = LocalDateTime.parse(req.getEndDateTime().toString());
        String ticker = req.getTicker();

        Optional<SharePrice> sp = sharePriceRepository.
                findFirstByTickerAndMeasurementDateBetweenOrderByPriceDesc(ticker, startDateTime, endDateTime);
        GetMaxSharePriceByTickerAndBetweenMeasurementDateResponse res = new GetMaxSharePriceByTickerAndBetweenMeasurementDateResponse();
        res.setSharePrice(convertToDto(sp.orElse(null)));
        return res;
    }

    private SharePriceDto convertToDto(SharePrice sp) {
        if (sp == null) {
            return null;
        }
        try {
            SharePriceDto dto = new SharePriceDto();
            dto.setId(new BigDecimal(sp.getId()));
            dto.setTicker(sp.getTicker());
            dto.setCurrency(sp.getCurrency());
            dto.setPrice(sp.getPrice());
            XMLGregorianCalendar measurementDate = null;
            measurementDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(sp.getMeasurementDate().toString());
            dto.setMeasurementDate(measurementDate);
            dto.setStockExchange(sp.getStockExchange());
            dto.setIndex(sp.getIndex());
            return dto;
        } catch (DatatypeConfigurationException datatypeConfigurationException) {
            datatypeConfigurationException.printStackTrace();
            return null;
        }
    }

    private SharePrice convertToEntity(SharePriceDto dto) {
        return SharePrice.builder()
                .id(dto.getId() != null ? dto.getId().longValue() : null)
                .ticker(dto.getTicker())
                .currency(dto.getCurrency())
                .price(dto.getPrice())
                .measurementDate(LocalDateTime.parse(dto.getMeasurementDate().toString()))
                .stockExchange(dto.getStockExchange())
                .index(dto.getIndex())
                .build();
    }
}
