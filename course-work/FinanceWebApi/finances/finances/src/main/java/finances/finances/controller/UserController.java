package finances.finances.controller;
import finances.finances.domain.user.service.UserService;
import finances.finances.dtos.ChangePasswordRequest;
import finances.finances.dtos.UserFilterRequest;
import finances.finances.dtos.UserRequest;
import finances.finances.dtos.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Manage application users")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponse(responseCode = "200", description = "List of users returned successfully")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@ModelAttribute UserFilterRequest filter) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userService.findAll(filter).get());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponse> getUserById(
        @Parameter(description = "User ID", required = true) @PathVariable Integer id) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userService.findById(id).get());
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request).get());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponse> updateUser(
        @Parameter(description = "User ID", required = true) @PathVariable Integer id,
        @Valid @RequestBody UserRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userService.update(id, request).get());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "User ID", required = true) @PathVariable Integer id)
        throws ExecutionException, InterruptedException {
        userService.delete(id).get();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/change-password")
    @Operation(summary = "Change password", description = "Verifies the current password then sets the new one")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
        @ApiResponse(responseCode = "401", description = "Current password is incorrect", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponse> changePassword(
        @Parameter(description = "User ID", required = true) @PathVariable Integer id,
        @Valid @RequestBody ChangePasswordRequest request)
        throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userService.changePassword(id, request).get());
    }
}