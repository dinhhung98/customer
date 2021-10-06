package com.vnpay.customer.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnpay.customer.model.BankRequest;

import java.io.IOException;

public class MapperObject {

	private ObjectMapper objectMapper;

	private MapperObject() {
		objectMapper = new ObjectMapper();
	}

	public static MapperObject getMapperObject() {
		return new MapperObject();
	} {
		
	}
	public BankRequest toEntity(String jsonObject) throws JsonParseException, JsonMappingException, IOException {
		return this.objectMapper.readValue(jsonObject, BankRequest.class);
	}
}
