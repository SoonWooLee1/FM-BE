package fashionmanager.develop.dto;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InsertMemberDTO {
    private String memberId;
    private String memberPwd;
    private String memberEmail;
    private String memberName;
    private int memberAge;
    private String memberGender;
    private int memberHeight = 170;
    private int memberWeight = 60;
    private String memberPhone = "010-1234-5678";
    private String memberAddress = "서울시";
    private String memberStatus = "활동중";
    private boolean memberMessageAllow = true;
}
