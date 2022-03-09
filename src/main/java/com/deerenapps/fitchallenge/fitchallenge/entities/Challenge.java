package com.deerenapps.fitchallenge.fitchallenge.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private String motto;

    @Column(name = "crew_total")
    private int crewTotal;

    @Column(name="active")
    private boolean active;

    // A Tracker does not make sense without a challenge it belongs to
    // Therefore this is a uni-directional relationship when we delete a challenge delete all trackers
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="challenge_id") // refers to challenge_id in the tracker table
    private List<UserStats> userStats;

    public Challenge(){

    }

    public Challenge(String title, String motto, int crewTotal) {
        this.title = title;
        this.motto = motto;
        this.crewTotal = crewTotal;
    }

    @Column(name = "start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd") //'T'HH:mm
    private LocalDate startDate;

    @Column(name = "end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd") //'T'HH:mm
    private LocalDate endDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getCrewTotal() {
        return crewTotal;
    }

    public void setCrewTotal(int crewTotal) {
        this.crewTotal = crewTotal;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<UserStats> getUserStats() {
        return userStats;
    }

    public void setUserStats(List<UserStats> trackers) {
        this.userStats = trackers;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", motto='" + motto + '\'' +
                ", crewTotal=" + crewTotal +
                ", startDate=" + startDate +
                '}';
    }
}
