package com.vnpay.customer.repository;

import com.vnpay.customer.model.BankRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<BankRequest, Long> {

}
