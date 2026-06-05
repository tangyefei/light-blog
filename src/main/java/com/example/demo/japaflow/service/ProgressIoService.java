package com.example.demo.japaflow.service;

import java.util.Map;

public interface ProgressIoService {
    /** Returns { exportedAt, lessons: { "lessonId": {...} } } */
    Map<String, Object> exportAll(Long userId);

    /** Imports a payload of { lessons: { "lessonId": {...} } } as full overwrite. Returns count of lessons. */
    int importAll(Long userId, Map<String, Object> payload);
}
