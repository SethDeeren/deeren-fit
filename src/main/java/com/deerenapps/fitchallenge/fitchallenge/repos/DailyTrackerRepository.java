package com.deerenapps.fitchallenge.fitchallenge.repos;

import com.deerenapps.fitchallenge.fitchallenge.entities.DailyTracker;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface DailyTrackerRepository extends JpaRepository<DailyTracker,Integer> {


    @Query(value = "SELECT * FROM daily_tracker WHERE user_stats_id = :userStatsId order by day", nativeQuery = true)
    List<DailyTracker> findAllByUserStatsIdAndSortByDayAsc(int userStatsId);

    @Query(value = "SELECT * FROM daily_tracker " +
            "JOIN user_stats on daily_tracker.user_stats_id = user_stats.id " +
            "JOIN challenge on challenge.id = user_stats.challenge_id " +
            "WHERE user_stats.user_id = :userId " +
            "AND challenge.id = :challengeId " +
            "AND daily_tracker.day = :day", nativeQuery = true)
    List<DailyTracker> findAllByChallengeIdUserIdAndDay(long userId, long challengeId, LocalDate day);

    @Query(value = "SELECT challenge.id FROM challenge " +
            "JOIN user_stats on user_stats.challenge_id = challenge.id " +
            "JOIN daily_tracker on daily_tracker.user_stats_id = user_stats.id " +
            "where daily_tracker.id = :id", nativeQuery = true)
    long findChallengeIdByDailyTracker(int id);
}
