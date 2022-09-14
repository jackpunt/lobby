package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.GameInst;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GameInst entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameInstRepository extends JpaRepository<GameInst, Long> {}
