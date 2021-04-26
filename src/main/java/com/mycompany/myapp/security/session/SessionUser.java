package com.mycompany.myapp.security.session;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.User;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

public class SessionUser {

    public static final String SESSION_USER_KEY = "SESSION_USER";

    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    Set<Authority> authorities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public static SessionUser mapUserToSessionUser(User user) {
        if (user == null) {
            return null;
        }
        SessionUser sessionUser = new SessionUser();
        sessionUser.setId(user.getId());
        sessionUser.setLogin(user.getLogin());
        sessionUser.setFirstName(user.getFirstName());
        sessionUser.setLastName(user.getLastName());
        sessionUser.setAuthorities(user.getAuthorities());
        return sessionUser;
    }
}
