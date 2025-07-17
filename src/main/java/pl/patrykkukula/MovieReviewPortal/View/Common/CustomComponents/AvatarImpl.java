package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.StreamResource;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AvatarImpl extends Div {

    private final Avatar avatar = new Avatar();

    public AvatarImpl(IImageService avatarService, UserDetailsServiceImpl userDetails, String width, String height, String dir, String placeholderDir) throws IOException {
        Long userId = userDetails.getAuthenticatedUserId();
        if (userId != null) {
            StreamResource streamResource = avatarService.loadImage(userId, dir, placeholderDir)
                    .map(data -> new StreamResource(userId + ".png", () -> new ByteArrayInputStream(data)))
                    .orElse(null);
            avatar.setHeight(width);
            avatar.setWidth(height);
            if (streamResource != null) {
                avatar.setImageResource(streamResource);
            }
        }
        add(avatar);
    }
}
