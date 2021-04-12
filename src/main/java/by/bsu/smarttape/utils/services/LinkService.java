package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.models.Link;
import org.springframework.stereotype.Service;
import by.bsu.smarttape.models.Package;

import java.util.List;

@Service
public class LinkService {

    private Package aPackage;

    public List<Link> showAll(){
        return this.aPackage.getLinks();
    }
}
