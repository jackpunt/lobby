package com.thegraid.lobby.repository;

import com.thegraid.lobby.domain.MemberGameProps;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MemberGameProps entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MemberGamePropsRepository extends JpaRepository<MemberGameProps, Long> {
    @Query("select memberGameProps from MemberGameProps memberGameProps where memberGameProps.user.login = ?#{principal.username}")
    List<MemberGameProps> findByUserIsCurrentUser();
}
