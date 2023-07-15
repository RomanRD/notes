package com.service.notes.persistence.entity;

import com.service.notes.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private String firstName;
    private String secondName;
    private List<Role> roles;

    public User(String username, String password, String firstName, String secondName, List<Role> roles){
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
