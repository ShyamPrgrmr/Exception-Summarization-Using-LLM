package com.exception.summarization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
		private final KafkaTemplate<String, String> kafkaTemplate;

		@Autowired
		public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
			this.kafkaTemplate = kafkaTemplate;
		}
		@SuppressWarnings("null")
        public void sendMessage(String topic, String message) {
			kafkaTemplate.send(topic, message);
			System.out.println("Message sent to topic: " + topic);
		}
	}

