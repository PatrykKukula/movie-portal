package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final List<String> ALLOWED_FORMATS = List.of(".png", ".jpg", ".jpeg");
    private static final String METADATA_FILE = "metadata.json";

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public void saveImage(String id, int maxBytes, String mimeType, String imageDir, InputStream uploadStream) {
        try (InputStream input = limitSize(uploadStream, maxBytes);
        BufferedInputStream buffered = new BufferedInputStream(input)){
            BufferedImage original = ImageIO.read(buffered);
            if (original == null) throw new IllegalArgumentException("Invalid file");

            Path dirPath = Paths.get(imageDir);
            Files.createDirectories(dirPath);

            removeImages(id, dirPath);
            Path imagePath = dirPath.resolve((("%s.") + mimeType).formatted(id));
            ImageIO.write(original, mimeType, imagePath.toFile());

            Path formatPath = dirPath.resolve(METADATA_FILE);
            HashMap<String, String> map = new HashMap<>();
            if (!Files.exists(formatPath)) Files.createFile(formatPath);
            try (InputStream fileInputStream = new FileInputStream(formatPath.toFile())) {
                try {
                     map = mapper.readValue(fileInputStream, new TypeReference<>() {
                    });
                }
                catch (MismatchedInputException ex){
                    mapper.writerWithDefaultPrettyPrinter().writeValue(formatPath.toFile(), map);
                }
                map.put(id, mimeType);
                mapper.writerWithDefaultPrettyPrinter().writeValue(formatPath.toFile(), map);
            }
        }
        catch (IOException ex){
            throw new RuntimeException("Saving image failed: ", ex);
        }
    }
    @Override
    @Cacheable(value = "image", key = "#id + '_' + #imageDir")
    public Optional<byte[]> loadImage(Long id, String imageDir, String placeholderDir) throws IOException {
        Path dirPath = Paths.get(imageDir);
        Path formatPath = dirPath.resolve(METADATA_FILE);

        if (!Files.exists(formatPath)) return Optional.empty();
        try {
            Map<String, String> map = mapper.readValue(formatPath.toFile(), new TypeReference<>() {
            });
            Path file = dirPath.resolve(id + "." + map.get(String.valueOf(id)));
            return Files.exists(file) ? Optional.of(Files.readAllBytes(file)) :
                    Optional.of(Files.readAllBytes(Paths.get(placeholderDir)));
        }
        catch (InvalidPathException ex) {
            return Optional.of(Files.readAllBytes(Paths.get(placeholderDir)));
        }
        catch (IOException | InputMismatchException ex) {
            return Optional.empty();
        }
    }
    private InputStream limitSize(InputStream original, int maxBytes) throws IOException {
        byte[] bytes = original.readAllBytes();
        if (bytes.length > maxBytes) throw new IllegalArgumentException("File must be less than or equal to " + maxBytes);
        return new ByteArrayInputStream(bytes);
    }
    private void removeImages(String id, Path dir){
        for (String format : ALLOWED_FORMATS) {
            File image = dir.resolve(("%s%s").formatted(id, format)).toFile();
            if (image.exists()) image.delete();
        }
    }
}
