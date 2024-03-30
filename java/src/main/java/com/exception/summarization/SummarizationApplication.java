package com.exception.summarization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.beans.factory.annotation.Autowired;


@SpringBootApplication
@RestController 
public class SummarizationApplication {

	private static String exception_names[] = {"InvalidShippingAddress","InsufficientInventory","InvalidCouponCode","PaymentFailed","ProductNotAvailable","InvalidCreditCardNumber","OrderNotFound","DuplicateEmailAddress","InvalidShippingMethod","CartEmpty","InvalidProductVariant","InsufficientFunds","AccountLocked","InvalidPromoCode","InvalidLoginCredentials","InvalidReturnRequest","OrderCancellationFailed","InvalidSecurityCode","ProductOutOfStock","AccountCreationFailed","InvalidDeliveryAddress","OrderModificationFaile","InvalidPaymentMethod","ProductNotFound","DuplicateOrder","AccountDeletionFailed","InvalidEmailAddress","InvalidOrderStatus","PaymentAuthorizationFailed","InvalidPhoneNumber","InvalidShippingOption","AccountSuspensionFailed","InvalidOrderID","InvalidCategory","AccountReactivationFailed","InvalidProductID","AccountUpdateFailed","InvalidOrderDetails","AccountVerificationFailed","InvalidProductName","AccountLoginFailed","InvalidOrderQuantity","AccountLogoutFailed","InvalidOrderDate","AccountPasswordResetFailed","InvalidOrderTotal","AccountCreationLimitExceeded","InvalidOrderStatusUpdate","AccountDeactivationFailed","InvalidOrderPayment","AccountSuspensionLimitExceeded","InvalidOrderShipping","AccountReactivationLimitExceeded","InvalidOrderCancellation","AccountUpdateLimitExceeded","InvalidOrderModification","AccountVerificationLimitExceeded","InvalidOrderReturn","AccountDeletionLimitExceeded"};

	public static void main(String[] args) {
		SpringApplication.run(SummarizationApplication.class, args);
	}

	private final KafkaProducerService kafkaProducerService;

    @Autowired
    public SummarizationApplication(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }


	@GetMapping("/trowRandomException")
	public String getRandomException() {
		try {
			throw new ExceptionData(exception_names[(int)(Math.random()*exception_names.length)]);
		} catch (Exception e) {


			int lineNumber = e.getStackTrace()[0].getLineNumber();
			String fileName = e.getStackTrace()[0].getFileName();
			String methodName = e.getStackTrace()[0].getMethodName();
			String exceptionName = e.getClass().getName();

			StringBuilder stackTrace = new StringBuilder();
			stackTrace.append("Exception: " + exceptionName + "|");
			stackTrace.append("File Name: " + fileName + "|");
			stackTrace.append("Method Name: " + methodName + "|");
			stackTrace.append("Line Number: " + lineNumber);

			if( stackTrace!=null && !stackTrace.isEmpty() && stackTrace.length() > 0 && !stackTrace.toString().equals("") ) {
				kafkaProducerService.sendMessage("exception-topic", stackTrace.toString());
			}
			
		}
		return "Exception was trown and sent to Kafka!";
	}


	private class ExceptionData extends Exception{		
		public ExceptionData(String exception_name) {
			super(exception_name);
		}
	} 

}



