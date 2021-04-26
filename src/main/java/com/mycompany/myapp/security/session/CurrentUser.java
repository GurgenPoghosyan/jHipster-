package com.mycompany.myapp.security.session;

import com.mycompany.myapp.domain.User;
import lombok.Getter;
import lombok.Setter;

public class CurrentUser {

    private SessionUser sessionUser;

    public CurrentUser(User user) {
        this.sessionUser = SessionUser.mapUserToSessionUser(user);
    }

    public SessionUser getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(SessionUser sessionUser) {
        this.sessionUser = sessionUser;
    }
}
