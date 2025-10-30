package fashionmanager.develop.dto;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private int memberNum;
    private String memberId;
    private String memberPwd;
    private String memberEmail;
    private String memberName;
    private int memberAge;
    private String memberGender;
    private int memberHeight;
    private int memberWeight;
    private String memberStatus;
    private int memberReportCount;
    private int memberDailyReportCount;
    private int memberGoodCount;
    private int memberMonthlyGoodCount;
    private int memberCheerCount;
    private boolean memberMessageAllow;

    // 새로 추가된 DB 컬럼(전화번호, 주소)
    private String memberPhone;
    private String memberAddress;



}
