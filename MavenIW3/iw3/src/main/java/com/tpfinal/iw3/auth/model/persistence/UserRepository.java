package com.tpfinal.iw3.auth.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpfinal.iw3.auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{ 
	public Optional<User> findOneByUsernameOrEmail(String username, String email);
}
