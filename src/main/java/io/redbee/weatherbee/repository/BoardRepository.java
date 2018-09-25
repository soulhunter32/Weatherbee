package io.redbee.weatherbee.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import io.redbee.weatherbee.domain.Board;

@Transactional
public interface BoardRepository extends CrudRepository<Board, Integer>{
}
