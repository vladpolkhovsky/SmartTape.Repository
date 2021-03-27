package by.bsu.smarttape.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "smart_tape_users")
public class User implements Serializable {

    private Long id;
    private String email;
    private String userName;
    private String password;

    @Transient
    private transient long sessionStart;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }

    @Column(length = 30, updatable = true, unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    @Column(length = 30, updatable = true, unique = false, nullable = false)
    public String getUserName() {
        return userName;
    }

    @Column(length = 30, updatable = true, unique = false, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Transient
    public void setSessionStart(long sessionStart) {
        this.sessionStart = sessionStart;
    }

    @Transient
    public long getSessionStart() {
        return sessionStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(userName, user.userName) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, userName, password);
    }

}
