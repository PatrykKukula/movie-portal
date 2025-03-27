package pl.patrykkukula.MovieReviewPortal.Utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class ControllerUtils {

    public static URI setUri(Long id, String endpoint){
       return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(endpoint + "/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
