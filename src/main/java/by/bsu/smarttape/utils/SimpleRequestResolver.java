package by.bsu.smarttape.utils;

import by.bsu.smarttape.utils.presentation.Presentation;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleRequestResolver extends RequestResolver {

    public SimpleRequestResolver(Presentation presentation) {
        super(presentation);
    }

    @Override
    public void setPresentation(Presentation presentation) {
        super.setPresentation(presentation);
    }

    @Override
    public Presentation getPresentation() {
        return super.getPresentation();
    }

    @Override
    public String resolve(HttpServletRequest request) {
        ArrayList<String> lines = new ArrayList<>();
        request.getParameterMap().forEach((x, y) -> {
            StringBuilder builder = new StringBuilder();
            Arrays.stream(y).forEach((z) -> builder.append(z + " "));
            lines.add(x + " : " + builder.toString());
        });
        return getPresentation().apply(lines.toArray(new String[0]));
    }
}
