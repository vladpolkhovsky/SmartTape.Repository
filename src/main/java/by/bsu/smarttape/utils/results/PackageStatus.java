package by.bsu.smarttape.utils.results;

import by.bsu.smarttape.models.Link;
import by.bsu.smarttape.models.Package;

public class PackageStatus implements SimpleStatus {

    private final int code;

    private final String message;
    private final Package aPackage;

    public PackageStatus(int code, String message, Package aPackage) {
        this.code = code;
        this.message = message;
        this.aPackage = aPackage;
    }

    public static PackageStatus createStatus(int code, String message, Package aPackage) {
        return new PackageStatus(code, message, aPackage);
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getMessage() {
        return null;
    }

    public int getCode() {
        return code;
    }

    public Package getaPackage() {
        return aPackage;
    }
}
