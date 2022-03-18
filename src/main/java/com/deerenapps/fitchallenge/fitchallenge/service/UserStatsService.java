package com.deerenapps.fitchallenge.fitchallenge.service;

import com.deerenapps.fitchallenge.fitchallenge.entities.DailyTracker;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.repos.DailyTrackerRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class UserStatsService {
    @Autowired
    UserStatsRepository userStatsRepository;

    @Autowired
    DailyTrackerRepository dailyTrackerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChallengeService challengeService;

    public List<UserStats> findUserStats(long challengeId){
        return userStatsRepository.findAllByChallengeId(challengeId);
    }


    public DailyTracker addDailyTracker(UserStats userStats, DailyTracker dailyTracker) {

        // added to the entity association established
        userStats.addDailyTracker(dailyTracker);

        dailyTrackerRepository.save(dailyTracker);
        calculateStats(userStats);

        return dailyTracker;
    }

    public void calculateStats(UserStats userStats){

        // The list to be calculated
        List<DailyTracker> dailTrackers = dailyTrackerRepository.findAllByUserStatsIdAndSortByDayAsc(userStats.getId());


        // Values used for calc
        int challengeStartDate = userStats.getChallenge().getStartDate().getDayOfYear();
        int currentDay = challengeStartDate;
        int currentTrackerDay;
        int currentTrackerWeek = 0;

        // Initial values for User stats
        int weeklyCumulative = 0;
        int currentWeeklyCumulative = 0;
        int dailyCumulative = 0;
        int cumulative = 0;
        int setPR = 0;
        int weeklyPR = 0;
        int dailyPR = 0;

        // Begins the calc process
        for(DailyTracker dailyTracker : dailTrackers){

            // current tracker reps
            int reps = dailyTracker.getReps();

            // current tracker tracker day
            currentTrackerDay = dailyTracker.getDay().getDayOfYear();


            // add to cumulative
            cumulative = cumulative + reps;

            // add to current week
            currentWeeklyCumulative = isTrackerCurrentWeek(dailyTracker,challengeStartDate) ? currentWeeklyCumulative + reps : currentWeeklyCumulative;

            // Check for set PR
            if(reps > setPR){
                setPR = reps;
            }

            // check for daily pr or add to daily and also update newDay indicator, currentDay and dayOfWeek
            if(currentDay == currentTrackerDay){
                dailyCumulative = dailyCumulative + reps;
            } else {
                dailyCumulative = reps;
                currentDay = currentTrackerDay;
            }

            if(dailyCumulative >= dailyPR){
                dailyPR = dailyCumulative;
            }

            // cumulating reps in the week, check for weekly pr
            if(currentTrackerWeek != getCurrentTrackerWeek(dailyTracker, challengeStartDate)){
                weeklyCumulative = reps;
                currentTrackerWeek = getCurrentTrackerWeek(dailyTracker, challengeStartDate);
            } else {
                weeklyCumulative = weeklyCumulative + reps;
            }
            if(weeklyCumulative > weeklyPR){
                weeklyPR = weeklyCumulative;
            }

        }
        // TODO update status based on cummulative

        if(weeklyCumulative > userStats.getWeekly_pr()){
            userStats.setWeekly_pr(weeklyCumulative);
        }
        if(dailyCumulative > userStats.getDaily_pr()){
            userStats.setDaily_pr(dailyCumulative);
        }

        userStats.setCurrent_week(currentWeeklyCumulative);
        userStats.setCumalitive(cumulative);
        userStats.setDaily_pr(dailyPR);
        userStats.setWeekly_pr(weeklyPR);
        userStats.setSet_pr(setPR);
        userStatsRepository.save(userStats);
        challengeService.calculateCrewTotal(userStats.getChallenge());
    }

    public boolean isTrackerCurrentWeek(DailyTracker dailyTracker, int startDay){
        // integer divide by seven will give us the week number the tracker falls under
        int currentWeek = (LocalDate.now().getDayOfYear() - startDay) / 7;
        int trackerWeek = (dailyTracker.getDay().getDayOfYear() - startDay) / 7;
        return currentWeek == trackerWeek;
    }

    public int getCurrentTrackerWeek(DailyTracker dailyTracker, int startDay){
        return (dailyTracker.getDay().getDayOfYear() - startDay) / 7;
    }
}
