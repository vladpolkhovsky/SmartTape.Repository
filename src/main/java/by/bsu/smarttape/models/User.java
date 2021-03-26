package by.bsu.smarttape.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "smart_tape_users")
public class User implements Serializable {

    private Long id;
    private String email;
    private String userName;
    private String password;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }

    @Column(length = 30, updatable = true, unique = false, nullable = false)
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
