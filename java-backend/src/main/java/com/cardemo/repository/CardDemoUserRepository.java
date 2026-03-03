package com.cardemo.repository;

import com.cardemo.model.CardDemoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for CardDemoUser entity.
 * Replaces CICS READ/WRITE/REWRITE/DELETE operations on USRSEC VSAM KSDS file.
 * Used by COSGN00C (signon), COUSR00C-03C (user management).
 */
@Repository
public interface CardDemoUserRepository extends JpaRepository<CardDemoUser, String> {

    List<CardDemoUser> findByUserType(String userType);
}
