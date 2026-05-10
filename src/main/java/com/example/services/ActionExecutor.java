package com.example.services;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ActionExecutor {

    public void execute(String severity, String reason) {

        switch (severity) {

            case "HIGH":
                createIncident(reason);
                sendPagerAlert(reason);
                break;

            case "MEDIUM":
                sendSlackAlert(reason);
                break;

            case "LOW":
                logEvent(reason);
                break;
        }
    }

    private void createIncident(String reason) {
        log.info("[ACTION] Creating incident: " + reason);
    }

    private void sendPagerAlert(String reason) {
        log.info("[ACTION] Sending pager alert: " + reason);
    }

    private void sendSlackAlert(String reason) {
        log.info("[ACTION] Sending slack alert: " + reason);
    }

    private void logEvent(String reason) {
        log.info("[ACTION] Logging event: " + reason);
    }
}