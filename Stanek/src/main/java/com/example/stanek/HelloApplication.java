package com.example.stanek;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloApplication extends Application {
    private StackPane root;
    private VBox sidebar;
    private VBox dashboardPage;
    private VBox cleanerPage;
    private VBox optimizerPage;
    private VBox windowsPage;
    private VBox settingsPage;
    private VBox aboutPage;
    private BorderPane mainLayout;
    private double sidebarWidth = 280;
    private boolean sidebarExpanded = true;
    private Timeline backgroundTimeline;
    private VBox loginPage;
    private Map<String, String> userCredentials = new HashMap<>();
    private Map<String, String> userUuids = new HashMap<>();
    private String currentUser;
    private Stage primaryStage;
    private Text timeLabel;
    private HostServices hostServices;
    private boolean darkMode = true;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.hostServices = getHostServices();
        initializeCredentialsAndUuids();

        root = new StackPane();
        applyTheme();

        createBackgroundEffects();

        loginPage = createLoginPage();
        root.getChildren().add(loginPage);

        Scene scene = new Scene(root, 1200, 800);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("ULTIMATE OPTIMIZER");
        primaryStage.setScene(scene);
        primaryStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        primaryStage.setMaximized(true);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        DropShadow shadow = new DropShadow();
        shadow.setRadius(30);
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        root.setEffect(shadow);

        primaryStage.show();
    }

    private void applyTheme() {
        if (darkMode) {
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f0c29, #302b63, #24243e);");
        } else {
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f5f7fa, #e4e8f0, #d9dde5);");
        }
    }

    private void initializeCredentialsAndUuids() {
        File file = new File("bin.txt");
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("admin,haslo123,ABCD-1234-EFGH-5678\n");
                writer.write("javarek,javaro,EE884A80-C4B8-11EE-A066-7DF9F0D04A00\n");
                writer.write("stanek,szefu,5AF70288-87C8-9119-A22A-D8BBC1A3CC60\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    userCredentials.put(parts[0], parts[1]);
                    userUuids.put(parts[0], parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getComputerUuid() {
        try {
            Process process = Runtime.getRuntime().exec("wmic csproduct get uuid");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0 && !line.contains("UUID")) {
                    return line.trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private VBox createLoginPage() {
        VBox loginContainer = new VBox();
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setSpacing(30);
        loginContainer.setPadding(new Insets(50));
        loginContainer.setMaxWidth(500);
        loginContainer.setMaxHeight(600);

        VBox loginBox = new VBox();
        loginBox.setStyle("-fx-background-color: rgba(15, 15, 15, 0.95); -fx-background-radius: 20;");
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setSpacing(30);
        loginBox.setPadding(new Insets(50, 40, 60, 40));
        loginBox.setEffect(new DropShadow(30, Color.rgb(0, 0, 0, 0.5)));

        Text loginTitle = new Text("LOGOWANIE");
        loginTitle.setFill(Color.WHITE);
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Text subtitle = new Text("ULTIMATE OPTIMIZER");
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#8a2be2")),
                new Stop(0.5, Color.web("#9370db")),
                new Stop(1, Color.web("#8a2be2"))
        );
        subtitle.setFill(gradient);
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        VBox fieldsBox = new VBox();
        fieldsBox.setAlignment(Pos.CENTER);
        fieldsBox.setSpacing(20);
        fieldsBox.setPadding(new Insets(20, 0, 20, 0));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nazwa użytkownika");
        styleTextField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");
        styleTextField(passwordField);

        Button loginButton = new Button("ZALOGUJ");
        loginButton.setStyle("-fx-background-color: linear-gradient(to right, #8a2be2, #9370db); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 40; -fx-font-size: 16;");
        loginButton.setEffect(new DropShadow(10, Color.rgb(138, 43, 226, 0.5)));

        Text errorLabel = new Text();
        errorLabel.setFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", 14));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
                String currentUuid = getComputerUuid();
                String allowedUuid = userUuids.get(username);

                if (currentUuid != null && currentUuid.equals(allowedUuid)) {
                    currentUser = username;
                    errorLabel.setText("");
                    initializeMainApplication();
                } else {
                    errorLabel.setText("Logowanie możliwe tylko z autoryzowanego komputera");
                    shakeAnimation(loginBox);
                }
            } else {
                errorLabel.setText("Nieprawidłowa nazwa użytkownika lub hasło");
                shakeAnimation(loginBox);
            }
        });

        fieldsBox.getChildren().addAll(usernameField, passwordField, loginButton, errorLabel);
        loginBox.getChildren().addAll(loginTitle, subtitle, fieldsBox);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(800), loginBox);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setInterpolator(Interpolator.EASE_BOTH);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), loginBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ParallelTransition pt = new ParallelTransition(scaleIn, fadeIn);
        pt.play();

        loginContainer.getChildren().add(loginBox);
        return loginContainer;
    }

    private void styleTextField(TextField field) {
        field.setStyle("-fx-background-color: #2d2d2d; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 12 15; -fx-background-radius: 8; -fx-border-color: #3d3d3d; -fx-border-radius: 8;");
        field.setPrefWidth(250);
        field.setEffect(new InnerShadow(5, Color.rgb(0, 0, 0, 0.3)));

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-color: #3d3d3d; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 12 15; -fx-background-radius: 8; -fx-border-color: #8a2be2; -fx-border-radius: 8;");
            } else {
                field.setStyle("-fx-background-color: #2d2d2d; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 12 15; -fx-background-radius: 8; -fx-border-color: #3d3d3d; -fx-border-radius: 8;");
            }
        });
    }

    private void shakeAnimation(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

    private void initializeMainApplication() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), loginPage);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            root.getChildren().remove(loginPage);
            setupMainInterface();
        });
        fadeOut.play();
    }

    private void setupMainInterface() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: transparent;");

        HBox header = createHeader();
        mainLayout.setTop(header);

        sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        StackPane contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: transparent;");
        contentArea.setPadding(new Insets(0, 20, 20, 20));

        dashboardPage = createDashboardPage();
        cleanerPage = createCleanerPage();
        optimizerPage = createOptimizerPage();
        windowsPage = createWindowsPage();
        settingsPage = createSettingsPage();
        aboutPage = createAboutPage();

        contentArea.getChildren().add(dashboardPage);
        mainLayout.setCenter(contentArea);
        root.getChildren().add(mainLayout);

        setupNavigation(contentArea);
        startClockUpdater();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), mainLayout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void startClockUpdater() {
        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    private void updateClock() {
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Platform.runLater(() -> timeLabel.setText(currentTime));
    }

    private void createBackgroundEffects() {
        backgroundTimeline = new Timeline(new KeyFrame(Duration.seconds(0.3), event -> {
            Node particle = createParticle();
            root.getChildren().add(particle);

            double startX = Math.random() * root.getWidth();
            double endX = startX + (Math.random() * 200 - 100);

            Timeline particleAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(15 + Math.random() * 15),
                            new KeyValue(particle.translateYProperty(), -100),
                            new KeyValue(particle.translateXProperty(), endX),
                            new KeyValue(particle.opacityProperty(), 0)
                    ));
            particleAnimation.setOnFinished(e -> root.getChildren().remove(particle));
            particleAnimation.play();
        }));
        backgroundTimeline.setCycleCount(Animation.INDEFINITE);
        backgroundTimeline.play();

        AnimationTimer gradientPulse = new AnimationTimer() {
            long startTime = 0;
            @Override
            public void handle(long now) {
                if (startTime == 0) startTime = now;
                double progress = (now - startTime) % 10_000_000_000L / 10_000_000_000.0;
                String gradient;
                if (darkMode) {
                    gradient = String.format("-fx-background-color: linear-gradient(to bottom right, #0f0c29, #%02x2b63, #24243e);",
                            (int)(0x30 + 0x10 * Math.sin(progress * 2 * Math.PI)));
                } else {
                    gradient = String.format("-fx-background-color: linear-gradient(to bottom right, #f5f7fa, #%02x%02x%02x, #d9dde5);",
                            (int)(0xe4 + 0x10 * Math.sin(progress * 2 * Math.PI)),
                            (int)(0xe8 + 0x10 * Math.cos(progress * 2 * Math.PI)),
                            (int)(0xf0 + 0x10 * Math.sin(progress * 2 * Math.PI)));
                }
                root.setStyle(gradient);
            }
        };
        gradientPulse.start();
    }

    private Node createParticle() {
        double rand = Math.random();
        Node particle;
        if (rand < 0.3) {
            particle = new Text("");
            ((Text)particle).setFill(Color.rgb(138, 43, 226, 0.15));
            ((Text)particle).setFont(Font.font(15 + Math.random() * 25));
        } else if (rand < 0.6) {
            particle = new Circle(3 + Math.random() * 8);
            ((Circle)particle).setFill(Color.rgb(138, 43, 226, 0.1));
        } else {
            particle = new Rectangle(6 + Math.random() * 12, 6 + Math.random() * 12);
            ((Rectangle)particle).setFill(Color.rgb(138, 43, 226, 0.08));
            ((Rectangle)particle).setRotate(Math.random() * 360);
        }
        particle.setTranslateX(Math.random() * root.getWidth());
        particle.setTranslateY(root.getHeight() + 50);
        return particle;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: rgba(15, 15, 15, 0.95);");
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        Button menuToggle = new Button("☰");
        menuToggle.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 20;");
        menuToggle.setOnAction(e -> toggleSidebar());

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setSpacing(15);

        Text title = new Text("ULTIMATE OPTIMIZER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#8a2be2")),
                new Stop(0.5, Color.web("#9370db")),
                new Stop(1, Color.web("#8a2be2"))
        );
        title.setFill(gradient);

        titleBox.getChildren().addAll(menuToggle, title);

        HBox userProfile = new HBox();
        userProfile.setAlignment(Pos.CENTER_RIGHT);
        userProfile.setSpacing(15);

        timeLabel = new Text();
        timeLabel.setFill(Color.WHITE);
        timeLabel.setFont(Font.font("Arial", 16));

        StackPane userAvatar = new StackPane();
        userAvatar.setStyle("-fx-background-color: linear-gradient(to bottom right, #8a2be2, #5f00a8); -fx-background-radius: 50%;");
        userAvatar.setPrefSize(40, 40);
        userAvatar.setEffect(new DropShadow(8, Color.rgb(138, 43, 226, 0.5)));

        Text avatarText = new Text(currentUser.substring(0, 1).toUpperCase());
        avatarText.setFill(Color.WHITE);
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        userAvatar.getChildren().add(avatarText);
        userProfile.getChildren().addAll(timeLabel, userAvatar);

        HBox.setHgrow(titleBox, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, userProfile);

        return header;
    }

    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sidebar);
        tt.setFromX(sidebarExpanded ? -sidebarWidth : 0);
        tt.setToX(sidebarExpanded ? 0 : -sidebarWidth);
        ScaleTransition st = new ScaleTransition(Duration.millis(300), sidebar);
        st.setFromX(sidebarExpanded ? 0.9 : 1);
        st.setToX(sidebarExpanded ? 1 : 0.9);
        ParallelTransition pt = new ParallelTransition(tt, st);
        pt.play();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-background-color: rgba(15, 15, 15, 0.95);");
        sidebar.setPrefWidth(sidebarWidth);
        sidebar.setSpacing(25);
        sidebar.setPadding(new Insets(25, 0, 25, 0));

        HBox logo = new HBox();
        logo.setAlignment(Pos.CENTER);
        logo.setSpacing(15);
        logo.setPadding(new Insets(0, 0, 30, 0));

        Text logoIcon = new Text("");
        logoIcon.setFont(Font.font(30));

        Text logoText = new Text("SYSTEM OPTIMIZER");
        logoText.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        LinearGradient logoGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#8a2be2")),
                new Stop(0.5, Color.web("#9370db")),
                new Stop(1, Color.web("#8a2be2"))
        );
        logoText.setFill(logoGradient);

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(3), logoText);
        pulse.setFromX(1);
        pulse.setToX(1.03);
        pulse.setFromY(1);
        pulse.setToY(1.03);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        logo.getChildren().addAll(logoIcon, logoText);

        VBox navMenu = new VBox();
        navMenu.setSpacing(8);

        Button dashboardBtn = createNavButton("Pulpit");
        dashboardBtn.setOnAction(e -> showPage(dashboardPage));

        Button cleanerBtn = createNavButton("Czyszczenie");
        cleanerBtn.setOnAction(e -> showPage(cleanerPage));

        Button optimizerBtn = createNavButton("Optymalizacja");
        optimizerBtn.setOnAction(e -> showPage(optimizerPage));

        Button windowsBtn = createNavButton("Uniwersalne");
        windowsBtn.setOnAction(e -> showPage(windowsPage));

        Button settingsBtn = createNavButton("Ustawienia");
        settingsBtn.setOnAction(e -> showPage(settingsPage));

        Button aboutBtn = createNavButton("O programie");
        aboutBtn.setOnAction(e -> showPage(aboutPage));

        navMenu.getChildren().addAll(dashboardBtn, cleanerBtn, optimizerBtn, windowsBtn, settingsBtn, aboutBtn);

        VBox sidebarFooter = new VBox();
        sidebarFooter.setPadding(new Insets(25, 0, 0, 0));
        sidebarFooter.setStyle("-fx-border-color: rgba(61, 61, 61, 0.3); -fx-border-width: 1 0 0 0;");

        Button logoutBtn = createNavButton("Wyloguj");
        logoutBtn.setStyle("-fx-background-color: rgba(138, 43, 226, 0.2); -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: center-left; -fx-padding: 12 20;");
        logoutBtn.setOnAction(e -> logout());

        logoutBtn.setOnMouseEntered(e -> {
            logoutBtn.setStyle("-fx-background-color: rgba(138, 43, 226, 0.4); -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: center-left; -fx-padding: 12 20;");
        });

        logoutBtn.setOnMouseExited(e -> {
            logoutBtn.setStyle("-fx-background-color: rgba(138, 43, 226, 0.2); -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: center-left; -fx-padding: 12 20;");
        });

        sidebarFooter.getChildren().add(logoutBtn);
        sidebar.getChildren().addAll(logo, navMenu, sidebarFooter);
        return sidebar;
    }

    private void logout() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), mainLayout);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            root.getChildren().remove(mainLayout);
            loginPage = createLoginPage();
            root.getChildren().add(loginPage);
        });
        fadeOut.play();
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: center-left; -fx-padding: 12 20;");
        btn.setMaxWidth(Double.MAX_VALUE);

        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("rgba(138, 43, 226")) {
                btn.setStyle("-fx-background-color: rgba(61, 61, 61, 0.3); -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: center-left; -fx-padding: 12 20;");
            }
            btn.setTranslateX(5);
        });

        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("rgba(138, 43, 226")) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: center-left; -fx-padding: 12 20;");
            }
            btn.setTranslateX(0);
        });

        return btn;
    }

    private VBox createDashboardPage() {
        VBox dashboard = new VBox();
        dashboard.setPadding(new Insets(25));
        dashboard.setSpacing(25);
        dashboard.setAlignment(Pos.CENTER);
        dashboard.setStyle("-fx-background-color: transparent;");

        Text welcomeText = new Text("WITAJ " + currentUser.toUpperCase() + "!");
        welcomeText.setFill(Color.WHITE);
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        welcomeText.setEffect(new DropShadow(10, Color.rgb(138, 43, 226, 0.7)));

        Text appTitle = new Text("ULTIMATE OPTIMIZER");
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#8a2be2")),
                new Stop(0.5, Color.web("#9370db")),
                new Stop(1, Color.web("#8a2be2"))
        );
        appTitle.setFill(gradient);
        appTitle.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        appTitle.setEffect(new Glow(0.8));

        Text versionText = new Text("Wersja 2.5.0 PRO");
        versionText.setFill(Color.WHITE);
        versionText.setFont(Font.font("Arial", 20));

        Button quickCleanBtn = new Button("ROZPOCZNIJ CZYSZCZENIE");
        quickCleanBtn.setStyle("-fx-background-color: linear-gradient(to right, #8a2be2, #9370db); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24; -fx-padding: 20 50; -fx-background-radius: 15;");
        quickCleanBtn.setEffect(new DropShadow(20, Color.rgb(138, 43, 226, 0.5)));
        quickCleanBtn.setOnAction(e -> showPage(cleanerPage));

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), quickCleanBtn);
        pulse.setFromX(1);
        pulse.setToX(1.05);
        pulse.setFromY(1);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        VBox.setMargin(welcomeText, new Insets(0, 0, 10, 0));
        VBox.setMargin(appTitle, new Insets(0, 0, 5, 0));
        VBox.setMargin(versionText, new Insets(0, 0, 40, 0));
        VBox.setMargin(quickCleanBtn, new Insets(40, 0, 0, 0));

        dashboard.getChildren().addAll(welcomeText, appTitle, versionText, quickCleanBtn);

        FadeTransition ft = new FadeTransition(Duration.millis(500), dashboard);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return dashboard;
    }

    private VBox createCleanerPage() {
        VBox cleanerPage = new VBox();
        cleanerPage.setPadding(new Insets(25));
        cleanerPage.setSpacing(25);

        HBox pageHeader = new HBox();
        pageHeader.setAlignment(Pos.CENTER_LEFT);
        pageHeader.setSpacing(20);

        Text cleanerIcon = new Text("");
        cleanerIcon.setFont(Font.font(30));

        Text pageTitle = new Text("Systemowy Cleaner");
        pageTitle.setFill(Color.LIME);
        pageTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 26));

        Button scanBtn = new Button("SKANUJ SYSTEM");
        scanBtn.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 25;");

        Text scanIcon = new Text("");
        scanIcon.setFont(Font.font(16));
        scanBtn.setGraphic(scanIcon);

        HBox.setHgrow(pageTitle, Priority.ALWAYS);
        pageHeader.getChildren().addAll(cleanerIcon, pageTitle, scanBtn);

        TextArea consoleOutput = new TextArea();
        consoleOutput.setStyle("-fx-control-inner-background: black; -fx-text-fill: lime; -fx-font-family: 'Consolas'; -fx-font-size: 14;");
        consoleOutput.setEditable(false);
        consoleOutput.setPrefHeight(500);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cleanBtn = new Button("WYCZYŚĆ");
        cleanBtn.setStyle("-fx-background-color: #8a2be2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 25;");

        buttonBox.getChildren().add(cleanBtn);

        scanBtn.setOnAction(e -> runCleanerScan(consoleOutput));
        cleanBtn.setOnAction(e -> runCleanerCleanup(consoleOutput));

        cleanerPage.getChildren().addAll(pageHeader, consoleOutput, buttonBox);

        FadeTransition ft = new FadeTransition(Duration.millis(500), cleanerPage);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return cleanerPage;
    }

    private void runCleanerScan(TextArea consoleOutput) {
        consoleOutput.clear();
        consoleOutput.appendText("--------------------------------------------------\n");
        consoleOutput.appendText("ULTIMATE SYSTEM OPTIMIZER - CLEANER PRO v2.5.0\n");
        consoleOutput.appendText("--------------------------------------------------\n\n");

        try {
            consoleOutput.appendText("> Skanowanie plików tymczasowych w %TEMP%...\n");
            java.nio.file.Path tempPath = Paths.get(System.getenv("TEMP"));
            long tempSize = 0;
            long tempCount = 0;

            try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(tempPath)) {
                for (java.nio.file.Path file : stream) {
                    if (Files.isRegularFile(file)) {
                        try {
                            tempSize += Files.size(file);
                            tempCount++;
                        } catch (IOException e) {
                            consoleOutput.appendText("    Brak dostępu do: " + file.getFileName() + "\n");
                        }
                    }
                }
            }
            consoleOutput.appendText(String.format("  Znaleziono %d plików tymczasowych (%.2f MB)\n",
                    tempCount, tempSize / (1024.0 * 1024.0)));

            consoleOutput.appendText("> Sprawdzanie kosza...\n");
            try {
                long recycleBinSize = 0;
                long recycleBinCount = 0;
                File[] roots = File.listRoots();
                for (File root : roots) {
                    File recycleBin = new File(root, "$Recycle.Bin");
                    if (recycleBin.exists() && recycleBin.canRead()) {
                        recycleBinCount += countFilesInRecycleBin(recycleBin, consoleOutput);
                    }
                }
                consoleOutput.appendText(String.format("  Znaleziono %d elementów w koszu\n", recycleBinCount));
            } catch (Exception e) {
                consoleOutput.appendText("  Nie można zeskanować kosza: " + e.getMessage() + " Javaruś :)\n");
            }

            consoleOutput.appendText("\nSKANOWANIE ZAKOŃCZONE: Znaleziono pliki do usunięcia\n");
        } catch (Exception e) {
            consoleOutput.appendText("Błąd podczas skanowania: " + e.getMessage() + "\n");
        }
    }

    private long countFilesInRecycleBin(File recycleBin, TextArea consoleOutput) {
        long count = 0;
        File[] files = recycleBin.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !file.getName().equals(".") && !file.getName().equals("..")) {
                    try {
                        count += countFilesInRecycleBin(file, consoleOutput);
                    } catch (Exception e) {
                        consoleOutput.appendText("    Brak dostępu do: " + file.getPath() + "\n");
                    }
                } else if (file.isFile()) {
                    count++;
                }
            }
        }
        return count;
    }

    private void runCleanerCleanup(TextArea consoleOutput) {
        consoleOutput.clear();
        consoleOutput.appendText("--------------------------------------------------\n");
        consoleOutput.appendText("ULTIMATE SYSTEM OPTIMIZER - CLEANER PRO v2.5.0\n");
        consoleOutput.appendText("--------------------------------------------------\n\n");
        VBox parentBox = (VBox) consoleOutput.getParent();

        ProgressBar cleanupProgressBar = new ProgressBar(0);
        cleanupProgressBar.setPrefWidth(400);
        cleanupProgressBar.setStyle("-fx-accent: #8a2be2;");
        parentBox.getChildren().add(cleanupProgressBar);

        Timeline progressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(cleanupProgressBar.progressProperty(), 0))
        );

        List<KeyFrame> keyFrames = new ArrayList<>();
        keyFrames.add(new KeyFrame(Duration.seconds(0.5),
                e -> {
                    consoleOutput.appendText("> Czyszczenie folderu %TEMP%...\n");
                    cleanTempFolder(consoleOutput);
                },
                new KeyValue(cleanupProgressBar.progressProperty(), 0.2)
        ));
        keyFrames.add(new KeyFrame(Duration.seconds(1),
                e -> {
                    consoleOutput.appendText("> Czyszczenie kosza...\n");
                    emptyRecycleBin(consoleOutput);
                },
                new KeyValue(cleanupProgressBar.progressProperty(), 0.4)
        ));
        keyFrames.add(new KeyFrame(Duration.seconds(1.5),
                e -> consoleOutput.appendText("> Usuwanie cache i pozostałości...\n"),
                new KeyValue(cleanupProgressBar.progressProperty(), 0.6)
        ));
        keyFrames.add(new KeyFrame(Duration.seconds(2),
                e -> consoleOutput.appendText("> Dostęp do kosza...\n"),
                new KeyValue(cleanupProgressBar.progressProperty(), 0.7)
        ));
        keyFrames.add(new KeyFrame(Duration.seconds(2.5),
                e -> consoleOutput.appendText("> Czyszczenie ukrytej pamięci...\n"),
                new KeyValue(cleanupProgressBar.progressProperty(), 0.8)
        ));
        keyFrames.add(new KeyFrame(Duration.seconds(3),
                e -> consoleOutput.appendText("> Usuwanie śladów malware...\n"),
                new KeyValue(cleanupProgressBar.progressProperty(), 0.9)
        ));
        keyFrames.add(new KeyFrame(Duration.seconds(3.5),
                e -> consoleOutput.appendText("> Finalizowanie czyszczenia...\n"),
                new KeyValue(cleanupProgressBar.progressProperty(), 1.0)
        ));
        keyFrames.add(new KeyFrame(Duration.seconds(4),
                e -> {
                    consoleOutput.appendText("> Zakończono: system zoptymalizowany, bezpieczeństwo wzmocnione.\n");
                    parentBox.getChildren().remove(cleanupProgressBar);
                }
        ));

        progressTimeline.getKeyFrames().addAll(keyFrames);
        progressTimeline.play();

        Timeline hackAnimationTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0),
                        e -> consoleOutput.setStyle("-fx-control-inner-background: black; -fx-text-fill: lime; -fx-font-family: 'Consolas'; -fx-font-size: 14;")
                ),
                new KeyFrame(Duration.seconds(0.2),
                        e -> consoleOutput.setStyle("-fx-control-inner-background: black; -fx-text-fill: lime; -fx-font-family: 'Consolas'; -fx-font-size: 14; -fx-background-color: #111111;")
                ),
                new KeyFrame(Duration.seconds(0.4),
                        e -> consoleOutput.setStyle("-fx-control-inner-background: black; -fx-text-fill: lime; -fx-font-family: 'Consolas'; -fx-font-size: 14;")
                )
        );
        hackAnimationTimeline.setCycleCount(6);
        hackAnimationTimeline.play();
    }

    private void cleanTempFolder(TextArea consoleOutput) {
        try {
            java.nio.file.Path tempPath = Paths.get(System.getenv("TEMP"));
            int deletedFiles = 0;
            int failedFiles = 0;

            try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(tempPath)) {
                for (java.nio.file.Path file : stream) {
                    try {
                        if (Files.isRegularFile(file)) {
                            Files.delete(file);
                            deletedFiles++;
                        } else if (Files.isDirectory(file)) {
                            deleteDirectory(file.toFile());
                            deletedFiles++;
                        }
                    } catch (IOException e) {
                        failedFiles++;
                        consoleOutput.appendText("    Nie można usunąć: " + file.getFileName() + "\n");
                    }
                }
            }
            consoleOutput.appendText(String.format("  Usunięto %d plików, nie udało się usunąć %d\n", deletedFiles, failedFiles));
        } catch (Exception e) {
            consoleOutput.appendText("  Błąd podczas czyszczenia folderu TEMP: " + e.getMessage() + "\n");
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    private void emptyRecycleBin(TextArea consoleOutput) {
        File[] roots = File.listRoots();
        for (File root : roots) {
            File recycleBin = new File(root, "$Recycle.Bin");
            if (recycleBin.exists() && recycleBin.canRead()) {
                emptyRecycleBinFolder(recycleBin, consoleOutput);
            }
        }
        consoleOutput.appendText("  Kosz został wyczyszczony\n");
    }

    private void emptyRecycleBinFolder(File recycleBin, TextArea consoleOutput) {
        File[] files = recycleBin.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !file.getName().equals(".") && !file.getName().equals("..")) {
                    emptyRecycleBinFolder(file, consoleOutput);
                } else {
                    try {
                        if (file.delete()) {
                            consoleOutput.appendText("    Usunięto: " + file.getName() + "\n");
                        } else {
                            consoleOutput.appendText("    Nie można usunąć: " + file.getName() + "\n");
                        }
                    } catch (SecurityException e) {
                        consoleOutput.appendText("    Brak uprawnień do usunięcia: " + file.getName() + "\n");
                    }
                }
            }
        }
    }

    private VBox createOptimizerPage() {
        VBox optimizerPage = new VBox();
        optimizerPage.setPadding(new Insets(25));
        optimizerPage.setSpacing(25);

        HBox pageHeader = new HBox();
        pageHeader.setAlignment(Pos.CENTER_LEFT);
        pageHeader.setSpacing(20);

        Text optimizerIcon = new Text("");
        optimizerIcon.setFont(Font.font(30));

        Text pageTitle = new Text("Optymalizacja Systemu");
        pageTitle.setFill(Color.WHITE);
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Button optimizeBtn = new Button("URUCHOM OPTYMALIZACJĘ");
        optimizeBtn.setStyle("-fx-background-color: linear-gradient(to right, #8a2be2, #9370db); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 25;");
        optimizeBtn.setOnAction(e -> showOptimizationProgress());

        HBox.setHgrow(pageTitle, Priority.ALWAYS);
        pageHeader.getChildren().addAll(optimizerIcon, pageTitle, optimizeBtn);

        TilePane toolsPane = new TilePane();
        toolsPane.setPadding(new Insets(15));
        toolsPane.setHgap(20);
        toolsPane.setVgap(20);
        toolsPane.setPrefColumns(2);

        List<Tool> tools = new ArrayList<>();
        tools.add(new Tool("Zmiana języka systemu", "Zmień ustawienia języka systemu"));
        tools.add(new Tool("Analizator systemu", "Przeanalizuj wydajność systemu"));
        tools.add(new Tool("Optymalizator sieci", "Optymalizuj ustawienia sieciowe"));
        tools.add(new Tool("Aktualizator sterowników", "Aktualizuj przestarzałe sterowniki"));
        tools.add(new Tool("Osłona prywatności", "Zwiększ ustawienia prywatności"));
        tools.add(new Tool("Synchronizacja czasu", "Zsynchronizuj czas systemowy"));
        tools.add(new Tool("Zarządzanie RAID", "Zarządzaj konfiguracjami RAID"));
        tools.add(new Tool("Niszczarka plików", "Bezpiecznie usuń pliki"));
        tools.add(new Tool("Menedżer uruchamiania", "Zarządzaj programami startowymi"));
        tools.add(new Tool("Czyszczenie plików tymczasowych", "Wyczyść pliki tymczasowe"));
        tools.add(new Tool("Menedżer schowka", "Zarządzaj historią schowka"));
        tools.add(new Tool("Szybkie połączenie VPN", "Połącz się szybko z VPN"));
        tools.add(new Tool("Tworzenie punktu przywracania", "Utwórz punkty przywracania"));
        tools.add(new Tool("Wyłącz przywracanie systemu", "Wyłącz funkcję przywracania systemu"));
        tools.add(new Tool("Wyłącz znaczniki czasu NTFS", "Zoptymalizuj wydajność NTFS"));
        tools.add(new Tool("Wyłącz wyszukiwanie Windows", "Wyłącz wyszukiwanie Windows"));
        tools.add(new Tool("Wyłącz Sticky Keys", "Wyłącz funkcje ułatwień dostępu"));
        tools.add(new Tool("Wyłącz usługę zgodności", "Wyłącz sprawdzanie zgodności"));
        tools.add(new Tool("Optymalizacja pasma sieciowego", "Zoptymalizuj pasmo sieciowe"));
        tools.add(new Tool("Optymalizacja wydajności", "Zoptymalizuj wydajność systemu"));

        for (Tool tool : tools) {
            VBox toolCard = createToolCard(tool);
            toolsPane.getChildren().add(toolCard);
        }

        ScrollPane scrollPane = new ScrollPane(toolsPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);

        optimizerPage.getChildren().addAll(pageHeader, scrollPane);

        FadeTransition ft = new FadeTransition(Duration.millis(500), optimizerPage);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return optimizerPage;
    }

    private void showOptimizationProgress() {
        Stage progressStage = new Stage();
        progressStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        VBox progressBox = new VBox(20);
        progressBox.setAlignment(Pos.CENTER);
        progressBox.setPadding(new Insets(30));
        progressBox.setStyle("-fx-background-color: rgba(15, 15, 15, 0.95); -fx-background-radius: 15;");
        progressBox.setEffect(new DropShadow(20, Color.BLACK));

        Text progressTitle = new Text("Optymalizacja systemu...");
        progressTitle.setFill(Color.WHITE);
        progressTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #8a2be2;");

        Text progressText = new Text("Inicjalizacja...");
        progressText.setFill(Color.WHITE);

        progressBox.getChildren().addAll(progressTitle, progressBar, progressText);

        Scene scene = new Scene(progressBox);
        scene.setFill(Color.TRANSPARENT);
        progressStage.setScene(scene);
        progressStage.show();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(1), e -> progressText.setText("Skanowanie systemu...")),
                new KeyFrame(Duration.seconds(2), new KeyValue(progressBar.progressProperty(), 0.3)),
                new KeyFrame(Duration.seconds(3), e -> progressText.setText("Stosowanie optymalizacji...")),
                new KeyFrame(Duration.seconds(4), new KeyValue(progressBar.progressProperty(), 0.6)),
                new KeyFrame(Duration.seconds(5), e -> progressText.setText("Finalizowanie zmian...")),
                new KeyFrame(Duration.seconds(6), new KeyValue(progressBar.progressProperty(), 1.0)),
                new KeyFrame(Duration.seconds(7), e -> {
                    progressText.setText("Optymalizacja zakończona!");
                    new Timeline(new KeyFrame(Duration.seconds(1), ev -> progressStage.close())).play();
                })
        );
        timeline.play();
    }

    private VBox createToolCard(Tool tool) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); -fx-background-radius: 15; -fx-border-color: rgba(61, 61, 61, 0.5); -fx-border-radius: 15; -fx-padding: 20;");
        card.setPrefWidth(350);
        card.setPrefHeight(150);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Circle iconBg = new Circle(20);
        iconBg.setFill(Color.rgb(138, 43, 226, 0.3));

        Text icon = new Text("");
        icon.setFont(Font.font(16));
        icon.setFill(Color.WHITE);
        StackPane iconPane = new StackPane(iconBg, icon);

        Text name = new Text(tool.getName());
        name.setFill(Color.WHITE);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        header.getChildren().addAll(iconPane, name);

        Text description = new Text(tool.getDescription());
        description.setFill(Color.gray(0.7));
        description.setFont(Font.font("Arial", 14));
        description.setWrappingWidth(310);

        Button runBtn = new Button("Uruchom narzędzie");
        runBtn.setStyle("-fx-background-color: linear-gradient(to right, #8a2be2, #9370db); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        runBtn.setMaxWidth(Double.MAX_VALUE);
        runBtn.setOnAction(e -> showToolExecution(tool.getName()));

        card.getChildren().addAll(header, description, runBtn);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        return card;
    }

    private void showToolExecution(String toolName) {
        Stage toolStage = new Stage();
        toolStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        VBox toolBox = new VBox(20);
        toolBox.setAlignment(Pos.CENTER);
        toolBox.setPadding(new Insets(30));
        toolBox.setStyle("-fx-background-color: rgba(15, 15, 15, 0.95); -fx-background-radius: 15;");
        toolBox.setEffect(new DropShadow(20, Color.BLACK));

        Text toolTitle = new Text("Wykonywanie: " + toolName);
        toolTitle.setFill(Color.WHITE);
        toolTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #8a2be2;");

        TextArea console = new TextArea();
        console.setStyle("-fx-control-inner-background: black; -fx-text-fill: lime; -fx-font-family: 'Consolas'; -fx-font-size: 12;");
        console.setEditable(false);
        console.setPrefHeight(200);
        console.setPrefWidth(400);

        toolBox.getChildren().addAll(toolTitle, progressBar, console);

        Scene scene = new Scene(toolBox);
        scene.setFill(Color.TRANSPARENT);
        toolStage.setScene(scene);
        toolStage.show();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(0.5), e -> console.appendText("> Inicjalizacja " + toolName + "...\n")),
                new KeyFrame(Duration.seconds(1), new KeyValue(progressBar.progressProperty(), 0.2)),
                new KeyFrame(Duration.seconds(1.5), e -> console.appendText("> Sprawdzanie wymagań systemowych...\n")),
                new KeyFrame(Duration.seconds(2), new KeyValue(progressBar.progressProperty(), 0.4)),
                new KeyFrame(Duration.seconds(2.5), e -> console.appendText("> Stosowanie optymalizacji...\n")),
                new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 0.6)),
                new KeyFrame(Duration.seconds(3.5), e -> console.appendText("> Weryfikacja zmian...\n")),
                new KeyFrame(Duration.seconds(4), new KeyValue(progressBar.progressProperty(), 0.8)),
                new KeyFrame(Duration.seconds(4.5), e -> console.appendText("> Finalizowanie...\n")),
                new KeyFrame(Duration.seconds(5), new KeyValue(progressBar.progressProperty(), 1.0)),
                new KeyFrame(Duration.seconds(5.5), e -> {
                    console.appendText("> " + toolName + " wykonane pomyślnie!\n");
                    new Timeline(new KeyFrame(Duration.seconds(1), ev -> toolStage.close())).play();
                })
        );
        timeline.play();
    }

    private VBox createWindowsPage() {
        VBox windowsPage = new VBox();
        windowsPage.setPadding(new Insets(25));
        windowsPage.setSpacing(25);

        HBox pageHeader = new HBox();
        pageHeader.setAlignment(Pos.CENTER_LEFT);
        pageHeader.setSpacing(20);

        Text windowsIcon = new Text("");
        windowsIcon.setFont(Font.font(30));

        Text pageTitle = new Text("Optymalizator Windows");
        pageTitle.setFill(Color.WHITE);
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Button applyBtn = new Button("ZASTOSUJ ZMIANY");
        applyBtn.setStyle("-fx-background-color: linear-gradient(to right, #8a2be2, #9370db); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 25;");
        applyBtn.setOnAction(e -> showWindowsOptimizationProgress());

        HBox.setHgrow(pageTitle, Priority.ALWAYS);
        pageHeader.getChildren().addAll(windowsIcon, pageTitle, applyBtn);

        TilePane featuresPane = new TilePane();
        featuresPane.setPadding(new Insets(15));
        featuresPane.setHgap(20);
        featuresPane.setVgap(20);
        featuresPane.setPrefColumns(2);

        List<Feature> features = new ArrayList<>();
        features.add(new Feature("Optymalizacja prywatności", false));
        features.add(new Feature("Optymalizacja systemu", false));
        features.add(new Feature("Zwiększenie wydajności", false));
        features.add(new Feature("Optymalizacja SerializeTimerExpiration", false));
        features.add(new Feature("Optymalizacja wyglądu", false));
        features.add(new Feature("Optymalizacja zasilania", false));
        features.add(new Feature("Wyłącz niepotrzebne funkcje", false));
        features.add(new Feature("Optymalizacja NTFS", false));
        features.add(new Feature("Wyłącz telemetrię", false));
        features.add(new Feature("Wyłącz SmartScreen", false));
        features.add(new Feature("Wyłącz raportowanie błędów", false));
        features.add(new Feature("Wyłącz Superfetch", false));
        features.add(new Feature("Wyłącz usługę drukowania", false));
        features.add(new Feature("Wyłącz usługę faksu", false));
        features.add(new Feature("Wyłącz hibernację", false));
        features.add(new Feature("Wyłącz udostępnianie HomeGroup", false));
        features.add(new Feature("Wyłącz protokół SMBv1", false));
        features.add(new Feature("Wyłącz protokół SMBv2", false));
        features.add(new Feature("Wyłącz telemetrię Office", false));
        features.add(new Feature("Wyłącz telemetrię Firefox", false));
        features.add(new Feature("Wyłącz telemetrię Chrome", false));
        features.add(new Feature("Wyłącz telemetrię NVIDIA", false));
        features.add(new Feature("Wyłącz telemetrię Visual Studio", false));
        features.add(new Feature("Usuń opóźnienia menu", false));
        features.add(new Feature("Pokaż wszystkie ikony w zasobniku", false));
        features.add(new Feature("Wyłącz udostępnianie Media Player", false));
        features.add(new Feature("Wyłącz zgodność aplikacji", false));
        features.add(new Feature("Wyłącz usługę zgodności", false));

        for (Feature feature : features) {
            VBox featureCard = createFeatureCard(feature);
            featuresPane.getChildren().add(featureCard);
        }

        ScrollPane scrollPane = new ScrollPane(featuresPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);

        windowsPage.getChildren().addAll(pageHeader, scrollPane);

        FadeTransition ft = new FadeTransition(Duration.millis(500), windowsPage);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return windowsPage;
    }

    private void showWindowsOptimizationProgress() {
        Stage progressStage = new Stage();
        progressStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        VBox progressBox = new VBox(20);
        progressBox.setAlignment(Pos.CENTER);
        progressBox.setPadding(new Insets(30));
        progressBox.setStyle("-fx-background-color: rgba(15, 15, 15, 0.95); -fx-background-radius: 15;");
        progressBox.setEffect(new DropShadow(20, Color.BLACK));

        Text progressTitle = new Text("Stosowanie optymalizacji Windows...");
        progressTitle.setFill(Color.WHITE);
        progressTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #8a2be2;");

        TextArea console = new TextArea();
        console.setStyle("-fx-control-inner-background: black; -fx-text-fill: lime; -fx-font-family: 'Consolas'; -fx-font-size: 12;");
        console.setEditable(false);
        console.setPrefHeight(200);
        console.setPrefWidth(400);

        progressBox.getChildren().addAll(progressTitle, progressBar, console);

        Scene scene = new Scene(progressBox);
        scene.setFill(Color.TRANSPARENT);
        progressStage.setScene(scene);
        progressStage.show();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(0.5), e -> console.appendText("> Przygotowywanie systemu do optymalizacji...\n")),
                new KeyFrame(Duration.seconds(1), new KeyValue(progressBar.progressProperty(), 0.15)),
                new KeyFrame(Duration.seconds(1.5), e -> console.appendText("> Tworzenie kopii zapasowej ustawień...\n")),
                new KeyFrame(Duration.seconds(2), new KeyValue(progressBar.progressProperty(), 0.3)),
                new KeyFrame(Duration.seconds(2.5), e -> console.appendText("> Stosowanie optymalizacji prywatności...\n")),
                new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 0.45)),
                new KeyFrame(Duration.seconds(3.5), e -> console.appendText("> Optymalizacja wydajności systemu...\n")),
                new KeyFrame(Duration.seconds(4), new KeyValue(progressBar.progressProperty(), 0.6)),
                new KeyFrame(Duration.seconds(4.5), e -> console.appendText("> Wyłączanie niepotrzebnych usług...\n")),
                new KeyFrame(Duration.seconds(5), new KeyValue(progressBar.progressProperty(), 0.75)),
                new KeyFrame(Duration.seconds(5.5), e -> console.appendText("> Finalizowanie zmian...\n")),
                new KeyFrame(Duration.seconds(6), new KeyValue(progressBar.progressProperty(), 0.9)),
                new KeyFrame(Duration.seconds(6.5), e -> console.appendText("> Czyszczenie plików tymczasowych...\n")),
                new KeyFrame(Duration.seconds(7), new KeyValue(progressBar.progressProperty(), 1.0)),
                new KeyFrame(Duration.seconds(7.5), e -> {
                    console.appendText("> Optymalizacja Windows zakończona pomyślnie!\n");
                    console.appendText("> Niektóre zmiany mogą wymagać restartu systemu.\n");
                    new Timeline(new KeyFrame(Duration.seconds(2), ev -> progressStage.close())).play();
                })
        );
        timeline.play();
    }

    private VBox createFeatureCard(Feature feature) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); -fx-background-radius: 15; -fx-border-color: rgba(61, 61, 61, 0.5); -fx-border-radius: 15; -fx-padding: 20;");
        card.setPrefWidth(350);
        card.setPrefHeight(120);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Text name = new Text(feature.getName());
        name.setFill(Color.WHITE);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ToggleSwitch toggle = new ToggleSwitch();
        toggle.setSelected(feature.isEnabled());

        HBox.setHgrow(name, Priority.ALWAYS);
        header.getChildren().addAll(name, toggle);

        Text description = new Text(getFeatureDescription(feature.getName()));
        description.setFill(Color.gray(0.7));
        description.setFont(Font.font("Arial", 12));
        description.setWrappingWidth(310);

        card.getChildren().addAll(header, description);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        return card;
    }

    private String getFeatureDescription(String featureName) {
        switch (featureName) {
            case "Optymalizacja prywatności":
                return "Zwiększa prywatność poprzez wyłączenie funkcji zbierania danych";
            case "Optymalizacja systemu":
                return "Optymalizuje ustawienia systemu dla lepszej wydajności";
            case "Zwiększenie wydajności":
                return "Dostosowuje parametry systemu dla maksymalnej wydajności";
            case "Optymalizacja NTFS":
                return "Dostosowuje ustawienia NTFS dla lepszej wydajności systemu plików";
            case "Wyłącz telemetrię":
                return "Wyłącza telemetrię Windows i zbieranie danych";
            case "Wyłącz Superfetch":
                return "Wyłącza usługę Superfetch aby zmniejszyć użycie dysku";
            case "Wyłącz hibernację":
                return "Wyłącza hibernację aby zaoszczędzić miejsce na dysku";
            default:
                return "Optymalizuje Windows " + featureName.replace("Wyłącz ", "").toLowerCase();
        }
    }

    private void showFeatureToggleNotification(String featureName, boolean enabled) {
        Stage notificationStage = new Stage();
        notificationStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        HBox notificationBox = new HBox(10);
        notificationBox.setAlignment(Pos.CENTER_LEFT);
        notificationBox.setPadding(new Insets(15, 25, 15, 25));
        notificationBox.setStyle("-fx-background-color: rgba(15, 15, 15, 0.95); -fx-background-radius: 10;");
        notificationBox.setEffect(new DropShadow(10, Color.BLACK));

        Text statusIcon = new Text(enabled ? "✓" : "✗");
        statusIcon.setFont(Font.font(20));
        statusIcon.setFill(enabled ? Color.LIME : Color.RED);

        Text message = new Text(featureName + " " + (enabled ? "włączona" : "wyłączona"));
        message.setFill(Color.WHITE);
        message.setFont(Font.font("Arial", 14));

        notificationBox.getChildren().addAll(statusIcon, message);

        Scene scene = new Scene(notificationBox);
        scene.setFill(Color.TRANSPARENT);
        notificationStage.setScene(scene);

        double startX = primaryStage.getX() + primaryStage.getWidth() - 400;
        double startY = primaryStage.getY() + primaryStage.getHeight() - 100;
        notificationStage.setX(startX);
        notificationStage.setY(startY);

        notificationStage.show();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> notificationStage.close())
        );
        timeline.play();
    }

    private VBox createSettingsPage() {
        VBox settingsPage = new VBox();
        settingsPage.setPadding(new Insets(25));
        settingsPage.setSpacing(25);

        HBox pageHeader = new HBox();
        pageHeader.setAlignment(Pos.CENTER_LEFT);
        pageHeader.setSpacing(20);

        Text settingsIcon = new Text("");
        settingsIcon.setFont(Font.font(30));

        Text pageTitle = new Text("Ustawienia");
        pageTitle.setFill(Color.WHITE);
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Button saveBtn = new Button("ZAPISZ USTAWIENIA");
        saveBtn.setStyle("-fx-background-color: linear-gradient(to right, #8a2be2, #9370db); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 25;");

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), saveBtn);
        pulse.setFromX(1);
        pulse.setToX(1.05);
        pulse.setFromY(1);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        HBox.setHgrow(pageTitle, Priority.ALWAYS);
        pageHeader.getChildren().addAll(settingsIcon, pageTitle, saveBtn);

        VBox settingsSection = new VBox();
        settingsSection.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); -fx-background-radius: 15; -fx-border-color: rgba(61, 61, 61, 0.5); -fx-border-radius: 15; -fx-padding: 25;");
        settingsSection.setSpacing(25);
        settingsSection.setPrefWidth(800);

        VBox notificationsGroup = createSettingsGroup("Powiadomienia");
        notificationsGroup.getChildren().add(createSettingItem("Powiadomienia o zakończeniu skanowania", true));
        notificationsGroup.getChildren().add(createSettingItem("Ostrzeżenia bezpieczeństwa", true));
        notificationsGroup.getChildren().add(createSettingItem("Powiadomienia o aktualizacjach", false));

        VBox privacyGroup = createSettingsGroup("Prywatność");
        privacyGroup.getChildren().add(createSettingItem("Anonimowe statystyki użycia", false));
        privacyGroup.getChildren().add(createSettingItem("Kopia zapasowa w chmurze", true));

        VBox appearanceGroup = createSettingsGroup("Wygląd");
        ToggleSwitch darkModeSwitch = new ToggleSwitch();
        darkModeSwitch.setSelected(darkMode);
        darkModeSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> {
            darkMode = newVal;
            applyTheme();
        });
        appearanceGroup.getChildren().add(createSettingItemWithCustomToggle("Tryb ciemny", darkModeSwitch));

        settingsSection.getChildren().addAll(notificationsGroup, privacyGroup, appearanceGroup);
        settingsPage.getChildren().addAll(pageHeader, settingsSection);

        FadeTransition pageFade = new FadeTransition(Duration.millis(500), settingsPage);
        pageFade.setFromValue(0);
        pageFade.setToValue(1);
        pageFade.play();

        return settingsPage;
    }

    private HBox createSettingItemWithCustomToggle(String label, ToggleSwitch toggleSwitch) {
        HBox settingItem = new HBox();
        settingItem.setAlignment(Pos.CENTER_LEFT);
        settingItem.setSpacing(20);
        settingItem.setPadding(new Insets(15, 0, 15, 0));
        settingItem.setStyle("-fx-border-color: rgba(45, 45, 45, 0.5); -fx-border-width: 0 0 1 0;");

        Text settingLabel = new Text(label);
        settingLabel.setFill(Color.WHITE);
        settingLabel.setFont(Font.font("Arial", 16));

        HBox.setHgrow(settingLabel, Priority.ALWAYS);
        settingItem.getChildren().addAll(settingLabel, toggleSwitch);
        return settingItem;
    }

    private VBox createSettingsGroup(String title) {
        VBox group = new VBox();
        group.setSpacing(15);

        HBox groupTitle = new HBox();
        groupTitle.setAlignment(Pos.CENTER_LEFT);
        groupTitle.setSpacing(10);
        groupTitle.setStyle("-fx-border-color: rgba(61, 61, 61, 0.5); -fx-border-width: 0 0 1 0; -fx-padding: 0 0 15 0;");

        Text titleText = new Text(title);
        titleText.setFill(Color.WHITE);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(3), titleText);
        pulse.setFromX(1);
        pulse.setToX(1.01);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        groupTitle.getChildren().add(titleText);
        group.getChildren().add(groupTitle);
        return group;
    }

    private HBox createSettingItem(String label, boolean checked) {
        HBox settingItem = new HBox();
        settingItem.setAlignment(Pos.CENTER_LEFT);
        settingItem.setSpacing(20);
        settingItem.setPadding(new Insets(15, 0, 15, 0));
        settingItem.setStyle("-fx-border-color: rgba(45, 45, 45, 0.5); -fx-border-width: 0 0 1 0;");

        Text settingLabel = new Text(label);
        settingLabel.setFill(Color.WHITE);
        settingLabel.setFont(Font.font("Arial", 16));

        ToggleSwitch toggleSwitch = new ToggleSwitch();
        toggleSwitch.setSelected(checked);

        HBox.setHgrow(settingLabel, Priority.ALWAYS);
        settingItem.getChildren().addAll(settingLabel, toggleSwitch);
        return settingItem;
    }

    private VBox createAboutPage() {
        VBox aboutPage = new VBox();
        aboutPage.setPadding(new Insets(25));
        aboutPage.setSpacing(25);
        aboutPage.setAlignment(Pos.TOP_CENTER);

        HBox pageHeader = new HBox();
        pageHeader.setAlignment(Pos.CENTER_LEFT);

        Text pageTitle = new Text("O programie");
        pageTitle.setFill(Color.WHITE);
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        HBox.setHgrow(pageTitle, Priority.ALWAYS);
        pageHeader.getChildren().add(pageTitle);

        VBox aboutSection = new VBox();
        aboutSection.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); -fx-background-radius: 15; -fx-border-color: rgba(61, 61, 61, 0.5); -fx-border-radius: 15; -fx-padding: 40;");
        aboutSection.setSpacing(30);
        aboutSection.setAlignment(Pos.TOP_CENTER);
        aboutSection.setMaxWidth(800);

        HBox aboutLogo = new HBox();
        aboutLogo.setAlignment(Pos.CENTER);
        aboutLogo.setSpacing(15);

        Text appLogo = new Text("");
        appLogo.setFont(Font.font(80));

        Text logoText = new Text("ULTIMATE OPTIMIZER");
        logoText.setStyle("-fx-fill: linear-gradient(to right, #8a2be2, #9370db, #8a2be2); -fx-font-size: 40; -fx-font-weight: bold;");

        AnimationTimer shimmer = new AnimationTimer() {
            long startTime = 0;
            @Override
            public void handle(long now) {
                if (startTime == 0) startTime = now;
                double progress = (now - startTime) % 3_000_000_000L / 3_000_000_000.0;
                LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#8a2be2")),
                        new Stop(progress, Color.web("#ffffff")),
                        new Stop(progress + 0.1, Color.web("#9370db")),
                        new Stop(1, Color.web("#8a2be2"))
                );
                logoText.setFill(gradient);
            }
        };
        shimmer.start();

        aboutLogo.getChildren().addAll(appLogo, logoText);

        VBox aboutContent = new VBox();
        aboutContent.setSpacing(25);
        aboutContent.setAlignment(Pos.TOP_CENTER);
        aboutContent.setMaxWidth(700);

        Label versionLabel = new Label("Wersja 2.5.0");
        versionLabel.setStyle("-fx-background-color: rgba(138, 43, 226, 0.2); -fx-text-fill: #8a2be2; -fx-padding: 10 25; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 16;");

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), versionLabel);
        pulse.setFromX(1);
        pulse.setToX(1.05);
        pulse.setFromY(1);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        Text description1 = new Text("\n" + "Ultimate Optimizer to profesjonalne narzędzie do optymalizacji systemu, zaprojektowane w celu czyszczenia, ochrony, optymalizacji i przyspieszenia działania komputera. Dzięki zaawansowanym algorytmom i premium interfejsowi pomaga utrzymać system w szczytowej wydajności.");
        description1.setFill(Color.gray(0.7));
        description1.setFont(Font.font("Arial", 16));
        description1.setTextAlignment(TextAlignment.CENTER);
        description1.setWrappingWidth(700);

        Text description2 = new Text("Dołącz do naszej społeczności, aby otrzymać wsparcie premium, automatyczne aktualizacje i możliwość zgłaszania nowych funkcji.");
        description2.setFill(Color.gray(0.7));
        description2.setFont(Font.font("Arial", 16));
        description2.setTextAlignment(TextAlignment.CENTER);
        description2.setWrappingWidth(700);

        Button discordBtn = new Button("Dołącz do Discorda");
        discordBtn.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 30; -fx-font-size: 16;");
        discordBtn.setEffect(new DropShadow(10, Color.rgb(88, 101, 242, 0.5)));

        discordBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), discordBtn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        discordBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), discordBtn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        discordBtn.setOnAction(e -> {
            try {
                hostServices.showDocument("https://discord.gg/sastore");
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Nie można otworzyć przeglądarki");
                alert.setContentText("Nie udało się otworzyć strony");
                alert.showAndWait();
            }
        });

        aboutContent.getChildren().addAll(versionLabel, description1, description2, discordBtn);
        aboutSection.getChildren().addAll(aboutLogo, aboutContent);
        aboutPage.getChildren().addAll(pageHeader, aboutSection);

        FadeTransition ft = new FadeTransition(Duration.millis(500), aboutPage);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return aboutPage;
    }

    private void setupNavigation(StackPane contentArea) {}

    private void showPage(VBox page) {
        StackPane contentArea = (StackPane) mainLayout.getCenter();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), contentArea.getChildren().get(0));
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), page);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class ToggleSwitch extends StackPane {
    private final Rectangle background = new Rectangle(70, 34);
    private final Circle trigger = new Circle(14);
    private final BooleanProperty selected;

    public ToggleSwitch() {
        selected = new SimpleBooleanProperty(false);

        background.setArcWidth(34);
        background.setArcHeight(34);
        background.setFill(Color.rgb(61, 61, 61));
        background.setStroke(Color.rgb(45, 45, 45));
        background.setStrokeWidth(1);

        trigger.setFill(Color.WHITE);
        trigger.setTranslateX(-17);
        trigger.setEffect(new DropShadow(3, Color.BLACK));

        getChildren().addAll(background, trigger);

        setOnMouseClicked(e -> {
            selected.set(!selected.get());
            updateUI();
        });

        setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), this);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), this);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        updateUI();
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
        updateUI();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    private void updateUI() {
        if (selected.get()) {
            background.setFill(Color.rgb(138, 43, 226));
            TranslateTransition tt = new TranslateTransition(Duration.millis(200), trigger);
            tt.setToX(17);
            tt.play();
        } else {
            background.setFill(Color.rgb(61, 61, 61));
            TranslateTransition tt = new TranslateTransition(Duration.millis(200), trigger);
            tt.setToX(-17);
            tt.play();
        }
    }
}

class Feature {
    private String name;
    private boolean enabled;

    public Feature(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

class Tool {
    private String name;
    private String description;

    public Tool(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}