package com.deerenapps.fitchallenge.fitchallenge.service;

import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.repos.ChallengeRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            int userCumulative = function.apply(userStats); // instead of get cumulative how to pass a method reference

            returnMap.put(userStats.getUser().getName(), userCumulative);

        }
        return returnMap;
    }

    public Map<String, Integer> getGroupAccumulationOverTime(long challengeId){
        // return map with string date and integer group amount
        Map<String, Integer> resultMap = challengeRepository.findAllByUserStatsIdAndSortByDayAsc(challengeId);
        String string = "string";
        // possible return hash ma



        return resultMap;
    }

    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }
}
