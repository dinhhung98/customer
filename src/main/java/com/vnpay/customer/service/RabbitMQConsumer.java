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
import org.springframework.http.HttpEntity;
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
			BankRequest bankRequest = MapperObject.getMapperObject().toEntity(queueData);
			logger.info("Data insert to sql: {}", bankRequest.toString());
			if (!saveToDatabase(bankRequest)){
				return "save to database false";
			}
			logger.info("Save payment success: {}", "200");
			ResponseEntity<?> result =sendToServer(bankRequest, url);
			logger.info("Response to server: ", result);
			return String.valueOf(result.getStatusCodeValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Process queue exception: ", e);
			return "";
		} finally {
			ThreadContext.pop();
	        ThreadContext.clearAll();
		}
	}
	private ResponseEntity<?> sendToServer(BankRequest bankRequest, String url){
		logger.info("Begin to server: {} and url: {}", bankRequest, url);
		try {
			RestTemplate restTemplate = new RestTemplate();
//			ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, bankRequest, Object.class);
			HttpEntity<BankRequest> requestBody = new HttpEntity<>(bankRequest);
			ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, requestBody, Object.class);
			return responseEntity;
		} catch (Exception e) {
			logger.error("Send to server: ", e);
			return null;
		}
	}

	private boolean saveToDatabase(BankRequest bankRequest){
		try {
			paymentRepository.save(bankRequest);
			return true;
		}catch (Exception e){
			logger.error("Save request to database ",e);
			return false;
		}
	}
}