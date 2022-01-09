package com.deerenapps.fitchallenge.fitchallenge.controller;

import com.deerenapps.fitchallenge.fitchallenge.entities.DailyTracker;
import com.deerenapps.fitchallenge.fitchallenge.entities.User;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.repos.DailyTrackerRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.service.DailyTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DailyTrackerController {

    @Autowired
    DailyTrackerRepository dailyTrackerRepository;

    @Autowired
    DailyTrackerService dailyTrackerService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/challenges/{id}/my_trackers")
    public String getMyDailyTrackers(Model theModel,
                                     @PathVariable("id") long challengeId){
        // add to the spring model, "stats" is what is referenced in the template
        theModel.addAttribute("stats", dailyTrackerService.getMyDailyTrackers(challengeId));
        theModel.addAttribute("challengeId",challengeId);
        return "stat-editor";
    }

    @GetMapping("/challenges/{id}/my_trackers/{day}")
    public String getMyStatsForGivenDay(Model theModel,
                                        @PathVariable("id") long challengeId,
                                        @PathVariable(value = "day") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day){

        theModel.addAttribute("stats", dailyTrackerService.getMyDailyTrackers(challengeId, day));
        theModel.addAttribute("challengeId",challengeId);
        return "stat-editor";
    }


    @GetMapping("/my_trackers/{dailyTrackerId}")
    public String editStat(Model model,
                           @PathVariable(value = "dailyTrackerId") int id){

        // get daily tracker
        DailyTracker dt = dailyTrackerRepository.findById(id).get();

        // get challenge id
        long challengeId = dailyTrackerService.findChallengeIdByDailyTracker(id);

        // set the model
        model.addAttribute("dailyTracker", dt);
        model.addAttribute("challengeId", challengeId);

        // model sends to the edit form automagically
        return "daily-tracker-edit";
    }

    @PostMapping("/my_trackers")
    public String postOrUpdateMyStats(@ModelAttribute("dailyTracker") DailyTracker dailyTracker){

        DailyTracker dt = dailyTrackerService.editDailyTracker(dailyTracker);

        long challengeId = dailyTrackerService.findChallengeIdByDailyTracker(dailyTracker.getId());

        // use a redirect to prevent duplicate submissions
        return "redirect:/challenges/" + challengeId + "/my_trackers/" + dt.getDay();
    }

    @PostMapping("/my_trackers/delete")
    public String delete(@ModelAttribute("dailyTracker") DailyTracker dailyTracker) {

        long challengeId = dailyTrackerService.findChallengeIdByDailyTracker(dailyTracker.getId());

        LocalDate date = dailyTrackerService.deleteDailyTracker(dailyTracker);

        // use a redirect to prevent duplicate submissions
        return "redirect:/challenges/" + challengeId + "/my_trackers/" + date;

    }
}
