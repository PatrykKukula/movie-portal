package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Image;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Poster extends Image {
    private final IImageService avatarService;
    private final Long id;
    private final String dir;

    public Poster(IImageService imageService, Long id, String dir, String placeholderDir, String width, String height) throws IOException {
        this.avatarService = imageService;
        this.id = id;
        this.dir = dir;
        StreamResource streamResource = imageService.loadImage(id, dir, placeholderDir)
                .map(bytes -> new StreamResource(id + ".png", () -> new ByteArrayInputStream(bytes)))
                .orElse(null);

        setAlt("Poster");
        if (streamResource != null) {
            setSrc(streamResource);
            setWidth(width);
            setHeight(height);
            getStyle().set("border-radius", "16px");
        }
        else {
            setWidth("24px");
            setHeight("24px");
        }
    }
}
