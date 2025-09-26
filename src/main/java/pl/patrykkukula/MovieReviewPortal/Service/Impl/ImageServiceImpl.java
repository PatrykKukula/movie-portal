package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final List<String> ALLOWED_FORMATS = List.of(".png", ".jpg", ".jpeg");
    private static final String METADATA_FILE = "metadata.json";

    @Value("${image.base-dir:/opt/app/images}")
    private String baseImageDir;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    @CacheEvict(value = "image", key = "#id + '_' + #imageDir")
    public void saveImage(String id, int maxBytes, String mimeType, String imageDir, InputStream uploadStream) {
        try (InputStream input = limitSize(uploadStream, maxBytes);
        BufferedInputStream buffered = new BufferedInputStream(input)){
            BufferedImage original = ImageIO.read(buffered);
            if (original == null) throw new IllegalArgumentException("Invalid file");

            Path dirPath = Paths.get(baseImageDir, imageDir);
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
    public Optional<byte[]> loadImage(Long id, String imageDir, String placeholderResource) throws IOException {
        Path dirPath = Paths.get(baseImageDir, imageDir);
        Path metadataPath = dirPath.resolve(METADATA_FILE);

        log.info("Try to read image on path:{} and path is:{} ", metadataPath, Files.exists(metadataPath));

        if (Files.exists(metadataPath)) {
            try {
                Map<String, String> map = mapper.readValue(metadataPath.toFile(), new TypeReference<>() {});
                Path file = dirPath.resolve(id + "." + map.get(String.valueOf(id)));
                if (Files.exists(file)) {
                    return Optional.of(Files.readAllBytes(file));
                }
            } catch (IOException | InvalidPathException ex) {
                log.warn("Failed to read image metadata", ex);
            }
        }

        Path placeholderPath = Paths.get(baseImageDir, placeholderResource);
        if (Files.exists(placeholderPath)) {
            return Optional.of(Files.readAllBytes(placeholderPath));
        } else {
            log.warn("Placeholder not found: {}", placeholderPath);
        }

        return Optional.empty();
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

    private static final List<String> FOLDERS = List.of("MoviePoster", "ActorPoster", "DirectorPoster", "avatars");

    @PostConstruct
    public void copyInitialPosters() {
        for (String folder : FOLDERS) {
            copyFolderFromResources(folder);
        }
    }

    private void copyFolderFromResources(String folderName) {
        try {
            Path targetDir = Paths.get(baseImageDir, folderName);
            Files.createDirectories(targetDir);

            try (InputStream listStream = getClass().getResourceAsStream("/" + folderName + "/file-list.txt")) {
                if (listStream == null) {
                    log.warn("file-list.txt not found in Resources/{}", folderName);
                    return;
                }

                List<String> files = new BufferedReader(new InputStreamReader(listStream))
                        .lines()
                        .filter(line -> !line.isBlank())
                        .toList();

                for (String fileName : files) {
                    try (InputStream is = getClass().getResourceAsStream("/" + folderName + "/" + fileName)) {
                        if (is != null) {
                            Path targetFile = targetDir.resolve(fileName);
                            Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            log.warn("File not found in Resources: {}/{}", folderName, fileName);
                        }
                    }
                }
            }

        } catch (IOException e) {
            log.error("Error copying folder {} from resources to {}", folderName, baseImageDir, e);
        }
    }
}
