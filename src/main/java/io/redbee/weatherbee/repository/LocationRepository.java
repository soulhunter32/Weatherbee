package io.redbee.weatherbee.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.redbee.weatherbee.domain.Location;

@Transactional
public interface LocationRepository extends JpaRepository<Location, Integer>{
}
