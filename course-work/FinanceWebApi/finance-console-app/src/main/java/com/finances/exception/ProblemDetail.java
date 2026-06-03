package com.finances.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Matches the RFC 7807 Problem Details response from the backend GlobalExceptionHandler.
 * Example backend response:
 * {
 *   "type": "http://localhost:8080/errors/not-found",
 *   "title": "Not Found",
 *   "status": 404,
 *   "detail": "Budget not found with id: 5",
 *   "instance": "/api/budgets/5",
 *   "timestamp": "2024-06-01T12:30:00Z"
 * }
 */
@Data
@NoArgsConstructor
public class ProblemDetail {
    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String instance;
}