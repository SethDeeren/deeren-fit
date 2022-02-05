package com.deerenapps.fitchallenge.fitchallenge.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String username;
    @Column
    private String name;
    @Column
    private String password;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    private List<DailyTracker> dailyTrackers;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    private List<UserStats> userStats;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<DailyTracker> getDailyTrackers() {
        return dailyTrackers;
    }

    public void setDailyTrackers(List<DailyTracker> dailyTrackers) {
        this.dailyTrackers = dailyTrackers;
    }

    public List<UserStats> getUserStats() {
        return userStats;
    }

    public void setUserStats(List<UserStats> userStats) {
        this.userStats = userStats;
    }

    public void addDailyTracker(DailyTracker dailyTracker){
        if(dailyTrackers == null){
            dailyTrackers = new ArrayList<>();
        }

        dailyTrackers.add(dailyTracker);
        dailyTracker.setUser(this);
    }
}
