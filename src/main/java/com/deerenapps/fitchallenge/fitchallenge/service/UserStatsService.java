package com.deerenapps.fitchallenge.fitchallenge.service;

import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import com.deerenapps.fitchallenge.fitchallenge.entities.DailyTracker;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.repos.DailyTrackerRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        calculateLeaderBoard(userStats.getChallenge());
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

    /*
     * Get users with highest reps from multiple categories
     * assigning rank and points for those with highest reps
     */
    public void calculateLeaderBoard(Challenge challenge){

        int highestSetPR = 0;
        int highestDailyPR = 0;
        int highestWeeklyPR = 0;
        int highestCumulative = 0;

        //AtomicInteger statRank = new AtomicInteger(1);
        int statRank;
        UserStats userStatsWinner;

        // Start with a clear board erase everyone's status for a given challenge
        List<UserStats> userStatsList = challenge.getUserStats();
        userStatsList.stream().forEach(userStats -> {
            userStats.setStatus("");
            userStats.setPoints(0);
        });

        // start with a series of sorting for each status number to find highest in each category

        // Current Highest set PR
        userStatsList = userStatsList.stream().sorted(Comparator.comparingInt(UserStats::getSet_pr).reversed()).collect(Collectors.toList());
        highestSetPR = userStatsList.get(0).getSet_pr();

        // Current Highest Daily PR
        userStatsList = userStatsList.stream().sorted(Comparator.comparingInt(UserStats::getDaily_pr).reversed()).collect(Collectors.toList());
        highestDailyPR = userStatsList.get(0).getDaily_pr();

        // Current Highest Weekly PR
        userStatsList = userStatsList.stream().sorted(Comparator.comparingInt(UserStats::getWeekly_pr).reversed()).collect(Collectors.toList());
        highestWeeklyPR = userStatsList.get(0).getWeekly_pr();

        // Current Highest Cumalitive
        userStatsList = userStatsList.stream().sorted(Comparator.comparingInt(UserStats::getCumalitive).reversed()).collect(Collectors.toList());
        highestCumulative = userStatsList.get(0).getCumalitive();

        // for each user stats check if they have the highest status and if so assign status and points
        // this will also ensure if more then one winner all will get points

        for (UserStats userStats : userStatsList) {
            String status = userStats.getStatus();
            userStats.setPoints(userStats.getCumalitive());
            if (userStats.getCumalitive() >= highestCumulative) {
                status = status.equals("") ? "Highest Cumulative" : status + ", Highest Cumulative";
                userStats.setPoints(userStats.getPoints() + 25);
            }
            if (userStats.getSet_pr() >= highestSetPR) {
                status = status.equals("") ? "Highest Set PR" : status + ", Set PR";
                userStats.setPoints(userStats.getPoints() + 10);
            }
            if (userStats.getDaily_pr() >= highestDailyPR) {
                status = status.equals("") ? "Highest Daily PR" : status + ", Daily PR";
                userStats.setPoints(userStats.getPoints() + 15);
            }
            if(userStats.getWeekly_pr() >= highestWeeklyPR) {
                status = status.equals("") ? "Highest Weekly PR" : status + " and Weekly PR";
                userStats.setPoints(userStats.getPoints() + 20);
            }
            userStats.setStatus(status);

        }

        calculateRank(userStatsList);

    }

    private void calculateRank(List<UserStats> userStatsList){
        AtomicInteger rank = new AtomicInteger(1);
        userStatsList.stream()
                .sorted(Comparator.comparingInt(UserStats::getPoints).reversed())
                .map(userStats -> {
                    userStats.setRank(rank.getAndIncrement());
                    return userStatsRepository.save(userStats);
                })
                .collect(Collectors.toList());
    }
}
