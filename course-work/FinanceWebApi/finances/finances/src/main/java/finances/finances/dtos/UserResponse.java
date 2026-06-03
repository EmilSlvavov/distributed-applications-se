package finances.finances.dtos;
import finances.finances.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "User details returned by the API")
public class UserResponse {

    @Schema(description = "User ID")
    private Integer id;

    @Schema(description = "User name")
    private String username;

    @Schema(description = "User role")
    private RoleType role;

    @Schema(description = "Whether the user account is active")
    private Boolean isActive;

    @Schema(description = "Timestamp when the user was created")
    private LocalDateTime createdAt;
}
