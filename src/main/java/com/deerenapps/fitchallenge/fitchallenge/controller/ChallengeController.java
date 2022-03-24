package com.deerenapps.fitchallenge.fitchallenge.controller;

import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import com.deerenapps.fitchallenge.fitchallenge.entities.DailyTracker;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.repos.DailyTrackerRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.service.ChallengeService;
import com.deerenapps.fitchallenge.fitchallenge.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import javax.persistence.criteria.CriteriaBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
public class ChallengeController {

    @Autowired
    DailyTrackerRepository dailyTrackerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserStatsService userStatsService;

    @Autowired
    ChallengeService challengeService;

    @GetMapping("")
    public String redirectChallenges(){
        return "redirect:/challenges";
    }

    @GetMapping("/challenges")
    public String getChallenges(Model model){
        // challenge service get all challenges
        List<Challenge> challenges = challengeService.getAllChallenges();
        model.addAttribute("challenges", challenges);
        return "challenge-list";
    }

    @GetMapping("/challenges/{challengeId}")
    public String getDashboard(Model model, @PathVariable long challengeId){

        model.addAttribute("stats", userStatsService.findUserStats(challengeId));
        model.addAttribute("challenge", challengeService.getChallengeById(challengeId));
        return "challenge-dashboard";
    }

    @GetMapping("/challenges/{challengeId}/leader-board")
    public String getLeaderBoard(Model model, @PathVariable long challengeId){

        model.addAttribute("stats", userStatsService.findUserStats(challengeId)
                .stream().sorted(Comparator.comparingInt(UserStats::getRank)).collect(Collectors.toList())
        );
        model.addAttribute("challenge", challengeService.getChallengeById(challengeId));
        return "challenge-leader-board";
    }

    @GetMapping("/challenges/{challengeId}/charts")
    public String getChallengeCharts(Model model, @PathVariable long challengeId){

        // Getting Maps should be in ChallengeService class

        // more complex query needed special method possibly change ddl in future
        Map<String, Integer> teamAccumulationOverTimeMap = challengeService.getGroupAccumulationOverTime(challengeId);

        Map<String, Integer> userCumulatives = challengeService.getChallengeChartMap(challengeId, UserStats::getCumalitive);
        Map<String, Integer> userSetPrs = challengeService.getChallengeChartMap(challengeId, UserStats::getSet_pr);
        Map<String, Integer> userDailyPRs = challengeService.getChallengeChartMap(challengeId, UserStats::getDaily_pr);
        Map<String, Integer> userWeeklyPRs = challengeService.getChallengeChartMap(challengeId, UserStats::getWeekly_pr);



        // dashboard link
        model.addAttribute("challengeId", challengeId);

        // for group accumulation over time chart
        model.addAttribute("groupAccumulationOverTimeKeySet", teamAccumulationOverTimeMap.keySet());
        model.addAttribute("groupAccumulationOverTimeValues", teamAccumulationOverTimeMap.values());

        // for cumulative by user chart
        model.addAttribute("userCumulativesKeySet", userCumulatives.keySet());
        model.addAttribute("userCumulativeValues", userCumulatives.values());

        // for set PR chart
        model.addAttribute("userSetPRKeySet",userSetPrs.keySet());
        model.addAttribute("userSetPRValues", userSetPrs.values());

        // for daily PR chart
        model.addAttribute("userDailyPRKeySet",userDailyPRs.keySet());
        model.addAttribute("userDailyPRValues", userDailyPRs.values());

        // for weekly PR chart
        model.addAttribute("userWeeklyPRKeySet",userWeeklyPRs.keySet());
        model.addAttribute("userWeeklyPRValues", userWeeklyPRs.values());



        return "challengeCharts";
    }

    @GetMapping("/challenges/create-form")
    public String getCreateForm(Model model){
        Challenge challenge = new Challenge();
        //LocalDate startDate = LocalDate.now();
        //challenge.setStartDate(startDate);
        model.addAttribute("challenge", challenge);
        return "challenge-create-form";
    }

    @PostMapping("/challenges")
    public String createChallenge(@ModelAttribute("challenge") Challenge challenge){
        System.out.println(challenge);
        challengeService.createChallenge(challenge);
        return "redirect:/challenges";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }



}
