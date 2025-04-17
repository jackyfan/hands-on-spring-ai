package com.jackyfan.handsonspringai.service;

import com.jackyfan.handsonspringai.domain.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameRepository extends CrudRepository<Game, Long> {

    Optional<Game> findBySlug(String slug);

}