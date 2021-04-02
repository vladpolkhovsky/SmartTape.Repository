package by.bsu.smarttape.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

enum SocialNetwork {
    vk, inst, tweet
};

@Entity
@Table(name = "smart_tape_package_link")
public class Link {
    private long id;
    private long packageId;
    private String urlAddress;

    public Link(String url, long packageId) {
        this.packageId = packageId;
        this.urlAddress = url;
    }

    public Link() {

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

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public long getPackageId() {
        return packageId;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    @Transient
    public SocialNetwork getSocialNetwork() {
        return SocialNetwork.vk;
    }

}
