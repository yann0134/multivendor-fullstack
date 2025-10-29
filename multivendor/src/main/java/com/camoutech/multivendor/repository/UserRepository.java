package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.domain.USER_ROLE;
import com.camoutech.multivendor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
    
    List<User> findByRole(USER_ROLE role);
}
