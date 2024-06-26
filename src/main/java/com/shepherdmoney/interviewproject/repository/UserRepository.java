package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Crud Repository to store User classes
 */
@Repository("UserRepo")
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByName(String name);
    List<User> findAllByEmail(String email);

    boolean existsByName(String name);
    boolean existsByEmail(String email);
}
