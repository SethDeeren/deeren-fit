package com.deerenapps.fitchallenge.fitchallenge.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //private long userId;
    private int cumalitive;
    private int current_week;
    private int daily_pr;
    private int weekly_pr;
    private int set_pr;
    private String status;

    @ManyToOne
    //@JoinColumn(name="challenge_id")
    private Challenge challenge;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy="userStats", cascade = CascadeType.ALL)
    private List<DailyTracker> dailyTrackers;

    public UserStats(){

    }

    public UserStats(Challenge challenge, User user) {
        this.challenge = challenge;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCumalitive() {
        return cumalitive;
    }

    public void setCumalitive(int cumalitive) {
        this.cumalitive = cumalitive;
    }

    public int getCurrent_week() {
        return current_week;
    }

    public void setCurrent_week(int current_week) {
        this.current_week = current_week;
    }

    public int getDaily_pr() {
        return daily_pr;
    }

    public void setDaily_pr(int daily_pr) {
        this.daily_pr = daily_pr;
    }

    public int getWeekly_pr() {
        return weekly_pr;
    }

    public void setWeekly_pr(int weekly_pr) {
        this.weekly_pr = weekly_pr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSet_pr() {
        return set_pr;
    }

    public void setSet_pr(int set_pr) {
        this.set_pr = set_pr;
    }

    public List<DailyTracker> getDailyTrackers() {
        return dailyTrackers;
    }

    public void setDailyTrackers(List<DailyTracker> dailyTrackers) {
        this.dailyTrackers = dailyTrackers;
    }

    public void addDailyTracker(DailyTracker dailyTracker){
        if(dailyTrackers == null){
            dailyTrackers = new ArrayList<>();
        }
        dailyTrackers.add(dailyTracker);
        // update cummalitve reps

        // update weekly pr

        // update daily pr

        //update
        dailyTracker.setUserStats(this);
    }
}
