package pl.patrykkukula.MovieReviewPortal.View.Common;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.StreamResource;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IAvatarService;

import java.io.ByteArrayInputStream;

public class AvatarImpl extends Div {

    private final Avatar avatar = new Avatar();

    public AvatarImpl(IAvatarService avatarService, UserDetailsServiceImpl userDetails, String width, String height) {
        Long userId = userDetails.getAuthenticatedUserId();
        if (userId != null) {
            StreamResource streamResource = avatarService.loadAvatar(userId)
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
