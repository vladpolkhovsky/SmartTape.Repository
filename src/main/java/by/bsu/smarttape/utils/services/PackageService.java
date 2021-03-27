package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.models.Package;
import by.bsu.smarttape.utils.results.PackageStatus;

public interface PackageService {
    PackageStatus getPackage(long id);
    PackageStatus setPackage(long id);
    PackageStatus deletePackage(long id);
}
