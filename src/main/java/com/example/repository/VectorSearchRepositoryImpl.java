package com.example.repository;

import com.example.data.EventDecision;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;  
import java.util.ArrayList;
import java.util.List;

@Repository
public class VectorSearchRepositoryImpl implements VectorSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EventDecision> findTop5Similar(String embedding) {

        String sql = """
            SELECT
                id,
                event_id,
                message,
                severity,
                reason,
                timestamp,
                embedding <-> CAST(:embedding AS vector) AS distance
            FROM event_decision
            WHERE embedding IS NOT NULL
            AND embedding <-> CAST(:embedding AS vector) < 0.75
            ORDER BY distance
            LIMIT 5
            """;

        List<Object[]> rows = entityManager
                .createNativeQuery(sql)
                .setParameter("embedding", embedding)
                .getResultList();

        List<EventDecision> results = new ArrayList<>();

        for (Object[] row : rows) {

            EventDecision e = new EventDecision();

            e.setEventId((String) row[1]);
            e.setMessage((String) row[2]);
            e.setSeverity((String) row[3]);
            e.setReason((String) row[4]);

            Timestamp ts = (Timestamp) row[5];
            if (ts != null) {
                e.setTimestamp(ts.toLocalDateTime());
            }

            results.add(e);
        }

        return results;
    }
}