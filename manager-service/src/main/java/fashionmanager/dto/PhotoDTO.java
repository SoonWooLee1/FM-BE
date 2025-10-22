package fashionmanager.dto;

import lombok.Data;

@Data
public class PhotoDTO {
    private int num;
    private String name;
    private String path;
    private String imageUrl;
    private int photoCategoryNum;
}
