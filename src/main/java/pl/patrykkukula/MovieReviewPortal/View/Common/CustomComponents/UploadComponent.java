package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;

import java.io.IOException;

public class UploadComponent extends Upload {
    MemoryBuffer buffer = new MemoryBuffer();

    /*
        Custom upload component to add image files
     */
    public UploadComponent(String maxSizeMb, int maxSizeBytes, String allowedFormat,
                           String[] allowedTypes, Long entityId, String dir, IImageService imageService) {
        UploadI18N I18N = new UploadI18N();
        UploadI18N.Error error = new UploadI18N.Error();
        error.setFileIsTooBig(maxSizeMb);
        error.setIncorrectFileType("Allowed file types: " + allowedFormat);
        I18N.setError(error);
        setReceiver(buffer);

        setI18n(I18N);
        setAcceptedFileTypes(allowedTypes);
        setMaxFileSize(maxSizeBytes);
        addSucceededListener(e -> {
            String id = String.valueOf(entityId);
            try {
                imageService.saveImage(id, maxSizeBytes, e.getMIMEType().substring(6), dir, buffer.getInputStream());
            } catch (IOException ex) {
                String errorMessage = ex.getMessage();
                Notification notification = Notification.show(errorMessage, 2500,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        addFileRejectedListener(e -> {
            String errorMessage = e.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 2500,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
    }
}
