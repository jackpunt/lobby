package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.GameClass;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GameClass entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameClassRepository extends JpaRepository<GameClass, Long> {}
