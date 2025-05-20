package com.globallogic.microservice1.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Usuario {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_phones",
            joinColumns = @JoinColumn(name = "usuario_id")
    )
    @BatchSize(size = 20)
    private Set<Telefono> telefonos = new HashSet<>();


    public Usuario() {
        this.created = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
    }


}
