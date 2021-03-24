package by.bsu.smarttape.utils.presentation;

import by.bsu.smarttape.utils.results.UserRegistrationStatus;

public class UserRegistrationStatusPresentation implements Presentation{

    private UserRegistrationStatus status;
    private String CSSName;

    public UserRegistrationStatusPresentation(UserRegistrationStatus status) {
        this(status, null);
    }

    public UserRegistrationStatusPresentation(UserRegistrationStatus status, String CSSClassNames) {
        this.status = status;
        this.CSSName = CSSClassNames;
    }

    @Override
    public String getHtml() {
        return process(status);
    }

    private String process(UserRegistrationStatus status) {
        StringBuilder builder = new StringBuilder();
        builder.append("<div class=\"").append(CSSName == null ? "" : CSSName).append("\">\n");
            builder.append("<h3>Code : ").append(status.getStatus()).append("</h3>");
            builder.append("<p>With message : ").append(status.getMessage()).append("</p>");
        builder.append("</div>");
        return builder.toString();
    }
}
