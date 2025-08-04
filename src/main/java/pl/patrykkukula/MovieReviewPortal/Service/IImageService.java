package pl.patrykkukula.MovieReviewPortal.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface IImageService {
    void saveImage(String id, int maxBytes, String mimeType, String dir, InputStream uploadStream) throws IOException;
    Optional<byte[]> loadImage(Long id, String dir, String placeholderDir) throws IOException;
}
