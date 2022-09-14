package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.Player;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Player entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("select player from Player player where player.user.login = ?#{principal.username}")
    List<Player> findByUserIsCurrentUser();
}
