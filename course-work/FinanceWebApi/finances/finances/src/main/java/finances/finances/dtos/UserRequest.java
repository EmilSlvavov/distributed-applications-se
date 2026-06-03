package finances.finances.dtos;
import finances.finances.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Payload for creating or updating a user")
public class UserRequest {

    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "Raw password (will be encoded)", example = "Secret123!")
    private String password;

    @NotBlank(message = "Username is required")
    @Schema(description = "Unique username", example = "john_doe")
    private String username;

    @NotNull(message = "Role is required")
    private RoleType role;
}
