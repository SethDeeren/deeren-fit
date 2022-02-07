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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/challenges/{challengeId}/charts")
    public String getChallengeCharts(Model model, @PathVariable long challengeId){
        Map<String, Integer> userCumulatives = new LinkedHashMap<String, Integer>();
        int maxUserCumulativeValue = 0;
        for(UserStats userStats : userStatsService.findUserStats(challengeId)){
            int userCumulative = userStats.getCumalitive();
            maxUserCumulativeValue = userCumulative > maxUserCumulativeValue ? userCumulative : maxUserCumulativeValue;
            userCumulatives.put(userStats.getUser().getName(), userCumulative);
        }
        model.addAttribute("challengeId", challengeId);

        model.addAttribute("maxUserCumulativeValue", maxUserCumulativeValue + (maxUserCumulativeValue/4));
        model.addAttribute("userCumulativesKeySet", userCumulatives.keySet());
        model.addAttribute("userCumulativeValues", userCumulatives.values());

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
