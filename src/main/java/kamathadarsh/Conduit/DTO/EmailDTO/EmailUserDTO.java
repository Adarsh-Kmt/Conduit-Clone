package kamathadarsh.Conduit.DTO.EmailDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailUserDTO{

    private String username;
    private String emailId;
}
