package fashionmanager.develop.dto;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    private int userNum;
    private String userId;
    private String userPwd;
    private String userEmail;
    private String userName;
    private int userAge;
    private String userGender;
    private int userMessageAllow;
    private int ReportCount;
    private int DailyReportCount;

    //  새 필드들
    private String userPhone;
    private String userAddress;
    private int userHeight;
    private int userWeight;


    private List<BadgeDTO> badges = new ArrayList<>();



}
