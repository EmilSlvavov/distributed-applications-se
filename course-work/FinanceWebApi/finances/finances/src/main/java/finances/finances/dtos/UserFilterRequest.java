package finances.finances.dtos;
import lombok.Data;
@Data
public class UserFilterRequest {
    private String role;
    private Boolean isActive;
    private String username;

    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}