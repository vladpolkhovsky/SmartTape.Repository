package by.bsu.smarttape.utils.presentation;

public class SimplePresentation implements Presentation {
    @Override
    public String apply(String[] lines) {
        StringBuilder builder = new StringBuilder();
        builder.append("SimplePresentation of args in url:<br>\n");
        if (lines.length > 0) {
            builder.append("<ul>\n");
            for (String line : lines)
                builder.append("<li>").append(line).append("</li>\n");
            builder.append("</ul>");
        } else {
            builder.append("<p>").append("<i>no args</i>").append("</p>\n");
        }
        return builder.toString();
    }
}
