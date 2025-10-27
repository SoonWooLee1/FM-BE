package fashionmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fashion_post")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FashionPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num")
    private int num;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "good")
    private int good;

    @Column(name = "cheer")
    private int cheer;

    @Column(name = "temp")
    private double temp;

    @Column(name = "member_num")
    private int memberNum;
}
