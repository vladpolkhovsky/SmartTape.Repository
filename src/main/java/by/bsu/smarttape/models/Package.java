package by.bsu.smarttape.models;

import by.bsu.smarttape.utils.results.PackageStatus;
import by.bsu.smarttape.utils.services.PackageService;

import java.util.List;

public class Package implements PackageService {
    private int id;
    private List<Link> links;
    private int ownerID;

    public int getId() {
        return id;
    }

    public List<Link> getLinks() {
        return links;
    }

    public int getOwnerID() {
        return ownerID;
    }

    @Override
    public PackageStatus getPackage(long id) {
        return null;
    }

    @Override
    public PackageStatus savePackage(long id) {
        return null;
    }

    @Override
    public PackageStatus deletePackage(long id) {
        return null;
    }

    @Override
    public PackageStatus subPackage(long packageID, long userID) {
        return null;
    }
}
