package com.deerenapps.fitchallenge.fitchallenge.service;

import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.repos.ChallengeRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChallengeService {

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    UserStatsRepository userStatsRepository;

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

    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }
}
