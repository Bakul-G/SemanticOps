package com.example.repository;

import com.example.data.EventDecision;

import java.util.List;

public interface VectorSearchRepository {

    List<EventDecision> findTop5Similar(String embedding);
}