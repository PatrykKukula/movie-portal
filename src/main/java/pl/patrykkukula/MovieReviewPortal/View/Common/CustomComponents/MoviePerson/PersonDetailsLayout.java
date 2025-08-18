package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Dto.ViewableEntityWithMovies;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Rating.RatingStarsLayout;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.TopicSectionLayout;
import pl.patrykkukula.MovieReviewPortal.View.Director.DirectorEditView;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.INITIAL_PAGE;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.PAGE_SIZE;

/*
    Layout for view for person entity details
 */
@CssImport("./styles/common-styles.css")
public class PersonDetailsLayout<T extends ViewableEntityWithMovies> extends VerticalLayout {

    public PersonDetailsLayout(Function<Long, T> fetchPerson, Long personId, BeforeEvent event, UserDetailsServiceImpl userDetailsService,
                               UserServiceImpl userService, BiFunction<Long, Long, RateDto> fetchRate, Function<RateDto, RatingResult> addRate, Function<Long, Double> removeRate,
                               String entityType, Consumer<Long> removeEntity, Class<? extends VerticalLayout> viewClass,
                               ImageServiceImpl imageService, String posterDir, String posterPh, TopicServiceImpl topicService) {
        addClassName("main-layout");
        setAlignItems(Alignment.CENTER);

        try {
            T entity = fetchPerson.apply(personId);
            Long entityId = entity.getId();

            PersonDetails personDetails = new PersonDetails(
                    entity.getCountry(),
                    entity.getDateOfBirth().toString()
            );

            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto rateDto = fetchRate.apply(entityId, userId);

            RatingStarsLayout ratingStarsLayout = new RatingStarsLayout(
                    entity.getRating(),
                    entity.getRateNumber(),
                    userDetailsService,
                    entity.getId(),
                    rateDto,
                    (newRate, id) -> addRate.apply(new RateDto(newRate, id)),
                    id -> removeRate.apply(entityId)
            );

            Button editButton = Buttons.editButton(DirectorEditView.class, "Edit director", entityId);
            Button deleteButton = new Button(entityType.toLowerCase(), e -> CommonComponents.confirmDelete(
                    entityId, entityType, y -> removeEntity.accept(y), viewClass));
            Button backButton = Buttons.backButton(viewClass, "Back to %ss".formatted(entityType.toLowerCase()));

            MoviePersonEntityLayout moviePersonEntityLayout = new MoviePersonEntityLayout(
                    userDetailsService, imageService, editButton, deleteButton, backButton,
                    personDetails, ratingStarsLayout, entityId, posterDir, posterPh,
                    entity.getFirstName(), entity.getLastName(), entity.getBiography(), entity.getMovies()
            );

            TopicSectionLayout topicSectionLayout = new TopicSectionLayout(
                    topicService, userDetailsService, userService, entityId, INITIAL_PAGE, PAGE_SIZE, "ASC", entityType.toLowerCase()
            );

            add(moviePersonEntityLayout, topicSectionLayout);


        } catch (ResourceNotFoundException ex) {
            event.rerouteToError(ResourceNotFoundException.class, ex.getMessage());
        } catch (InvalidIdException ex) {
            event.rerouteToError(InvalidIdException.class, ex.getMessage());
        } catch (IOException ex) {
            event.rerouteToError(IOException.class, ex.getMessage());
        }
    }
}

