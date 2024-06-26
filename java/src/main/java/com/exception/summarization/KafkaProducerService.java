package com.exception.summarization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service class for sending messages to Kafka topics.
 */
@Service
public class KafkaProducerService {
	private final KafkaTemplate<String, String> kafkaTemplate;

	/**
	 * Constructor for KafkaProducerService.
	 * 
	 * @param kafkaTemplate The KafkaTemplate used for sending messages.
	 */
	@Autowired
	public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	/**
	 * Sends a message to the specified Kafka topic.
	 * 
	 * @param topic   The name of the Kafka topic.
	 * @param message The message to be sent.
	 */
	@SuppressWarnings("null")
	public void sendMessage(String topic, String message) {
		kafkaTemplate.send(topic, message);
		System.out.println("Message sent to topic: " + topic);
	}
}

