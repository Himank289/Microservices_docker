package vw.microservice.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods=false)
class RestTemplateConfiguration{
	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder ) {
		return builder.build();
	}
}

@RestController
public class CurrencyConversionController {

	@Autowired
	private RestTemplate restTemplate;
	Logger logger = LoggerFactory.getLogger(CurrencyConversion.class);
	
	@Autowired
	CurrencyExchangeProxy proxy;
	
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversion(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		HashMap<String,String> uriVariables=new HashMap<>();
		uriVariables.put("from",from);
		uriVariables.put("to",to);
		logger.info("form"+from+"to"+to+"quantity"+quantity);
		
		
		
		ResponseEntity<CurrencyConversion> responseEntity=restTemplate.getForEntity("http://localhost:8000/currency-exchange/from/USD/to/INR",CurrencyConversion.class,uriVariables);
		
		CurrencyConversion currencyConversion=responseEntity.getBody();
		
		//it will populate the all properties like totalcalculated amt,id,environment
//		return new CurrencyConversion(currencyConversion.getId(),from,to,quantity,currencyConversion.getConversionMultiple(),quantity.multiply(currencyConversion.getConversionMultiple()),
//				currencyConversion.getEnvironment()+" "+"rest Template");
		currencyConversion.setQuantity(quantity);
		
		BigDecimal conversionMultiple=currencyConversion.getConversionMultiple();
		BigDecimal totalCalculateAmount=quantity.multiply(conversionMultiple);
		
		currencyConversion.setTotalCalculatedAmount(totalCalculateAmount);
		logger.info("after setting total amount"+currencyConversion);
		return currencyConversion;
	}
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversionFiegn(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		HashMap<String,String> uriVariables=new HashMap<>();
		uriVariables.put("from",from);
		uriVariables.put("to",to);
		logger.info("currencyconversionfeign:form"+from+"to"+to+"quantity"+quantity);
		
		
		CurrencyConversion currencyConversion=proxy.retrieveExchangeValue(from, to);
		
		//it will populate the all properties like totalcalculated amt,id,environment
//		return new CurrencyConversion(currencyConversion.getId(),from,to,quantity,currencyConversion.getConversionMultiple(),quantity.multiply(currencyConversion.getConversionMultiple()),
//				currencyConversion.getEnvironment()+" "+"rest Template");
		currencyConversion.setQuantity(quantity);
		
		BigDecimal conversionMultiple=currencyConversion.getConversionMultiple();
		BigDecimal totalCalculateAmount=quantity.multiply(conversionMultiple);
		
		currencyConversion.setTotalCalculatedAmount(totalCalculateAmount);
		logger.info("after setting total amount ,currencyconversionfeign"+currencyConversion);
		return currencyConversion;
	}
	
	
	
	
}
