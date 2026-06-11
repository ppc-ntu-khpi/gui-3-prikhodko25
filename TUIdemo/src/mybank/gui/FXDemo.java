package com.mybank.gui;

import com.mybank.data.DataSource;
import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import java.util.Locale;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.web.WebView;

public class FXDemo extends Application {

    private Text     title;
    private ComboBox<String> clients;
    private WebView  webLog;   

    @Override
    public void start(Stage primaryStage) {
        BorderPane border = new BorderPane();

        border.setLeft(addVBox());
        border.setCenter(addWebPane());

        HBox hbox = addHBox();
        border.setTop(hbox);
        addStackPane(hbox);

        Scene scene = new Scene(border, 520, 420);
        primaryStage.setTitle("MyBank Clients");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        ObservableList<String> items = FXCollections.observableArrayList();
        Bank bank = Bank.getBank();
        for (int i = 0; i < bank.getNumberOfCustomers(); i++) {
            Customer c = bank.getCustomer(i);
            items.add(c.getLastName() + ", " + c.getFirstName());
        }

        clients = new ComboBox<>(items);
        clients.setPrefSize(180, 26);
        clients.setPromptText("Click to choose...");

        Button btnShow = new Button("Show");
        btnShow.setPrefSize(90, 26);

        Button btnReport = new Button("Report");
        btnReport.setPrefSize(90, 26);

        btnShow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    int custNo = clients.getSelectionModel().getSelectedIndex();
                    if (custNo < 0) throw new Exception("You need to choose a client first!");

                    Bank bank = Bank.getBank();
                    Customer cust = bank.getCustomer(custNo);
                    title.setText(clients.getValue());

                    StringBuilder sb = new StringBuilder(htmlHead());
                    sb.append("<h2>").append(cust.getLastName()).append(", ")
                      .append(cust.getFirstName()).append("</h2><hr>")
                      .append(tableHeader("#", "Account Type", "Balance"));

                    for (int i = 0; i < cust.getNumberOfAccounts(); i++) {
                        Account acc  = cust.getAccount(i);
                        String  type = acc instanceof CheckingAccount ? "Checking" : "Savings";
                        String  bg   = i % 2 == 0 ? "#fff" : "#f5f5f5";
                        String  color = acc.getBalance() >= 0 ? "#2e7d32" : "#c62828";
                        sb.append("<tr style='background:").append(bg).append(";'>")
                          .append("<td>").append(i + 1).append("</td>")
                          .append("<td>").append(type).append("</td>")
                          .append("<td style='text-align:right;color:").append(color)
                          .append(";'><b>$").append(String.format(Locale.US, "%.2f", acc.getBalance()))
                          .append("</b></td></tr>");
                    }

                    sb.append("</table></body></html>");
                    webLog.getEngine().loadContent(sb.toString());

                } catch (Exception e) {
                    showError(e.getMessage() != null ? e.getMessage() : "Choose a client first!");
                }
            }
        });

        btnReport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Bank bank = Bank.getBank();
                title.setText("Customer Report");

                StringBuilder sb = new StringBuilder(htmlHead());
                sb.append("<h2>Customer Report</h2><hr>")
                  .append(tableHeader("#", "Name", "Account Type", "Balance"));

                for (int i = 0; i < bank.getNumberOfCustomers(); i++) {
                    Customer cust     = bank.getCustomer(i);
                    String   fullName = cust.getLastName() + ", " + cust.getFirstName();

                    for (int j = 0; j < cust.getNumberOfAccounts(); j++) {
                        Account acc   = cust.getAccount(j);
                        String  type  = acc instanceof CheckingAccount ? "Checking" : "Savings";
                        String  bg    = i % 2 == 0 ? "#fff" : "#f5f5f5";
                        String  color = acc.getBalance() >= 0 ? "#2e7d32" : "#c62828";

                        sb.append("<tr style='background:").append(bg).append(";'>")
                          .append("<td>").append(i).append("</td>")
                          .append("<td>").append(j == 0 ? fullName : "").append("</td>")
                          .append("<td>").append(type).append("</td>")
                          .append("<td style='text-align:right;color:").append(color)
                          .append(";'><b>$").append(String.format(Locale.US, "%.2f", acc.getBalance()))
                          .append("</b></td></tr>");
                    }
                }

                sb.append("</table></body></html>");
                webLog.getEngine().loadContent(sb.toString());
            }
        });

        hbox.getChildren().addAll(clients, btnShow, btnReport);
        return hbox;
    }

    public VBox addVBox() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);
        vbox.setPrefWidth(160);

        title = new Text("Client Name");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        vbox.getChildren().add(title);

        Line sep = new Line(10, 10, 140, 10);
        vbox.getChildren().add(sep);

        Text hint = new Text("Select a customer\nfrom the list above\nand press Show.");
        hint.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        hint.setFill(Color.GRAY);
        vbox.getChildren().add(hint);

        return vbox;
    }

    private BorderPane addWebPane() {
        webLog = new WebView();
        webLog.getEngine().loadContent("<html><body style='font-family:Arial;color:#555;'>"
            + "<p>Select a customer and press <b>Show</b>, "
            + "or press <b>Report</b> for all customers.</p></body></html>");

        BorderPane pane = new BorderPane(webLog);
        pane.setPadding(new Insets(8));
        return pane;
    }

    public void addStackPane(HBox hb) {
        StackPane stack = new StackPane();
        Rectangle helpIcon = new Rectangle(30.0, 25.0);
        helpIcon.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop[]{
                    new Stop(0,   Color.web("#4977A3")),
                    new Stop(0.5, Color.web("#B0C6DA")),
                    new Stop(1,   Color.web("#9CB6CF"))}));
        helpIcon.setStroke(Color.web("#D0E6FA"));
        helpIcon.setArcHeight(3.5);
        helpIcon.setArcWidth(3.5);

        Text helpText = new Text("?");
        helpText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        helpText.setFill(Color.WHITE);
        helpText.setStroke(Color.web("#7080A0"));

        EventHandler<MouseEvent> aboutHandler = t -> showAbout();
        helpIcon.setOnMouseClicked(aboutHandler);
        helpText.setOnMouseClicked(aboutHandler);

        stack.getChildren().addAll(helpIcon, helpText);
        stack.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setMargin(helpText, new Insets(0, 10, 0, 0));

        hb.getChildren().add(stack);
        HBox.setHgrow(stack, Priority.ALWAYS);
    }

    private void showAbout() {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("About");
        a.setHeaderText(null);
        a.setContentText("MyBank Client Manager — Lab 5 (JavaFX)\n"
            + "Reads customer data from test.dat.\n\n"
            + "© 2026 Yarik");
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String htmlHead() {
        return "<html><body style='font-family:Arial,sans-serif;font-size:13px;margin:8px;'>";
    }

    private String tableHeader(String... cols) {
        StringBuilder sb = new StringBuilder(
            "<table cellpadding='5' cellspacing='0' width='100%' "
            + "style='border-collapse:collapse;'>"
            + "<tr style='background:#e8eaf6;font-weight:bold;'>");
        for (String col : cols) {
            sb.append("<th align='left'>").append(col).append("</th>");
        }
        sb.append("</tr>");
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);

        DataSource ds = new DataSource("data/test.dat");
        ds.loadData();

        launch(args);
    }
}
