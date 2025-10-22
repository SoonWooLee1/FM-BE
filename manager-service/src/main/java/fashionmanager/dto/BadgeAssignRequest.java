package fashionmanager.dto;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BadgeAssignRequest {
    private int userNum;
    private int badgeNum;
}