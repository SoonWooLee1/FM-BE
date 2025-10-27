package fashionmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fashion_item")
@Getter
@Setter
@NoArgsConstructor
public class FashionItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num")
    private int num;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "link")
    private String link;
}
