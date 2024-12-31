package org.example.secureplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class MyTUserDetail implements UserDetails {
    private User User;

    @JsonIgnore  //json忽略
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.getUser().getPassword();
    }
    @JsonIgnore
    @Override
    public String getUsername() {
        return this.getUser().getUsername();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return this.getUser().getStatus()==0;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return this.getUser().getStatus()==0;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return this.getUser().getStatus()==0;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return this.getUser().getStatus()==0;
    }
}
