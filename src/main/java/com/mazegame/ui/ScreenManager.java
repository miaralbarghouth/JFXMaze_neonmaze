package com.mazegame.ui;

import com.mazegame.logic.AuthManager;
import com.mazegame.logic.LeaderboardManager;
import com.mazegame.logic.MazeManager;
import com.mazegame.logic.MusicManager;
import com.mazegame.logic.SoundManager;
import com.mazegame.model.Maze;
import com.mazegame.model.ScoreRecord;
import com.mazegame.ui.components.GameView;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ScreenManager {
    private final Stage stage;
    private final AuthManager authManager;
    private StackPane mainStack;
    private javafx.scene.canvas.Canvas bgCanvas;
    private Scene scene;
    private javafx.scene.Node previousNode;
    private int mazeSize = 21;
    private String currentUsername = "Player";
    private double masterVolume = 0.5;
    private double musicVolume = 0.5;
    private boolean musicOn = true;

    public ScreenManager(Stage stage) {
        this.stage = stage;
        this.authManager = new AuthManager();
        mainStack = new StackPane();
        mainStack.getStyleClass().add("main-container");
        setupBackground();
    }

    // Background maze effect
    private void setupBackground() {
        bgCanvas = new javafx.scene.canvas.Canvas(1100, 1000);
        var gc = bgCanvas.getGraphicsContext2D();
        Maze bgMaze = new Maze(40, 40);
        MazeManager.generateMaze(bgMaze);
        double cellSize = 28;
        gc.setStroke(Color.web("#00eeff", 0.15));
        gc.setLineWidth(1);
        for (int y = 0; y < bgMaze.getHeight(); y++) {
            for (int x = 0; x < bgMaze.getWidth(); x++) {
                if (bgMaze.getCell(x, y) == Maze.WALL) {
                    gc.strokeRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    // Title styling
    private VBox createGameTitle() {
        VBox titleBox = new VBox(-15);
        titleBox.setAlignment(Pos.CENTER);
        
        Label neonLbl = new Label("NEON");
        neonLbl.getStyleClass().add("title-neon");
        
        Label mazeLbl = new Label("MAZE");
        mazeLbl.getStyleClass().add("title-maze");
        
        titleBox.getChildren().addAll(neonLbl, mazeLbl);
        VBox.setMargin(mazeLbl, new Insets(0, 0, 10, 0));
        return titleBox;
    }

    // Login screen logic
    public void showLogin() {
        VBox root = new VBox(30);
        root.getStyleClass().add("root");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setBackground(null);

        VBox title = createGameTitle();
        
        VBox fields = new VBox(15);
        fields.setAlignment(Pos.CENTER);
        
        TextField userField = new TextField();
        userField.promptTextProperty().bind(LanguageManager.getStringProperty("login.username"));
        userField.setMaxWidth(350);
        
        PasswordField passField = new PasswordField();
        passField.promptTextProperty().bind(LanguageManager.getStringProperty("login.password"));
        passField.setMaxWidth(350);
        
        fields.getChildren().addAll(userField, passField);
        
        Label loginErrorLbl = new Label();
        loginErrorLbl.setTextFill(Color.RED);
        loginErrorLbl.setFont(Font.font(16));
        
        HBox registerLinkBox = new HBox(5);
        registerLinkBox.setAlignment(Pos.CENTER);
        
        Label noAccLbl = new Label();
        noAccLbl.textProperty().bind(LanguageManager.getStringProperty("login.noAccount"));
        noAccLbl.setStyle("-fx-effect: none; -fx-text-fill: #555; -fx-font-size: 18px;");
        
        Hyperlink createLink = new Hyperlink();
        createLink.textProperty().bind(LanguageManager.getStringProperty("login.createLink"));
        createLink.getStyleClass().add("hyperlink");
        createLink.setOnAction(e -> showRegister());
        
        registerLinkBox.getChildren().addAll(noAccLbl, createLink);
        
        Button loginBtn = new Button();
        loginBtn.textProperty().bind(LanguageManager.getStringProperty("login.button"));
        loginBtn.setMinWidth(250);
        loginBtn.setOnAction(e -> {
            String user = userField.getText().trim();
            String pass = passField.getText();
            if (authManager.login(user, pass)) {
                currentUsername = user;
                showMenu();
            } else {
                loginErrorLbl.textProperty().bind(LanguageManager.getStringProperty("login.error"));
            }
        });
        
        root.getChildren().addAll(title, fields, loginErrorLbl, registerLinkBox, loginBtn);

        Label footer = new Label("v1.0 Developed by Miar A.");
        footer.getStyleClass().add("login-footer");
        
        StackPane container = new StackPane();
        container.getChildren().addAll(root, footer);
        StackPane.setAlignment(footer, Pos.BOTTOM_RIGHT);
        
        setScene(container);
    }

    // Registration screen
    public void showRegister() {
        VBox root = new VBox(25);
        root.getStyleClass().add("root");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));

        Label title = new Label();
        title.textProperty().bind(LanguageManager.getStringProperty("register.title"));
        title.getStyleClass().add("title-highlight");

        VBox fields = new VBox(15);
        fields.setAlignment(Pos.CENTER);
        
        TextField userField = new TextField();
        userField.promptTextProperty().bind(LanguageManager.getStringProperty("login.username"));
        userField.setMaxWidth(350);
        
        PasswordField passField = new PasswordField();
        passField.promptTextProperty().bind(LanguageManager.getStringProperty("login.password"));
        passField.setMaxWidth(350);
        
        PasswordField confirmField = new PasswordField();
        confirmField.promptTextProperty().bind(LanguageManager.getStringProperty("register.confirmPassword"));
        confirmField.setMaxWidth(350);
        
        fields.getChildren().addAll(userField, passField, confirmField);
        
        Label errorLbl = new Label();
        errorLbl.setTextFill(Color.RED);
        
        Button registerBtn = new Button();
        registerBtn.textProperty().bind(LanguageManager.getStringProperty("register.button"));
        registerBtn.setMinWidth(250);
        registerBtn.setOnAction(e -> {
            String user = userField.getText().trim();
            String pass = passField.getText();
            String confirm = confirmField.getText();
            if (user.isEmpty() || pass.isEmpty()) { 
                errorLbl.textProperty().bind(LanguageManager.getStringProperty("register.error.empty")); 
                return; 
            }
            if (!pass.equals(confirm)) { 
                errorLbl.textProperty().bind(LanguageManager.getStringProperty("register.error.match")); 
                return; 
            }
            if (authManager.register(user, pass)) {
                showLogin();
            } else {
                errorLbl.textProperty().bind(LanguageManager.getStringProperty("register.error.exists"));
            }
        });
        
        Button backBtn = new Button();
        backBtn.textProperty().bind(LanguageManager.getStringProperty("settings.back"));
        backBtn.getStyleClass().add("button-secondary");
        backBtn.setOnAction(e -> showLogin());
        
        root.getChildren().addAll(title, fields, errorLbl, registerBtn, backBtn);
        setScene(root);
    }

    // Main menu navigation
    public void showMenu() {
        MusicManager.setMuted(!musicOn);
        MusicManager.play();
        VBox root = new VBox(35);
        root.getStyleClass().add("root");
        root.setAlignment(Pos.CENTER);
        
        VBox title = createGameTitle();
        VBox buttons = new VBox(20);
        buttons.setAlignment(Pos.CENTER);
        
        Button startBtn = createMenuButton("menu.start", e -> showSizeSelection());
        Button settingsBtn = createMenuButton("menu.settings", e -> showSettings());
        Button leaderBtn = createMenuButton("menu.leaderboard", e -> showLeaderboard());
        
        Button exitBtn = createMenuButton("menu.exit", e -> stage.close());
        exitBtn.getStyleClass().add("button-danger");
        
        buttons.getChildren().addAll(startBtn, settingsBtn, leaderBtn, exitBtn);
        
        Button logoutBtn = new Button();
        logoutBtn.textProperty().bind(LanguageManager.getStringProperty("menu.logout"));
        logoutBtn.setStyle("-fx-background-color: #aa0000; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 15 5 15; -fx-border-radius: 5;");
        logoutBtn.setOnAction(e -> logout());
        
        root.getChildren().addAll(title, buttons, logoutBtn);
        setScene(root);
    }

    private void logout() {
        currentUsername = "Player";
        previousNode = null;
        MusicManager.setMuted(true);
        showLogin();
    }

    /**
     * Popup for choosing the maze size before starting the game.
     */
    // Size selection
    private void showSizeSelection() {
        VBox overlay = new VBox(25);
        overlay.setAlignment(Pos.CENTER);
        overlay.getStyleClass().add("popup-container");
        overlay.setMaxSize(400, 350);

        Label title = new Label();
        title.textProperty().bind(LanguageManager.getStringProperty("game.size.title"));
        title.getStyleClass().add("popup-title");

        VBox sliderBox = new VBox(10);
        sliderBox.setAlignment(Pos.CENTER);
        
        Label sizeValLbl = new Label(LanguageManager.get("game.size.current") + mazeSize + "x" + mazeSize);
        sizeValLbl.setStyle("-fx-text-fill: #00eeff; -fx-font-family: 'Monospaced'; -fx-font-size: 20px;");
        
        Slider sizeSlider = new Slider(11, 51, mazeSize);
        sizeSlider.getStyleClass().add("settings-slider");
        sizeSlider.setBlockIncrement(2);
        sizeSlider.valueProperty().addListener((obs, ov, nv) -> {
            int val = nv.intValue();
            if (val % 2 == 0) val = val > ov.intValue() ? val + 1 : val - 1;
            mazeSize = val;
            sizeValLbl.setText(LanguageManager.get("game.size.current") + mazeSize + "x" + mazeSize);
        });
        
        sliderBox.getChildren().addAll(sizeValLbl, sizeSlider);

        Button playBtn = new Button();
        playBtn.textProperty().bind(LanguageManager.getStringProperty("game.size.play"));
        playBtn.getStyleClass().add("settings-button");
        playBtn.setMinWidth(180);
        playBtn.setStyle("-fx-font-size: 22px; -fx-border-width: 2;");
        playBtn.setOnAction(e -> {
            mainStack.getChildren().remove(overlay);
            startGame();
        });

        Button cancelBtn = new Button();
        cancelBtn.textProperty().bind(LanguageManager.getStringProperty("game.size.cancel"));
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #777; -fx-font-size: 14px;");
        cancelBtn.setOnAction(e -> mainStack.getChildren().remove(overlay));

        overlay.getChildren().addAll(title, sliderBox, playBtn, cancelBtn);
        mainStack.getChildren().add(overlay);
    }

    private Button createMenuButton(String key, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button();
        btn.textProperty().bind(LanguageManager.getStringProperty(key));
        btn.setMinWidth(350);
        btn.setOnAction(handler);
        return btn;
    }

    // Leaderboard display
    public void showLeaderboard() {
        VBox root = new VBox(25);
        root.getStyleClass().add("root");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label();
        title.textProperty().bind(LanguageManager.getStringProperty("leaderboard.title"));
        title.getStyleClass().add("title-highlight");
        title.setTextFill(Color.web("#00ffcc"));

        ListView<ScoreRecord> listView = new ListView<>();
        listView.setMaxWidth(850);
        listView.setMinHeight(550);
        
        var scores = LeaderboardManager.loadScores();
        if (scores.isEmpty()) { 
            root.getChildren().add(new Label(LanguageManager.get("leaderboard.empty"))); 
        } else {
            listView.getItems().addAll(scores);
            listView.setCellFactory(param -> new ListCell<>() {
                private final Button deleteBtn = new Button("X");
                private final HBox hBox = new HBox(15);
                private final Label rankLabel = new Label();
                private final Label textLabel = new Label();
                {
                    deleteBtn.setStyle("-fx-background-color: #aa0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 2 8;");
                    deleteBtn.setOnAction(e -> {
                        ScoreRecord item = getItem();
                        if (item != null) { 
                            LeaderboardManager.deleteScore(item); 
                            listView.getItems().remove(item); 
                        }
                    });
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    rankLabel.setMinWidth(40);
                    rankLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
                    textLabel.setStyle("-fx-text-fill: #00ffcc; -fx-font-size: 16px;");
                    hBox.getChildren().addAll(deleteBtn, rankLabel, textLabel);
                }
                @Override protected void updateItem(ScoreRecord item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        // Special colors and medals for top 3
                        int rowIndex = getIndex();
                        if (rowIndex == 0) {
                            rankLabel.setText("1. 🥇");
                            rankLabel.setTextFill(Color.GOLD);
                            textLabel.setStyle("-fx-text-fill: GOLD; -fx-font-weight: bold; -fx-font-size: 18px;");
                        } else if (rowIndex == 1) {
                            rankLabel.setText("2. 🥈");
                            rankLabel.setTextFill(Color.SILVER);
                            textLabel.setStyle("-fx-text-fill: SILVER; -fx-font-size: 17px;");
                        } else if (rowIndex == 2) {
                            rankLabel.setText("3. 🥉");
                            rankLabel.setTextFill(Color.web("#CD7F32")); // Bronze
                            textLabel.setStyle("-fx-text-fill: #CD7F32; -fx-font-size: 16px;");
                        } else {
                            rankLabel.setText((rowIndex + 1) + ".");
                            rankLabel.setTextFill(Color.GRAY);
                            textLabel.setStyle("-fx-text-fill: #00ffcc; -fx-font-size: 16px;");
                        }

                        textLabel.setText(item.toString());
                        setGraphic(hBox);
                        setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-border-color: rgba(0, 255, 204, 0.1); -fx-border-width: 0 0 1 0;");
                    }
                }
            });
            root.getChildren().add(listView);
        }
        
        Button backBtn = new Button();
        backBtn.textProperty().bind(LanguageManager.getStringProperty("settings.back"));
        backBtn.setMinWidth(200);
        backBtn.setOnAction(e -> showMenu());
        
        root.getChildren().add(backBtn);
        setScene(root);
    }

    // Settings
    public void showSettings() {
        VBox root = new VBox(20);
        root.getStyleClass().add("root");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        VBox outerContainer = new VBox(20);
        outerContainer.setAlignment(Pos.CENTER);
        outerContainer.setPadding(new Insets(30));
        outerContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.95); -fx-border-color: #ffd700; -fx-border-width: 2;");
        outerContainer.setMaxWidth(650);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(650);
        
        VBox scrollContent = new VBox(30);
        scrollContent.setPadding(new Insets(20));
        scrollContent.setAlignment(Pos.CENTER);

        Label mainTitle = new Label();
        mainTitle.textProperty().bind(LanguageManager.getStringProperty("settings.title"));
        mainTitle.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        mainTitle.setTextFill(Color.web("#ffd700"));
        mainTitle.setAlignment(Pos.CENTER);
        mainTitle.setMaxWidth(Double.MAX_VALUE);
        scrollContent.getChildren().add(mainTitle);

        // --- GENERAL SECTION ---
        VBox genBox = createBoxedSection("GENERAL");
        
        genBox.getChildren().add(createVolumeControl("settings.masterVol", masterVolume, nv -> {
            masterVolume = nv;
            SoundManager.setVolume(masterVolume);
        }));
        
        genBox.getChildren().add(createVolumeControl("settings.musicVol", musicVolume, nv -> {
            musicVolume = nv;
            MusicManager.setVolume(musicVolume);
        }));

        Button musicToggleBtn = new Button();
        updateMusicToggleButton(musicToggleBtn);
        musicToggleBtn.getStyleClass().add("settings-button");
        musicToggleBtn.setOnAction(e -> {
            musicOn = !musicOn;
            MusicManager.setMuted(!musicOn);
            updateMusicToggleButton(musicToggleBtn);
        });
        
        HBox langButtons = new HBox(15);
        langButtons.setAlignment(Pos.CENTER);
        Button trBtn = createSettingsButton("Turkish", e -> LanguageManager.setLanguage(LanguageManager.Language.TR));
        Button enBtn = createSettingsButton("English", e -> LanguageManager.setLanguage(LanguageManager.Language.EN));
        trBtn.setMinWidth(140);
        enBtn.setMinWidth(140);
        langButtons.getChildren().addAll(trBtn, enBtn);
        
        genBox.getChildren().addAll(musicToggleBtn, langButtons);

        // --- CONTROLS SECTION ---
        VBox controlBox = createBoxedSection("settings.controls");
        Label controlsInfo = new Label();
        controlsInfo.textProperty().bind(LanguageManager.getStringProperty("settings.controlsMsg"));
        controlsInfo.getStyleClass().add("settings-info-text");
        controlsInfo.setTextAlignment(TextAlignment.CENTER);
        controlBox.getChildren().add(controlsInfo);

        // --- ACCOUNT SECTION ---
        VBox accountBox = createBoxedSection("settings.account");
        Button changeNameBtn = createSettingsButton(LanguageManager.get("settings.changeUser"), e -> {
            showInputPopup("settings.changeUser", "settings.newUser", n -> {
                if (authManager.renameUser(currentUsername, n)) {
                    currentUsername = n;
                }
            });
        });
        Button changePassBtn = createSettingsButton(LanguageManager.get("settings.changePass"), e -> {
            showInputPopup("settings.changePass", "settings.newPass", p -> {
                authManager.changePassword(currentUsername, p);
            });
        });
        
        Button deleteBtn = createSettingsButton(LanguageManager.get("settings.deleteAcc"), e -> { 
            authManager.deleteUser(currentUsername);
            logout(); 
        });
        deleteBtn.getStyleClass().add("button-danger");
        deleteBtn.setStyle("-fx-font-size: 16px;"); // Slight override for settings context
        
        accountBox.getChildren().addAll(changeNameBtn, changePassBtn, deleteBtn);

        // --- CREDITS SECTION ---
        VBox creditsBox = createBoxedSection("settings.credits");
        Button creditsBtn = createSettingsButton("Credits Info", e -> showCredits());
        HBox socialLinks = new HBox(15);
        socialLinks.setAlignment(Pos.CENTER);
        
        Button githubBtn = createSettingsButton("GitHub", e -> openUrl("https://github.com/miaralbarghouth"));
        Button linkedInBtn = createSettingsButton("LinkedIn", e -> openUrl("https://www.linkedin.com/in/miar-albarghouth"));
        githubBtn.setStyle("-fx-border-color: #3399ff; -fx-text-fill: #3399ff;");
        linkedInBtn.setStyle("-fx-border-color: #0077b5; -fx-text-fill: #0077b5;");
        
        socialLinks.getChildren().addAll(githubBtn, linkedInBtn);
        creditsBox.getChildren().addAll(creditsBtn, socialLinks);

        scrollContent.getChildren().addAll(genBox, controlBox, accountBox, creditsBox);
        scroll.setContent(scrollContent);
        
        Button backBtn = createSettingsButton(LanguageManager.get("settings.back"), e -> {
            if (previousNode instanceof VBox && ((VBox)previousNode).getStyleClass().contains("root")) {
                // If the previous node was a menu, show menu again
                showMenu();
            } else {
                // Otherwise (e.g. from game), return to previous state
                setScene(previousNode);
            }
        });
        backBtn.setMinWidth(300);
        backBtn.setStyle("-fx-text-fill: white; -fx-border-color: white;");

        outerContainer.getChildren().addAll(scroll, backBtn);
        root.getChildren().add(outerContainer);
        setScene(root);
    }

    private void showInputPopup(String titleKey, String placeholderKey, java.util.function.Consumer<String> onSave) {
        VBox overlay = new VBox(25);
        overlay.setAlignment(Pos.CENTER);
        overlay.getStyleClass().add("popup-container");
        overlay.setMaxSize(450, 280);

        Label title = new Label();
        title.textProperty().bind(LanguageManager.getStringProperty(titleKey));
        title.getStyleClass().add("popup-title");

        TextField input = new TextField();
        input.promptTextProperty().bind(LanguageManager.getStringProperty(placeholderKey));
        input.setMaxWidth(350);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button saveBtn = new Button();
        saveBtn.textProperty().bind(LanguageManager.getStringProperty("settings.save"));
        saveBtn.getStyleClass().add("settings-button");
        saveBtn.setMinWidth(140);
        saveBtn.setOnAction(e -> {
            String val = input.getText();
            if (val != null && !val.trim().isEmpty()) {
                onSave.accept(val.trim());
                mainStack.getChildren().remove(overlay);
            }
        });

        Button backBtn = new Button();
        backBtn.textProperty().bind(LanguageManager.getStringProperty("settings.back"));
        backBtn.getStyleClass().add("button-secondary");
        backBtn.setStyle("-fx-font-size: 14px; -fx-min-width: 120px;");
        backBtn.setOnAction(e -> mainStack.getChildren().remove(overlay));

        buttons.getChildren().addAll(saveBtn, backBtn);
        overlay.getChildren().addAll(title, input, buttons);
        mainStack.getChildren().add(overlay);
    }

    private VBox createVolumeControl(String labelKey, double initialVal, java.util.function.Consumer<Double> onValueChange) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        Label lbl = new Label();
        lbl.textProperty().bind(LanguageManager.getStringProperty(labelKey));
        lbl.getStyleClass().add("settings-label");
        
        Slider slider = new Slider(0, 1, initialVal);
        slider.getStyleClass().add("settings-slider");
        slider.valueProperty().addListener((obs, ov, nv) -> onValueChange.accept(nv.doubleValue()));
        
        box.getChildren().addAll(lbl, slider);
        return box;
    }

    // Credits display
    public void showCredits() {
        VBox root = new VBox(20);
        root.getStyleClass().add("root");
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(750);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox content = new VBox(35);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        content.setStyle("-fx-background-color: black;");

        // Shadows
        javafx.scene.effect.DropShadow goldGlow = new javafx.scene.effect.DropShadow();
        goldGlow.setColor(Color.web("#FFD700", 0.4));
        goldGlow.setRadius(10);

        javafx.scene.effect.DropShadow blueGlow = new javafx.scene.effect.DropShadow();
        blueGlow.setColor(Color.web("#00EEFF", 0.3));
        blueGlow.setRadius(8);

        Label mainTitle = new Label("JFXMaze Credits");
        mainTitle.getStyleClass().add("credits-title");
        mainTitle.setAlignment(Pos.CENTER);
        mainTitle.setMaxWidth(Double.MAX_VALUE);
        
        content.getChildren().addAll(mainTitle, createCreditsSeparator());

        content.getChildren().addAll(
            createCreditsSection("DEVELOPER", "Miar Albarghouth"),
            createCreditsSeparator(),
            createCreditsSection("TOOLS & FRAMEWORKS", "Built with: JavaFX\nIDE: Visual Studio Code (VS Code)\nFocus: Algorithmic maze design and futuristic UI/UX"),
            createCreditsSeparator(),
            createCreditsSection("MUSIC & SOUND EFFECTS", "Background Music: Sourced from Pixabay\nHit & Point SFX: Created with Bfxr"),
            createCreditsSeparator(),
            createCreditsSection("SPECIAL THANKS", 
                "Prof. Dr. Ahmet Gurhanli, for his insightful lectures and invaluable guidance throughout this process.\n\n" +
                "Lith Albarghouth & Abdurrahman Albarghouth, for their immense support in my first project and for helping me learn from my mistakes.\n\n" +
                "Friends and peers, for their valuable feedback and encouragement.")
        );

        scroll.setContent(content);

        Button backBtn = new Button("BACK");
        backBtn.getStyleClass().add("settings-button");
        backBtn.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        backBtn.setOnAction(e -> showSettings());
        
        root.getChildren().addAll(scroll, backBtn);
        setScene(root);
    }

    private VBox createCreditsSection(String title, String detail) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER);
        
        Label t = new Label(title);
        t.getStyleClass().add("credits-title");
        t.setAlignment(Pos.CENTER);
        t.setMaxWidth(Double.MAX_VALUE);
        
        Label d = new Label(detail);
        d.getStyleClass().add("credits-detail");
        d.setWrapText(true);
        d.setMaxWidth(850);
        d.setTextAlignment(TextAlignment.CENTER);
        d.setAlignment(Pos.CENTER);
        
        section.getChildren().addAll(t, d);
        return section;
    }

    private Separator createCreditsSeparator() {
        Separator s = new Separator();
        s.getStyleClass().add("credits-separator");
        return s;
    }

    private VBox createBoxedSection(String titleKey) {
        VBox box = new VBox(15);
        box.getStyleClass().add("settings-section-box");
        Label title = new Label();
        if(titleKey.contains(".")) title.textProperty().bind(LanguageManager.getStringProperty(titleKey));
        else title.setText(titleKey);
        title.getStyleClass().add("settings-section-title");
        box.getChildren().add(title);
        return box;
    }

    private Button createSettingsButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> h) {
        Button b = new Button(text);
        b.getStyleClass().add("settings-button");
        b.setMinWidth(200);
        b.setOnAction(h);
        return b;
    }

    private void openUrl(String url) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else {
                Runtime.getRuntime().exec("xdg-open " + url);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateMusicToggleButton(Button btn) {
        String b = LanguageManager.get("settings.musicToggle");
        String s = musicOn ? LanguageManager.get("settings.on") : LanguageManager.get("settings.off");
        btn.setText(b + s);
    }

    private void startGame() {
        Maze maze = new Maze(mazeSize, mazeSize);
        MazeManager.generateMaze(maze);
        GameView gameView = new GameView(this, maze);
        setScene(gameView.getRoot());
    }

    private void setScene(javafx.scene.Node content) {
        // Save previous node if it's not the settings screen
        if (!mainStack.getChildren().isEmpty()) {
            javafx.scene.Node current = mainStack.getChildren().get(mainStack.getChildren().size() - 1);
            if (current != bgCanvas) {
                previousNode = current;
            }
        }
        
        mainStack.getChildren().clear();
        mainStack.getChildren().add(bgCanvas);
        mainStack.getChildren().add(content);
        if (scene == null) {
            scene = new Scene(mainStack, 1000, 900);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        }
        FadeTransition ft = new FadeTransition(Duration.millis(400), content);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        stage.show();
    }

    public String getCurrentUsername() { return currentUsername; }
}
