package com.cardemo.repository;

import com.cardemo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Customer entity.
 * Replaces CICS READ/WRITE/REWRITE operations on CUSTDAT VSAM KSDS file.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    List<Customer> findByLastName(String lastName);

    List<Customer> findBySsn(String ssn);
}
