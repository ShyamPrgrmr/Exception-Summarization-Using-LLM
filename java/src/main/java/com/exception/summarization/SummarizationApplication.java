package com.exception.summarization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

import org.apache.kafka.common.protocol.types.Field.Str;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.SimpleDateFormat;

/**
 * This class represents the main application for exception summarization.
 * It is responsible for handling HTTP requests and throwing random exceptions.
 */
@SpringBootApplication
@RestController 
public class SummarizationApplication {

	private static String exception_names[] = {"InvalidShippingAddress","InsufficientInventory","InvalidCouponCode","PaymentFailed","ProductNotAvailable","InvalidCreditCardNumber","OrderNotFound","DuplicateEmailAddress","InvalidShippingMethod","CartEmpty","InvalidProductVariant","InsufficientFunds","AccountLocked","InvalidPromoCode","InvalidLoginCredentials","InvalidReturnRequest","OrderCancellationFailed","InvalidSecurityCode","ProductOutOfStock","AccountCreationFailed","InvalidDeliveryAddress","OrderModificationFaile","InvalidPaymentMethod","ProductNotFound","DuplicateOrder","AccountDeletionFailed","InvalidEmailAddress","InvalidOrderStatus","PaymentAuthorizationFailed","InvalidPhoneNumber","InvalidShippingOption","AccountSuspensionFailed","InvalidOrderID","InvalidCategory","AccountReactivationFailed","InvalidProductID","AccountUpdateFailed","InvalidOrderDetails","AccountVerificationFailed","InvalidProductName","AccountLoginFailed","InvalidOrderQuantity","AccountLogoutFailed","InvalidOrderDate","AccountPasswordResetFailed","InvalidOrderTotal","AccountCreationLimitExceeded","InvalidOrderStatusUpdate","AccountDeactivationFailed","InvalidOrderPayment","AccountSuspensionLimitExceeded","InvalidOrderShipping","AccountReactivationLimitExceeded","InvalidOrderCancellation","AccountUpdateLimitExceeded","InvalidOrderModification","AccountVerificationLimitExceeded","InvalidOrderReturn","AccountDeletionLimitExceeded"};

	public static void main(String[] args) {
		SpringApplication.run(SummarizationApplication.class, args);
	}

	private final KafkaProducerService kafkaProducerService;

	/**
	 * Constructor for SummarizationApplication class.
	 * @param kafkaProducerService The KafkaProducerService instance used for sending messages to Kafka.
	 */
	@Autowired
	public SummarizationApplication(KafkaProducerService kafkaProducerService) {
		this.kafkaProducerService = kafkaProducerService;
	}

	/**
	 * Handles the HTTP GET request for throwing a random exception.
	 * @return A message indicating that an exception was thrown and sent to Kafka.
	 */
	@GetMapping("/trowRandomException")
	public String getRandomException() {
		try {
			throw new ExceptionData(exception_names[(int)(Math.random()*exception_names.length)]);
		} catch (Exception e) {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = dateFormat.format(new Date(System.currentTimeMillis()));

			int lineNumber = e.getStackTrace()[0].getLineNumber();
			String fileName = e.getStackTrace()[0].getFileName();
			String methodName = e.getStackTrace()[0].getMethodName();
			String exceptionName = e.getMessage();

			StringBuilder stackTrace = new StringBuilder();
			stackTrace.append(date + "|");
			stackTrace.append(exceptionName + "|");
			stackTrace.append(fileName + "|");
			stackTrace.append(methodName + "|");
			stackTrace.append(lineNumber+"|");
			stackTrace.append(e.getStackTrace());

			if( stackTrace!=null && !stackTrace.isEmpty() && stackTrace.length() > 0 && !stackTrace.toString().equals("") ) {
				kafkaProducerService.sendMessage("exception-topic", stackTrace.toString());
			}
			
		}
		return "Exception was thrown and sent to Kafka!";
	}

	/**
	 * This class represents a custom exception that includes the exception name.
	 */
	private class ExceptionData extends Exception{    
		private String exception_name;    
		public ExceptionData(String exception_name) {
			super(exception_name);
			this.exception_name = exception_name;
		}
	} 

}



