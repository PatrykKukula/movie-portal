package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Poster extends Image {
    private final IImageService avatarService;
    private final Long id;
    private final String dir;
    private static final String WIDTH = "210x";
    private static final String HEIGHT = "300px";

    public Poster(IImageService avatarService, Long id, String dir, String placeholderDir) throws IOException {
        this.avatarService = avatarService;
        this.id = id;
        this.dir = dir;
        StreamResource streamResource = avatarService.loadImage(id, dir, placeholderDir)
                .map(bytes -> new StreamResource(id + ".png", () -> new ByteArrayInputStream(bytes)))
                .orElse(null);

        setAlt("Poster");
        if (streamResource != null) {
            setSrc(streamResource);
            setWidth(WIDTH);
            setHeight(HEIGHT);
        }
        else {
            setWidth("24px");
            setHeight("24px");
        }
    }
}
