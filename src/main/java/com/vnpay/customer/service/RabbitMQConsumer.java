package com.vnpay.customer.service;

import com.vnpay.customer.model.BankRequest;
import com.vnpay.customer.repository.PaymentRepository;
import com.vnpay.customer.util.MapperObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class RabbitMQConsumer {
	@Autowired
	PaymentRepository paymentRepository;
	private static Logger logger = LogManager.getLogger(RabbitMQConsumer.class);
	@Value("${vnpay.server.url}")
	String url;
	@RabbitListener(queues = "${vnpay.rabbitmq.queue}")
	public String recievedMessage(Message message) {
		String tokenRequest = UUID.randomUUID().toString();
		ThreadContext.put("token", tokenRequest);
		String queueData = new String(message.getBody());
		logger.info("message input : {}",message);
		try {
			logger.info("Queue data request: {}", queueData);
			BankRequest payment = MapperObject.getMapperObject().toEntity(queueData);
			logger.info("Data insert to sql: {}", payment.toString());
			paymentRepository.save(payment);
			logger.info("Save payment success: {}", "200");
			ResponseEntity<?> result =sendToServer(payment, url);
			logger.info("Response to server: ", result);
			if(200 == result.getStatusCodeValue()) {
				return String.valueOf(result.getStatusCodeValue());
			}
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Process queue exception: ", e);
			return "";
		} finally {
			ThreadContext.pop();
	        ThreadContext.clearAll();
		}
	}
	private ResponseEntity<?> sendToServer(BankRequest payment, String url){
		logger.info("Begin to server: {} and url: {}", payment, url);
		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, payment, Object.class);
			return responseEntity;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Send to server: ", e);
			return null;
		}
	}
	
}