package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
}
