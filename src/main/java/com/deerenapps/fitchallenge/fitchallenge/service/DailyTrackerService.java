package com.deerenapps.fitchallenge.fitchallenge.service;

import com.deerenapps.fitchallenge.fitchallenge.dto.DailyTrackerRequest;
import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import com.deerenapps.fitchallenge.fitchallenge.entities.DailyTracker;
import com.deerenapps.fitchallenge.fitchallenge.entities.Users;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.repos.ChallengeRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.DailyTrackerRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyTrackerService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DailyTrackerRepository dailyTrackerRepository;

    @Autowired
    UserStatsService userStatsService;

    @Autowired
    UserStatsRepository userStatsRepository;

    @Autowired
    ChallengeRepository challengeRepository;

   /*
    *   Get's current logged in user
    *   Finds all current day's daily trackers from given challenge
    *
    */
    public List<DailyTracker> getMyDailyTrackers(long challengeId){
        // get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = userRepository.findByUsername(username).get(0);

        // find or create user to challenge association
        if(userStatsRepository.findByUserIdAndChallengeId(user.getId(), challengeId) == null){
            Challenge challenge = challengeRepository.findById(challengeId).get();
            UserStats userStats = new UserStats(challenge, user);
            userStatsRepository.save(userStats);
        }

        // get current date
        LocalDate day = LocalDate.now();

        // get daily tracker from current challenge
        List<DailyTracker> dailyTrackerList = dailyTrackerRepository.findAllByChallengeIdUserIdAndDay(user.getId(),challengeId,day);
        return dailyTrackerList;
    }

    /*
     *   Get's current logged in user
     *   Finds all given day's daily trackers from given challenge
     *
     */
    public List<DailyTracker> getMyDailyTrackers(long challengeId, LocalDate day){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        long userId = userRepository.findByUsername(username).get(0).getId();
        List<DailyTracker> dailyTrackerList =
                dailyTrackerRepository.findAllByChallengeIdUserIdAndDay(userId,challengeId,day);
        return dailyTrackerList;
    }

    /*
     *   Adds daily tracker to user, updates their stats
     *   currently each user only has one stats record
     *   will need to update method if in future there is more TODO
     */
    public DailyTracker addDailyTracker(DailyTrackerRequest dailyTrackerRequest){

        // Get the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = userRepository.findByUsername(username).get(0);
        // Create the Daily tracker
        DailyTracker dailyTracker = new DailyTracker();
        dailyTracker.setReps(dailyTrackerRequest.getReps());
        dailyTracker.setDay(dailyTrackerRequest.getDay());
        // Add values to respective entities for associations
        user.addDailyTracker(dailyTracker);
        UserStats userStats = userStatsRepository.findByUserIdAndChallengeId(user.getId(), dailyTrackerRequest.getChallengeId());


        return userStatsService.addDailyTracker(userStats,dailyTracker);

    }

    public DailyTracker editDailyTracker(DailyTracker dailyTracker){
        DailyTracker dt= dailyTrackerRepository.findById(dailyTracker.getId()).get();
        dt.setReps(dailyTracker.getReps());


        // save the daily tracker
        dailyTrackerRepository.save(dt);

        UserStats userStats = dt.getUserStats();

        userStatsService.calculateStats(userStats);

        return dt;
    }

    public DailyTracker deleteById(int id){
        // get daily tracker
        DailyTracker dt = dailyTrackerRepository.findById(id).get();

        dailyTrackerRepository.deleteById(id);

        userStatsService.calculateStats(dt.getUserStats());

        return dt;

    }


    public long findChallengeIdByDailyTracker(int id) {
        return dailyTrackerRepository.findChallengeIdByDailyTracker(id);
    }

    public LocalDate deleteDailyTracker(DailyTracker dailyTracker) {

        DailyTracker dailyTrackerDelete = dailyTrackerRepository.findById(dailyTracker.getId()).get();
        UserStats userStats = dailyTrackerDelete.getUserStats();
        LocalDate date = dailyTrackerDelete.getDay();
        dailyTrackerRepository.delete(dailyTracker);
        userStatsService.calculateStats(userStats);
        return date;
    }
}
