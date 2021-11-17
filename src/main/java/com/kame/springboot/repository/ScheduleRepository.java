package com.kame.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kame.springboot.entity.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

	

}
