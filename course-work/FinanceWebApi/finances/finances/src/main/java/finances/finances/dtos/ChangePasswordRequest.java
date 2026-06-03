package finances.finances.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Payload for changing a user's password")
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Schema(description = "The user's current password", example = "Secret123!")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    @Schema(description = "The new password to set", example = "Secret321!")
    private String newPassword;
}
