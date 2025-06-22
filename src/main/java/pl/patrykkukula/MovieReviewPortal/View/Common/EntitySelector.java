package pl.patrykkukula.MovieReviewPortal.View.Common;

import com.vaadin.flow.component.combobox.ComboBox;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;

import java.util.List;

public class EntitySelector {

    private final DirectorServiceImpl directorService;

    public EntitySelector(DirectorServiceImpl directorService) {
        this.directorService = directorService;
    }

    public ComboBox<DirectorSummaryDto> directorComboBox(){
        ComboBox<DirectorSummaryDto> directorList = new ComboBox<>("Director");
        List<DirectorSummaryDto> directors = directorService.fetchAllDirectorsSummary();

        directorList.setItems(directors);
        directorList.setItemLabelGenerator(item -> item.getFullName() != null ? item.getFullName() : " - ");

        directorList.setRequiredIndicatorVisible(false);

        return directorList;
    }

}
