package com.example.repository;

import com.example.data.EventDecision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDecisionRepository
        extends JpaRepository<EventDecision, Long>,
                VectorSearchRepository {
}