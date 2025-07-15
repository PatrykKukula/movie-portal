package pl.patrykkukula.MovieReviewPortal.View.Common;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Poster extends Image {
    private final IImageService avatarService;
    private final Long id;
    private final String dir;
    private static final String WIDTH = "279px";
    private static final String HEIGHT = "397px";

    public Poster(IImageService avatarService, Long id, String dir) throws IOException {
        this.avatarService = avatarService;
        this.id = id;
        this.dir = dir;
        StreamResource streamResource = avatarService.loadImage(id, dir)
                .map(bytes -> new StreamResource(id + ".png", () -> new ByteArrayInputStream(bytes)))
                .orElse(null);

        setWidth(WIDTH);
        setHeight(HEIGHT);
        setAlt("Poster");
        if (streamResource != null) {
            setSrc(streamResource);
        }
    }
}
