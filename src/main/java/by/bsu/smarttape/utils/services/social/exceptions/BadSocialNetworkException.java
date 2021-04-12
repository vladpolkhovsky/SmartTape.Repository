package by.bsu.smarttape.utils.services.social.exceptions;

public class BadSocialNetworkException extends ParserException {
    public BadSocialNetworkException(String url, String socialNetwork) {
        super(String.format("Неправильная ссылка: '%s' для сервиса %s.", url, socialNetwork));
    }
}
