package by.bsu.smarttape.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "smart_tape_package")
public class Package {
    private long id;

    private transient List<Link> links;

    private long ownerID;

    private String name;

    public Package(List<Link> links, long ownerID, String name) {
        this.links = links;
        this.ownerID = ownerID;
        this.name = name;
    }

    public Package() {

    }

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Transient
    public List<Link> getLinks() {
        return links;
    }

    @Transient
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Column(unique = false)
    public long getOwnerID() {
        return ownerID;
    }

    @Column(unique = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }
}
