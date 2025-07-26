package pl.patrykkukula.MovieReviewPortal.View.Topic;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route("topics")
public class TopicDetailsView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {

    }

    @Override
    public String getPageTitle() {
        return "topic";
    }
}
