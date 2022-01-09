package com.deerenapps.fitchallenge.fitchallenge.repos;

import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
