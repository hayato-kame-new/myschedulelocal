package com.kame.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kame.springboot.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
}
