package pl.patrykkukula.MovieReviewPortal.View.Account;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserDataDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.notification.Notification.Position;
import static com.vaadin.flow.component.notification.Notification.show;

@Slf4j
@Route("admin-panel")
@PageTitle("Admin panel")
@RolesAllowed(value = "ADMIN")
public class AdminView extends VerticalLayout {
    private final Grid<UserDataDto> grid = new Grid<>();
    private final UserServiceImpl userService;
    private final TextField searchField = FormFields.searchField("Search by username", "Enter username...");
    private final Button searchButton = new Button("Search");
    private final String ROLE_MODERATOR = "MODERATOR";
    private final String BAN_DURATION_DAY = "1 day";
    private final String BAN_DURATION_WEEK = "7 days";
    private final String BAN_DURATION_MONTH = "30 days";
    private final String BAN_DURATION_PERMANENT = "Permanent";

    public AdminView(UserServiceImpl userService) {
        this.userService = userService;
        configureGrid();
        add(searchField, searchButton, grid);
    }

    private void configureGrid(){
        grid.setPageSize(4);
        grid.addColumn(UserDataDto::getUsername).setHeader("Username").setSortable(true);
        grid.addColumn(UserDataDto::getEmail).setHeader("Email");
        grid.addColumn(user -> user.getRoles().stream().collect(Collectors.joining(", "))).setHeader("Roles");
        grid.addColumn(UserDataDto::getStatus).setHeader("Status");
        grid.addColumn(UserDataDto::getBanExpirationDate).setHeader("Banned until");
        grid.addComponentColumn(user -> createActionSpan(user.getUsername())).setHeader("Action");

        DataProvider<UserDataDto, String> dataProvider = dataProvider();
        ConfigurableFilterDataProvider<UserDataDto, Void, String> dp = dataProvider.withConfigurableFilter();

        searchButton.addClickListener(e -> {
           dp.setFilter(searchField.getValue());
        });
        grid.setItems(dp);
    }
    private BackEndDataProvider<UserDataDto, String> dataProvider(){
        return new CallbackDataProvider<>(
                query -> {
                    int offset = query.getOffset();
                    List<QuerySortOrder> sortOrders = query.getSortOrders();
                    Sort sort = mapToSort(sortOrders);
                    int page = offset / grid.getPageSize();
                    String filter = query.getFilter().orElse(null);

                    return userService.fetchAllUsers(page, grid.getPageSize(), sort, filter).stream();
                },
                query -> {
                    String filter = query.getFilter().orElse(null);
                    return userService.countUsers(filter);
                }
        );
    }
    private Sort mapToSort(List<QuerySortOrder> sortOrders){
        if (sortOrders == null || sortOrders.isEmpty()) return Sort.by(Sort.Direction.ASC, "username");
        List<Sort.Order> orders = new ArrayList<>();
        for (QuerySortOrder order : sortOrders){
            orders.add(new Sort.Order(order.getDirection().equals(SortDirection.ASCENDING) ? Sort.Direction.ASC : Sort.Direction.DESC, order.getSorted()));
        }
        return Sort.by(orders);
    }
    private Span createActionSpan(String username){
        Span span = new Span();

        Icon ban = VaadinIcon.BAN.create();
        ban.getStyle().set("color", "red");
        ban.setTooltipText("Ban user");

        ContextMenu contextMenu = new ContextMenu(ban);
        contextMenu.setOpenOnClick(true);
        addContextMenuItems(username,contextMenu);

        Icon makeAdmin = VaadinIcon.PLUS_CIRCLE.create();
        makeAdmin.setTooltipText("Add user Moderator");
        makeAdmin.addClickListener(e -> roleActionListener(userService::addRole, "add", username));

        Icon removeAdmin = VaadinIcon.MINUS_CIRCLE.create();
        removeAdmin.setTooltipText("Remove user Moderator");
        removeAdmin.addClickListener(e -> roleActionListener(userService::removeRole, "remove", username));

        ban.getStyle().set("margin-right", "10px").set("font-size", "12px");
        makeAdmin.getStyle().set("margin-right", "10px").set("font-size", "12px");
        removeAdmin.getStyle().set("margin-right", "10px").set("font-size", "12px");

        span.add(ban, makeAdmin, removeAdmin);
        return span;
    }
    private void addContextMenuItems(String username, ContextMenu menu){
        menu.addItem(createBanDurationButton(username, BAN_DURATION_DAY));
        menu.addItem(createBanDurationButton(username, BAN_DURATION_WEEK));
        menu.addItem(createBanDurationButton(username, BAN_DURATION_MONTH));
        menu.addItem(createBanDurationButton(username, BAN_DURATION_PERMANENT));
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> menu.close());
        menu.addItem(cancel);
    }
    private Button createBanDurationButton(String username, String banDuration){
        Button button = new Button(banDuration);

        button.addClickListener(e -> {
           Duration duration;
           switch (banDuration){
               case "7 days" -> duration = Duration.ofDays(7);
               case "30 days" -> duration = Duration.ofDays(30);
               case "Permanent" -> duration = Duration.ofDays(9999999);
               default -> duration = Duration.ofHours(24);
           }
           ConfirmDialog confirmDialog = createConfirmDialog(username, duration, banDuration);
           confirmDialog.open();
        });
        return button;
    }
    private ConfirmDialog createConfirmDialog(String username, Duration duration, String banDuration){
        ConfirmDialog dialog = new ConfirmDialog(
                "Are you sure you want to ban user?",
                null,
                "confirm",
                e -> {
                    boolean banned = userService.banUser(username, duration);
                    if (banned) show("User banned for %s successfully".formatted(banDuration), 3000, Position.MIDDLE);
                    else show("Something went wrong. Please try again", 3000, Position.MIDDLE);
                    },
                "cancel",
                e -> {});

        dialog.addCancelListener(e -> dialog.close());
        return dialog;
    }
    private void roleActionListener(BiPredicate<String, String> service, String word, String username){
        String first = "";
        String second = "";
        switch (word){
            case "Remove" -> {
                first = "removed";
                second = "removing";
            }
            case "Add" -> {
                first = "added";
                second = "adding";
            }
        }
        try{
        boolean isActionSuccess = service.test(username, ROLE_MODERATOR);

        if (isActionSuccess) show("Role Moderator %s successfully.".formatted(first), 3000, Position.MIDDLE);
        else show("Error %s role".formatted(second), 5000, Position.MIDDLE);
    }
        catch (IllegalStateException ex){
        show(ex.getMessage(), 5000, Position.MIDDLE);
    }
    }
}
