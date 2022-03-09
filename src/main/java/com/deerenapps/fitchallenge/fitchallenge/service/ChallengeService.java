package com.deerenapps.fitchallenge.fitchallenge.service;

import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.repos.ChallengeRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class ChallengeService {

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    UserStatsRepository userStatsRepository;

    @Autowired
    UserStatsService userStatsService;


    public List<Challenge> getAllChallenges(){
        return challengeRepository.findAll();
    }

    public Challenge getChallengeById(Long challengeId){
        return challengeRepository.findById(challengeId).get();
    }

    public void calculateCrewTotal(Challenge challenge) {
        int crewTotall = 0;
        List<UserStats> userStatsList = userStatsRepository.findAllByChallengeId(challenge.getId());
        for(UserStats userStats : userStatsList){
            crewTotall = crewTotall + userStats.getCumalitive();
        }

        challenge.setCrewTotal(crewTotall);
        challengeRepository.save(challenge);
    }

    public Map<String, Integer> getChallengeChartMap(long challengeId, Function<UserStats, Integer> function){
        Map<String, Integer> returnMap = new LinkedHashMap<String, Integer>();
        List<UserStats> userStatsList = userStatsService.findUserStats(challengeId);

        for(UserStats userStats : userStatsList){

            int userCumulative = function.apply(userStats);

            returnMap.put(userStats.getUser().getName(), userCumulative);

        }
        return returnMap;
    }

    // Probably better way to do this I don't know how to return anything but lists from JPA repos
    // those have List<Object[]> Object[] is just two values returned from a query ( would be better as hashmap)

    public Map<String, Integer> getGroupAccumulationOverTime(long challengeId){

        // return map with string date and integer group amount
        Map<String, Integer> resultMap = new LinkedHashMap<String, Integer>();

        List<Object[]> challengeSumListByDay = challengeRepository.findAccumulationByDay(challengeId);


        int sum = new BigInteger("0").intValue();
        int i = 0;
        for (Object[] dayAndSum : challengeSumListByDay){
            // we don't want to many dates on one chart so this is a way
            // to get 10 date plus first and last day
            int numOfChallengeDays = challengeSumListByDay.size();
            int theModulator = numOfChallengeDays / 10; // the 10 days

            sum = sum + (((BigInteger)dayAndSum[1])).intValue();
            LocalDate date = ((Date) dayAndSum[0]).toLocalDate();
            String day = date.getMonth() + " " + date.getDayOfMonth();

            if(i == 0 || i % theModulator == 0 || i == numOfChallengeDays - 1){
                resultMap.put(day, sum);
            }

            i++;
        }





        // Get the challenge
        Challenge challenge = challengeRepository.findById(challengeId).get();

        // number of days we want to display and associated dates
        //int num = challenge.getStartDate().getDayOfYear();

        // will need to concat these two for the key like "January " + 1

        // challenge.getStartDate().getMonth().name() // gives you the month name
        // challenge.getStartDate().getDayOfMonth() // and day of month

        // will need to sum values up until day needed.


        String string = "string";
        // possible return hash ma



        return resultMap;
    }

    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    /*
     * Run this method everyday at 1 am
     * calculate only for active challenges hitting a week mark
     *
     * This is an O(n^2) method. But it is running at 1 am when users aren't active
     *
     * cron = second, minute, hour, day of month, month, day(s) of week
     * */
    @Scheduled(cron = "0 0 1 * * *")
    public void calculateAllUserStatsForEachChallenge(){

        System.out.println("calculateAllUserStatsForEachChallenge: ran on " + LocalDate.now());

        // find all active challenges
        List<Challenge> activeChallenges = challengeRepository.findByActiveTrue();
        System.out.println("calculateAllUserStatsForEachChallenge: number of active challenges " + activeChallenges.size());
        // loop
        for (Challenge challenge : activeChallenges){
            // check if new week, if yes calculate all stats
            if((challenge.getStartDate().getDayOfYear() - LocalDate.now().getDayOfYear()) % 7 == 0){
                for(UserStats userStats : challenge.getUserStats()){
                    userStatsService.calculateStats(userStats);
                    System.out.println("Calculate userStats.id " + userStats.getId());
                }
            }
        }

    }

    // another method possibly to set an indicator of a challenge ended
}

