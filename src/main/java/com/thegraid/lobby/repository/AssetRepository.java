package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.Asset;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Asset entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    @Query("select asset from Asset asset where asset.user.login = ?#{principal.username}")
    List<Asset> findByUserIsCurrentUser();
}
