package finances.finances.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Base URI for error type identifiers — follows RFC 7807 convention
    private static final String ERROR_BASE_URI = "http://localhost:8080/errors/";

    // ── 400 Validation errors (@Valid failures) ───────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
        MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create(ERROR_BASE_URI + "validation-failed"));
        problem.setTitle("Validation Failed");
        problem.setDetail("One or more fields failed validation.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errors", fieldErrors);         // field-level errors map
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problem);
    }

    // ── 400 Wrong type in path variable or request param ─────────────────────
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String detail = String.format("Parameter '%s' should be of type '%s'",
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create(ERROR_BASE_URI + "invalid-parameter"));
        problem.setTitle("Invalid Parameter");
        problem.setDetail(detail);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(problem);
    }

    // ── 401 Authentication failed ─────────────────────────────────────────────
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(
        AuthenticationException ex, HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setType(URI.create(ERROR_BASE_URI + "authentication-failed"));
        problem.setTitle("Authentication Failed");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    // ── 401 JWT expired ───────────────────────────────────────────────────────
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ProblemDetail> handleExpiredJwt(
        ExpiredJwtException ex, HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setType(URI.create(ERROR_BASE_URI + "token-expired"));
        problem.setTitle("Token Expired");
        problem.setDetail("Your session has expired. Please log in again.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    // ── 401 JWT malformed or invalid signature ────────────────────────────────
    @ExceptionHandler({MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<ProblemDetail> handleInvalidJwt(
        RuntimeException ex, HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setType(URI.create(ERROR_BASE_URI + "invalid-token"));
        problem.setTitle("Invalid Token");
        problem.setDetail("The provided token is invalid or has been tampered with.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    // ── 403 Access denied (wrong role or not owner) ───────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
        AccessDeniedException ex, HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setType(URI.create(ERROR_BASE_URI + "access-denied"));
        problem.setTitle("Access Denied");
        problem.setDetail("You do not have permission to perform this action.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    // ── 404 Endpoint not found ────────────────────────────────────────────────
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFound(
        NoResourceFoundException ex, HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setType(URI.create(ERROR_BASE_URI + "endpoint-not-found"));
        problem.setTitle("Endpoint Not Found");
        problem.setDetail("The requested endpoint does not exist.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // ── Any ResponseStatusException (404 not found, 403 forbidden etc.) ───────
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(
        ResponseStatusException ex, HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());
        problem.setType(URI.create(ERROR_BASE_URI + "error"));
        problem.setTitle(HttpStatus.resolve(ex.getStatusCode().value()) != null
            ? HttpStatus.resolve(ex.getStatusCode().value()).getReasonPhrase()
            : "Error");
        problem.setDetail(ex.getReason());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(ex.getStatusCode()).body(problem);
    }

    // ── Async wrapper — unwraps exceptions from @Async methods ────────────────
    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<ProblemDetail> handleExecutionException(
        ExecutionException ex, HttpServletRequest request) {

        Throwable cause = ex.getCause();

        if (cause instanceof ResponseStatusException rse) {
            ProblemDetail problem = ProblemDetail.forStatus(rse.getStatusCode());
            problem.setType(URI.create(ERROR_BASE_URI + "error"));
            problem.setTitle(HttpStatus.resolve(rse.getStatusCode().value()) != null
                ? HttpStatus.resolve(rse.getStatusCode().value()).getReasonPhrase()
                : "Error");
            problem.setDetail(rse.getReason());
            problem.setInstance(URI.create(request.getRequestURI()));
            problem.setProperty("timestamp", Instant.now());
            return ResponseEntity.status(rse.getStatusCode()).body(problem);
        }

        return buildInternalError(request);
    }

    // ── 500 Catch-all — anything not handled above ────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(
        Exception ex, HttpServletRequest request) {
        return buildInternalError(request);
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private ResponseEntity<ProblemDetail> buildInternalError(HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setType(URI.create(ERROR_BASE_URI + "internal-error"));
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred. Please try again later.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.internalServerError().body(problem);
    }
}