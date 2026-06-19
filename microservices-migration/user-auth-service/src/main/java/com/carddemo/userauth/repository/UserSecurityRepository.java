package com.carddemo.userauth.repository;

import com.carddemo.common.entity.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, String> {

    List<UserSecurity> findByUserType(String userType);

    @Query("SELECT u FROM UserSecurity u WHERE u.userType = 'A'")
    List<UserSecurity> findAllAdmins();

    @Query("SELECT u FROM UserSecurity u WHERE u.userType = 'U'")
    List<UserSecurity> findAllRegularUsers();
}
