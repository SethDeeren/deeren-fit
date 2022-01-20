package com.deerenapps.fitchallenge.fitchallenge.restcontroller;

import com.deerenapps.fitchallenge.fitchallenge.dto.DailyTrackerRequest;
import com.deerenapps.fitchallenge.fitchallenge.entities.DailyTracker;
import com.deerenapps.fitchallenge.fitchallenge.repos.DailyTrackerRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.service.DailyTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DailyTrackerRestController {

    @Autowired
    DailyTrackerRepository dailyTrackerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DailyTrackerService dailyTrackerService;

    @GetMapping("/my_stats")
    public List<DailyTracker> getMyStatsAPI
    (
         @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
         @RequestParam(value = "challengeId") long challengeId
    )
    {
        List<DailyTracker> dailyTrackerList = new ArrayList<>();
        // Get current authenticated username to get user Id there may be a better way
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // For whatever reason spring calls this route before asking for authentication
        // need to do this check ensure out of bounds exception when getting user Id
        if(username.equals("anonymousUser")){
            return dailyTrackerList;
        } else {
            // Get user id for query below
            long userId = userRepository.findByUsername(username).get(0).getId();
            // Get the date
            LocalDate day = date;
            // Get current user's daily tracker for today
            dailyTrackerList = dailyTrackerRepository.findAllByChallengeIdUserIdAndDay(userId,challengeId,day);
            return dailyTrackerList;
        }
    }

    @PostMapping("/my_stats")
    public DailyTracker postMyStatsAPI(@RequestBody DailyTrackerRequest dailyTracker){

        return dailyTrackerService.addDailyTracker(dailyTracker);
    }

    @PutMapping("/my_stats")
    public DailyTracker updateMyStatsAPI(@RequestBody DailyTrackerRequest dailyTrackerRequest){

        DailyTracker dailyTracker = dailyTrackerRepository.findById(dailyTrackerRequest.getId()).get();
        dailyTracker.setReps(dailyTrackerRequest.getReps());

        // save the daily tracker
        return dailyTrackerRepository.save(dailyTracker);
    }

    @DeleteMapping("/my_stats/{id}")
    public DailyTracker deleteMyStatsAPI(@PathVariable int id){
        // find daily tracker by id
        DailyTracker dt = dailyTrackerRepository.findById(id).get();

        dailyTrackerService.deleteDailyTracker(dt);
        // delete the daily tracker
        //dailyTrackerRepository.deleteById(id);
        return dt;
    }
}
