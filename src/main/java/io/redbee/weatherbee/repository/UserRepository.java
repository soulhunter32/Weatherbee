package io.redbee.weatherbee.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.redbee.weatherbee.domain.User;

@Transactional
public interface UserRepository extends JpaRepository<User, Integer>{

	public User findByUsername(String username);
}
