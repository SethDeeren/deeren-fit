package com.deerenapps.fitchallenge.fitchallenge.repos;

import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import com.deerenapps.fitchallenge.fitchallenge.entities.DailyTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query(value = "select " +
            "day, sum(reps) " +
            "from daily_tracker " +
            "join user_stats on user_stats.id = user_stats_id " +
            "join challenge on challenge.id = challenge_id " +
            "where challenge.id = :challengeId " +
            "Group By day " +
            "order by day;", nativeQuery = true)
    List<Object[]> findAccumulationByDay(long challengeId);
}
