package com.example.application.data.service;

import com.example.application.data.entity.Log;
import com.example.application.data.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LogService {

    private static LogRepository repo;

    public LogService(LogRepository repo){
        this.repo = repo;
    }

    public LogRepository getRepo() {
        return repo;
    }

    public void setRepo(LogRepository repo) {
        this.repo = repo;
    }

    public static void log(Log log){

        repo.save(log);

    }

    public static void log(String userId, Log.OperationType type, Class relatedEntityClass, String relatedEntityId ){

        Log log = new Log();
        log.setUserId(userId);
        log.setOperationType(type.name());
        log.setRelatedEntityClass(relatedEntityClass.getName());
        log.setRelatedEntityId(relatedEntityId);

        log(log);

    }



}
