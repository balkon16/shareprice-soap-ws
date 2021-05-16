package edu.pja.plonca.sri.sharepricesoapws.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class SoapWSConfig extends WsConfigurerAdapter {
    public static final String SHARE_PRICE_NAMESPACE = "http://sharepricesoapws.sri.plonca.pja.edu/sharePrices";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext){
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "sharePrices")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema employeesSchema){
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("SharePricesPort");
        wsdl11Definition.setLocationUri("/ws/sharePrices");
        wsdl11Definition.setTargetNamespace(SHARE_PRICE_NAMESPACE);
        wsdl11Definition.setSchema(employeesSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema countriesSchema(){return new SimpleXsdSchema(new ClassPathResource("sharePrices.xsd"));
    }
}