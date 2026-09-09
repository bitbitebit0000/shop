package shopping.shop.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue @Column(name = "member_id")
    private Long id;
    // private String name;
    private String name;
    @Column(unique = true, nullable = false) // 중복 불가 설정
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();



}
