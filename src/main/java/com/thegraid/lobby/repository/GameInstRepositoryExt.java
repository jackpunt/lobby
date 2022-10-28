package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.GameInst;
import com.thegraid.lobby.domain.GameInstProps;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface GameInstRepositoryExt extends GameInstRepository {
    @Query("select giProps from GameInstProps AS giProps where giProps.gameInst = :gameInst")
    List<GameInstProps> getGameInstProps(@Param("gameInst") GameInst gameInst);
    //Map<String, Object> getPropertyMap(@Param("gameInst") GameInst gameInst);

}
