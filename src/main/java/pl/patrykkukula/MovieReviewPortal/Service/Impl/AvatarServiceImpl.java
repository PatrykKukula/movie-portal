package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Service.IAvatarService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements IAvatarService {
    private static final long MAX_BYTES = 2 * 1024 * 1024;
    private final Path avatarsDir = Paths.get("avatars");

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void saveAvatar(String userId, String MIMEType, InputStream uploadStream, int target) {
        try (InputStream input = limitSize(uploadStream);
        BufferedInputStream buffered = new BufferedInputStream(input)){
            BufferedImage original = ImageIO.read(buffered);
            if (original == null) throw new IllegalArgumentException("Invalid file");

            BufferedImage scaled = resizeKeepingRatio(original, target);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(scaled, MIMEType, out);

            Path avatarPath = avatarsDir.resolve("%s.png".formatted(userId));
            Files.createDirectories(avatarsDir);
            Files.write(avatarPath, out.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException ex){
            throw new RuntimeException("Saving avatar failed: ", ex);
        }

    }
    @Override
    public Optional<byte[]> loadAvatar(Long userId) {
        Path file = avatarsDir.resolve("%s.png".formatted(userId));
        try {
            return Files.exists(file) ? Optional.of(Files.readAllBytes(file)) : Optional.empty();
        }
        catch (IOException ex) {
            return Optional.empty();
        }
    }
    private InputStream limitSize(InputStream original) throws IOException {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        original.transferTo(temp);
        if (temp.size() > MAX_BYTES) throw new IllegalArgumentException("File must be less than or equal to " + MAX_BYTES);
        return new ByteArrayInputStream(temp.toByteArray());
    }
    private BufferedImage resizeKeepingRatio(BufferedImage src, int target){
        int width = src.getWidth();
        int height = src.getHeight();
        if (width <= target && height <= target){
            return src;
        }
        double scale = width > height ? (double) target / width : (double) target / height;
        int newW = (int) (width * scale);
        int newH = (int) (height * scale);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = resized.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.drawImage(src, 0, 0, newW, newH, null);
        graphics.dispose();
        return resized;
    }
}
