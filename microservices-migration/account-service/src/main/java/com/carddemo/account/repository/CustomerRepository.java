package com.carddemo.account.repository;

import com.carddemo.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query("SELECT c FROM Customer c WHERE c.ssn = :ssn")
    Optional<Customer> findBySsn(String ssn);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Customer> searchByLastName(String name, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.stateCode = :stateCode")
    List<Customer> findByStateCode(String stateCode);
}
