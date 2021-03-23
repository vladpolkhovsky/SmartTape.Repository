package by.bsu.smarttape.utils;

import by.bsu.smarttape.utils.presentation.Presentation;

import javax.servlet.http.HttpServletRequest;

public abstract class RequestResolver {

    private Presentation presentation;

    public RequestResolver(Presentation presentation) {
        this.presentation = presentation;
    }

    public void setPresentation(Presentation presentation) {
        this.presentation = presentation;
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public abstract String resolve(HttpServletRequest request);

}
