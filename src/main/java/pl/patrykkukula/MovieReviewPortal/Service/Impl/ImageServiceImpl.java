package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void saveImage(String id, int maxBytes, String mimeType, String dir, int maxSize, InputStream uploadStream) {
        try (InputStream input = limitSize(uploadStream, maxBytes);
        BufferedInputStream buffered = new BufferedInputStream(input)){
            BufferedImage original = ImageIO.read(buffered);
            if (original == null) throw new IllegalArgumentException("Invalid file");

            Path dirPath = Paths.get(dir);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(original, mimeType, out);

            Path imagePath = dirPath.resolve(("%s.png").formatted(id));
            Files.createDirectories(dirPath);
            Files.write(imagePath, out.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException ex){
            throw new RuntimeException("Saving avatar failed: ", ex);
        }
    }
    @Override
    public Optional<byte[]> loadImage(Long id, String dir) {
        Path dirPath = Paths.get(dir);
        Path file = dirPath.resolve("%s.png".formatted(id));
        try {
            return Files.exists(file) ? Optional.of(Files.readAllBytes(file)) : Optional.empty();
        }
        catch (IOException ex) {
            return Optional.empty();
        }
    }
    private InputStream limitSize(InputStream original, int maxBytes) throws IOException {
        byte[] bytes = original.readAllBytes();
        if (bytes.length > maxBytes) throw new IllegalArgumentException("File must be less than or equal to " + maxBytes);
        return new ByteArrayInputStream(bytes);
    }
//    private BufferedImage resizeKeepingRatio(BufferedImage src, int maxSize){
//        int width = src.getWidth();
//        int height = src.getHeight();
//        if (width <= maxSize && height <= maxSize){
//            return src;
//        }
//        double scale = width > height ? (double) width/maxSize : (double) height/maxSize;
//        int newW = (int) (width * scale);
//        int newH = (int) (height * scale);
//        int imageType = src.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
//        BufferedImage resized = new BufferedImage(newW, newH, imageType);
//        Graphics2D graphics = resized.createGraphics();
//        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        graphics.drawImage(src, 0, 0, newW, newH, null);
//        graphics.dispose();
//        return resized;
//    }
}
