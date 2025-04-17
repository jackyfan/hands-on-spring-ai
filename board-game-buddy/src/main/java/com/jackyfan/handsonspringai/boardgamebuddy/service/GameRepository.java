package com.jackyfan.handsonspringai.boardgamebuddy.service;

import com.jackyfan.handsonspringai.boardgamebuddy.domain.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameRepository extends CrudRepository<Game, Long> {

    Optional<Game> findBySlug(String slug);

}