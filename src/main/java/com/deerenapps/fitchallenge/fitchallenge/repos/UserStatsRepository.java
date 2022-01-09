package com.deerenapps.fitchallenge.fitchallenge.repos;

import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStatsRepository extends JpaRepository<UserStats, Integer> {
    List<UserStats> findAllByChallengeId(long challengeId);
    UserStats findByUserId(long userId);

    UserStats findByUserIdAndChallengeId(long id, long challengeId);
}