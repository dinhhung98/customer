package com.vnpay.customer.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@Entity
public class BankRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String tokenKey;
    @NotBlank
    private String apiID;
    @Value("^09\\d{8}$")
    private String mobile;
    private String bankCode;
    @NotBlank
    private String accountNo;
    @NotBlank
    private String payDate;
    private String addtionalData;
    @DecimalMin(value = "0.0",inclusive = false)
    private BigDecimal debitAmount;
    @NotBlank
    private String respCode;
    @NotBlank
    private String respDesc;
    @NotBlank
    private String traceTransfer;
    @Value("1")
    private String messageType;
    @NotBlank
    private String checkSum;
    @NotBlank
    private String orderCode;
    @NotBlank
    private String userName;
    @DecimalMin(value = "0.0",inclusive = false)
    private BigDecimal realAmount;
    private String promotionCode;
    @Value( "{\"payMethod\":\"01\",\"payMethodMMS\":1}")
    private String addValue;
}
