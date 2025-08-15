package pl.patrykkukula.MovieReviewPortal.View.Movie;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;

import java.util.List;

public class CommonComponents {

    private CommonComponents() {}

    public static Icon closeIcon(ActorSummaryDto actor, Div currentActor, List<ActorSummaryDto> actors, VerticalLayout pickedActors){
        Icon closeIcon = VaadinIcon.CLOSE.create();
        closeIcon.addClassName("close-icon");
        closeIcon.setSize("0.8rem");
        closeIcon.addClickListener(ev -> {
            pickedActors.remove(currentActor);
            actors.remove(actor);
        });
        return closeIcon;
    }
    public static Div addedActor(String fullName){
        Div addedActor = new Div();
        addedActor.setClassName("actor");
        addedActor.setText(fullName);
        return addedActor;
    }
    public static ComboBox<ActorSummaryDto> actorComboBox(ActorServiceImpl actorService) {
        ComboBox<ActorSummaryDto> actorComboBox = new ComboBox<>("Add actor");
        List<ActorSummaryDto> actorItems = actorService.fetchAllActorsSummary();

        actorComboBox.setItems(actorItems);
        actorComboBox.setItemLabelGenerator(ActorSummaryDto::getFullName);
        actorComboBox.setRequiredIndicatorVisible(false);
        actorComboBox.setHelperText("You can add multiple actors");

        return actorComboBox;
    }
    public static ComboBox<DirectorSummaryDto> directorComboBox(DirectorServiceImpl directorService) {
        ComboBox<DirectorSummaryDto> directorList = new ComboBox<>("Director");
        List<DirectorSummaryDto> directors = directorService.fetchAllDirectorsSummary();

        directorList.setItems(directors);
        directorList.setItemLabelGenerator(item -> item.getFullName() != null ? item.getFullName() : " - ");

        directorList.setRequiredIndicatorVisible(false);

        return directorList;
    }
}
