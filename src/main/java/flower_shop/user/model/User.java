package flower_shop.user.model;


import jakarta.persistence.*;
import flower_shop.address.model.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

//    @Column(nullable = false)
//    private String phoneNumber;

//    @Enumerated(EnumType.STRING)
//    @Column
//    private Title title;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
//    private List<Address> addresses = new ArrayList<>();
}
