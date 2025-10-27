package fashionmanager.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InfluencerPageResponseDTO {

    private Integer num;
    private String title;
    private String content;
    private String insta;
    private String phone;
    private Integer memberNum;

    // 칭호 좋아요 힘내요 추가
    private Integer likes;
    private Integer cheers;
    private List<String> badges;

    private String memberName;


    //  이미지 관련 코드
    private List<String> photoPaths;

}
