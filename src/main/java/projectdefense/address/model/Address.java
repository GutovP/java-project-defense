package projectdefense.address.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import projectdefense.user.model.User;

import java.util.UUID;

@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String zipCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country country;

    @ManyToOne
    private User user;

}
