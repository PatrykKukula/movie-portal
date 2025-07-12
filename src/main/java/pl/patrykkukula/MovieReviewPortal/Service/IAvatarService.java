package pl.patrykkukula.MovieReviewPortal.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface IAvatarService {
    void saveAvatar(String userId, String mimeType, InputStream uploadStream, int target) throws IOException;
    Optional<byte[]> loadAvatar(Long userId);
}
