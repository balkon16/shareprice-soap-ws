package edu.pja.plonca.sri.sharepricesoapws.endpoint;

import edu.pja.plonca.sri.sharepricesoapws.config.SoapWSConfig;
import edu.pja.plonca.sri.sharepricesoapws.model.SharePrice;
import edu.pja.plonca.sri.sharepricesoapws.repo.SharePriceRepository;
import edu.pja.plonca.sri.sharepricesoapws.shareprices.GetSharePriceByIdRequest;
import edu.pja.plonca.sri.sharepricesoapws.shareprices.GetSharePriceByIdResponse;
import edu.pja.plonca.sri.sharepricesoapws.shareprices.SharePriceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Endpoint
@RequiredArgsConstructor
public class SharePriceEndpoint {

    private final SharePriceRepository sharePriceRepository;

//    @PayloadRoot(namespace = SoapWSConfig.EMPLOYEE_NAMESPACE, localPart = "getEmployeeByIdRequest")
//    @ResponsePayload
//    public GetEmployeeByIdResponse getEmployeeById(@RequestPayload GetEmployeeByIdRequest req){
//        Long employeeId = req.getEmployeeId().longValue();
//        Optional<Employee> emp = employeeRepository.findById(employeeId);
//        GetEmployeeByIdResponse res = new GetEmployeeByIdResponse();
//        res.setEmployee(convertToDto(emp.orElse(null)));
//        return res;
//    }

    @PayloadRoot(namespace = SoapWSConfig.SHARE_PRICE_NAMESPACE, localPart = "getSharePriceByIdRequest")
    @ResponsePayload
    public GetSharePriceByIdResponse getSharePriceById(@RequestPayload GetSharePriceByIdRequest req){
        Long sharePriceId = req.getSharePriceId().longValue();
        Optional<SharePrice> sp = sharePriceRepository.findById(sharePriceId);
        GetSharePriceByIdResponse res = new GetSharePriceByIdResponse();
        res.setSharePrice(convertToDto(sp.orElse(null)));
        return res;
    }

    private SharePriceDto convertToDto(SharePrice sp) {
        if (sp == null){
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
