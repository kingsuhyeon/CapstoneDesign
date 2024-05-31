package goinmul.sportsmanage.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ground {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ground_id")
    private Long id;
    private String name;
    private String location;

    public static Ground createGround(String name, String location){
        Ground ground = new Ground();
        ground.setName(name);
        ground.setLocation(location);
        return ground;
    }


}
