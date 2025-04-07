package com.example.blog_post_manager.user.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole name;

    @ManyToMany(
            mappedBy = "roles",
            fetch = FetchType.LAZY
    )
    private Set<User> users = new HashSet<>();

    public Role() {
    }

    public Role(UserRole name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public UserRole getName() {
        return name;
    }

    public void setName(UserRole name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;
        return name == role.name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
