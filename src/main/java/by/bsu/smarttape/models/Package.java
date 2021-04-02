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

    public Package(List<Link> links, long ownerID) {
        this.links = links;
        this.ownerID = ownerID;
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

    @Column
    public long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }
}
