package com.finances.exception;

/**
 * Thrown by ApiClient when the backend returns a non-2xx response.
 * Wraps the RFC 7807 ProblemDetail from the backend.
 * Extends RuntimeException so it propagates through service methods
 * without needing to be declared in method signatures.
 */
public class ApiException extends RuntimeException {

    private final ProblemDetail problemDetail;
    private final int statusCode;

    public ApiException(ProblemDetail problemDetail) {
        super(problemDetail.getDetail() != null ? problemDetail.getDetail() : problemDetail.getTitle());
        this.problemDetail = problemDetail;
        this.statusCode = problemDetail.getStatus() != null ? problemDetail.getStatus() : 0;
    }

    // Fallback constructor when the error body isn't valid RFC 7807
    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.problemDetail = new ProblemDetail();
        this.problemDetail.setStatus(statusCode);
        this.problemDetail.setDetail(message);
        this.problemDetail.setTitle(message);
    }

    public ProblemDetail getProblemDetail() {
        return problemDetail;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the human-readable detail message from the backend.
     * This is what should be shown to the user.
     */
    public String getDetail() {
        return problemDetail.getDetail() != null
            ? problemDetail.getDetail()
            : problemDetail.getTitle();
    }

    public boolean isNotFound() {
        return statusCode == 404;
    }

    public boolean isForbidden() {
        return statusCode == 403;
    }

    public boolean isUnauthorized() {
        return statusCode == 401;
    }

    public boolean isConflict() {
        return statusCode == 409;
    }

    public boolean isValidationError() {
        return statusCode == 400;
    }
}