package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.GameInstProps;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GameInstProps entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameInstPropsRepository extends JpaRepository<GameInstProps, Long> {}
