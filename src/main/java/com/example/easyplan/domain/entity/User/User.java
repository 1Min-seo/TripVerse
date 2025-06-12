package com.example.easyplan.domain.entity.User;

import com.example.easyplan.domain.Role;

import com.example.easyplan.domain.entity.Heart.Heart;
import com.example.easyplan.domain.entity.Review.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "user_key", nullable = false, unique = true)
    private String userKey;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

//    private String accessToken;
//
//    public void setAccessToken(String accessToken) {
//        this.accessToken = accessToken;
//    }

    @Builder
    public User(String name, String userKey, String email, String profileImageUrl, Role role) {
        this.name = name;
        this.userKey = userKey;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Heart> hearts = new ArrayList<>();

    public User update(String name) {
        this.name = name;
        return this;
    }


    public List<Review> getLikedReviews() {
        return this.hearts.stream()
                .map(Heart::getReview)
                .collect(Collectors.toList());
    }


    //사용자의 고유한 식별자
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return null;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collectors = new ArrayList<>();
        collectors.add(new SimpleGrantedAuthority(role.toString()));
        return collectors;
    }

//    public void setUsername(String username) {
//        this.username = username;
//    }
}
