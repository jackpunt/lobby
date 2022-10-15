package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.GameInst;
import com.thegraid.lobby.domain.GamePlayer;
import com.thegraid.lobby.domain.Player;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface GamePlayerRepositoryExt extends GamePlayerRepository {
    @Query("select gamePlayer from GamePlayer AS gamePlayer where gamePlayer.player = :plyr")
    List<GamePlayer> getGamePlayers(@Param("plyr") Player plyr);

    @Query("select gamePlayer from GamePlayer AS gamePlayer where gamePlayer.gameInst = :gi")
    List<GamePlayer> getGamePlayers(@Param("gi") GameInst gi);
}
