package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.GamePlayer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GamePlayer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {}
