package by.bsu.smarttape.utils.results;

public interface SimpleStatus {
    public static int OK = 0;
    public static int ERROR = 1;
    int getStatus();
    String getMessage();
}
