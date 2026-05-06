package com.mazegame.ui.components;

import com.mazegame.logic.LeaderboardManager;
import com.mazegame.logic.MazeManager;
import com.mazegame.logic.MusicManager;
import com.mazegame.logic.SoundManager;
import com.mazegame.model.Maze;
import com.mazegame.model.Maze.Point;
import com.mazegame.model.ScoreRecord;
import com.mazegame.ui.LanguageManager;
import com.mazegame.ui.ScreenManager;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class GameView {
    private final ScreenManager screenManager;
    private Maze maze;
    private final StackPane mainStack;
    private final BorderPane gameRoot;
    private final Canvas canvas;
    private final GraphicsContext gc;
    
    private Point playerPos;
    private int score = 0;
    private int steps = 0;
    private long startTime;
    private long totalPausedTime = 0;
    private long pauseStartedAt = 0;
    private boolean gameWon = false;
    private boolean isPaused = false;
    private List<Point> hintPath = null;

    private Label scoreLbl, stepsLbl, timeLbl;
    private VBox pauseMenu;
    private VBox winOverlay;

    // Static effects to avoid accumulation
    private static final Glow EXIT_GLOW = new Glow(0.8);
    private static final Glow PLAYER_GLOW = new Glow(0.9);
    private static final Glow HINT_GLOW = new Glow(0.5);

    public GameView(ScreenManager screenManager, Maze maze) {
        this.screenManager = screenManager;
        this.maze = maze;
        this.playerPos = new Point(1, 1);
        this.startTime = System.currentTimeMillis();

        this.mainStack = new StackPane();
        this.gameRoot = new BorderPane();
        this.gameRoot.getStyleClass().add("root");

        this.canvas = new Canvas(750, 750);
        this.gc = canvas.getGraphicsContext2D();
        
        setupHUD();
        this.gameRoot.setCenter(canvas);
        this.mainStack.getChildren().add(gameRoot);

        setupPauseMenu();
        setupWinOverlay();
        setupControls();
        startAnimation();
    }

    // Top status bar
    private void setupHUD() {
        HBox hud = new HBox(50);
        hud.setAlignment(Pos.CENTER);
        hud.setPadding(new Insets(20));
        hud.setStyle("-fx-background-color: rgba(10, 10, 10, 0.9); -fx-border-color: #00ffcc; -fx-border-width: 0 0 2 0;");

        scoreLbl = createHUDLabel();
        stepsLbl = createHUDLabel();
        timeLbl = createHUDLabel();

        hud.getChildren().addAll(scoreLbl, stepsLbl, timeLbl);
        this.gameRoot.setTop(hud);
    }

    private Label createHUDLabel() {
        Label lbl = new Label();
        lbl.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        return lbl;
    }

    // Pause menu setup
    private void setupPauseMenu() {
        pauseMenu = createMenuOverlay(LanguageManager.get("pause.title"));
        
        Button resumeBtn = createButton("pause.resume", "", e -> togglePause());
        Button restartBtn = createButton("pause.restart", "", e -> restartGame());
        Button settingsBtn = createButton("pause.settings", "", e -> screenManager.showSettings());
        Button menuBtn = createButton("game.menu", "", e -> screenManager.showMenu());
        
        Button exitBtn = createButton("pause.exit", "", e -> System.exit(0));
        exitBtn.getStyleClass().add("button-danger");
        
        pauseMenu.getChildren().addAll(resumeBtn, restartBtn, settingsBtn, menuBtn, exitBtn);
        mainStack.getChildren().add(pauseMenu);
    }

    private void setupWinOverlay() {
        winOverlay = createMenuOverlay("");
        mainStack.getChildren().add(winOverlay);
    }

    private VBox createMenuOverlay(String titleText) {
        VBox overlay = new VBox(25);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        overlay.setVisible(false);
        
        if (!titleText.isEmpty()) {
            Label title = new Label(titleText);
            title.getStyleClass().add("title-highlight");
            overlay.getChildren().add(title);
        }
        return overlay;
    }

    private Button createButton(String langKey, String style, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button();
        btn.textProperty().bind(LanguageManager.getStringProperty(langKey));
        btn.setMinWidth(250);
        if (!style.isEmpty()) btn.setStyle(style);
        btn.setOnAction(handler);
        return btn;
    }

    // Input handling
    private void setupControls() {
        gameRoot.setOnKeyPressed(e -> {
            if (gameWon) return;
            if (e.getCode() == KeyCode.ESCAPE) togglePause();
            if (isPaused) return;

            switch (e.getCode()) {
                case W, UP -> movePlayer(0, -1);
                case S, DOWN -> movePlayer(0, 1);
                case A, LEFT -> movePlayer(-1, 0);
                case D, RIGHT -> movePlayer(1, 0);
                case H -> hintPath = MazeManager.solveBFS(maze, playerPos, new Point(maze.getWidth()-2, maze.getHeight()-2));
            }
        });
        gameRoot.setFocusTraversable(true);
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseMenu.setVisible(isPaused);
        if (isPaused) pauseStartedAt = System.currentTimeMillis();
        else totalPausedTime += (System.currentTimeMillis() - pauseStartedAt);
    }

    private void restartGame() {
        isPaused = false; 
        pauseMenu.setVisible(false); 
        winOverlay.setVisible(false); 
        gameWon = false;
        score = 0; 
        steps = 0; 
        playerPos = new Point(1, 1);
        startTime = System.currentTimeMillis(); 
        totalPausedTime = 0; 
        hintPath = null;
        
        int size = maze.getWidth(); 
        this.maze = new Maze(size, size);
        MazeManager.generateMaze(this.maze); 
        gameRoot.requestFocus();
    }

    private void movePlayer(int dx, int dy) {
        int nx = playerPos.x + dx;
        int ny = playerPos.y + dy;
        
        if (nx >= 0 && nx < maze.getWidth() && ny >= 0 && ny < maze.getHeight() && maze.getCell(nx, ny) != Maze.WALL) {
            playerPos = new Point(nx, ny);
            steps++;
            hintPath = null; // Clear hint on move

            int cell = maze.getCell(nx, ny);
            if (cell == Maze.COLLECTIBLE) {
                score += 100;
                maze.setCell(nx, ny, Maze.PATH);
                SoundManager.play();
            } else if (cell == Maze.EXIT) {
                handleWin();
            }
        }
    }

    private void handleWin() {
        gameWon = true;
        winOverlay.getChildren().clear();
        winOverlay.setVisible(true);

        Label congrats = new Label(LanguageManager.get("game.congrats"));
        congrats.getStyleClass().add("title-highlight");
        
        long finalTime = (System.currentTimeMillis() - startTime - totalPausedTime) / 1000;
        Label stats = new Label(String.format("%s: %d | %s: %ds | %s: %dx%d", 
            LanguageManager.get("game.score"), score, LanguageManager.get("game.time"), finalTime,
            LanguageManager.get("settings.mazeSize"), maze.getWidth(), maze.getHeight()));
        stats.setFont(Font.font("Monospaced", 22));
        stats.setTextFill(Color.WHITE);

        Button playAgainBtn = createButton("game.restart", "-fx-border-color: #ff00ff; -fx-text-fill: #ff00ff;", e -> restartGame());
        Button menuBtn = createButton("game.menu", "", e -> screenManager.showMenu());
        
        Button saveBtn = createButton("game.saveRecord", "-fx-border-color: #3399ff; -fx-text-fill: #3399ff;", e -> {
            LeaderboardManager.saveScore(new ScoreRecord(screenManager.getCurrentUsername(), score, steps, finalTime, maze.getWidth()));
            Button btn = (Button) e.getSource();
            btn.setDisable(true);
            btn.textProperty().unbind();
            btn.setText(LanguageManager.get("game.recordSaved"));
        });

        winOverlay.getChildren().addAll(congrats, stats, playAgainBtn, menuBtn, saveBtn);
    }

    // Main draw loop
    private void startAnimation() {
        new AnimationTimer() {
            @Override public void handle(long now) {
                draw();
                if (!isPaused && !gameWon) updateHUD();
            }
        }.start();
    }

    private void updateHUD() {
        scoreLbl.setText(LanguageManager.get("game.score") + score);
        stepsLbl.setText(LanguageManager.get("game.steps") + steps);
        long elapsed = (System.currentTimeMillis() - startTime - totalPausedTime) / 1000;
        timeLbl.setText(LanguageManager.get("game.time") + elapsed + "s");
    }

    private void draw() {
        // Clear canvas completely
        gc.setEffect(null);
        gc.setFill(Color.web("#050505"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double cellSize = (double) 750 / maze.getWidth();

        // 1. Draw Static Elements (Walls, Collectibles)
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                int cell = maze.getCell(x, y);
                if (cell == Maze.WALL) {
                    gc.setFill(Color.web("#111"));
                    gc.setStroke(Color.web("#00ffcc", 0.2));
                    gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                    gc.strokeRect(x * cellSize, y * cellSize, cellSize, cellSize);
                } else if (cell == Maze.COLLECTIBLE) {
                    gc.setFill(Color.web("#ff00ff"));
                    gc.fillOval(x * cellSize + cellSize/3, y * cellSize + cellSize/3, cellSize/3, cellSize/3);
                }
            }
        }

        // 2. Draw Exit with isolated effect
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                if (maze.getCell(x, y) == Maze.EXIT) {
                    gc.save();
                    gc.setEffect(EXIT_GLOW);
                    gc.setFill(Color.RED);
                    gc.fillRect(x * cellSize + 2, y * cellSize + 2, cellSize - 4, cellSize - 4);
                    gc.restore();
                }
            }
        }

        // 3. Draw Hint Path with isolated effect
        if (hintPath != null) {
            gc.save();
            gc.setEffect(HINT_GLOW);
            gc.setStroke(Color.web("#00eeff", 0.8));
            gc.setLineWidth(3);
            for (int i = 0; i < hintPath.size() - 1; i++) {
                Point p1 = hintPath.get(i);
                Point p2 = hintPath.get(i + 1);
                gc.strokeLine(p1.x * cellSize + cellSize/2, p1.y * cellSize + cellSize/2, p2.x * cellSize + cellSize/2, p2.y * cellSize + cellSize/2);
            }
            gc.restore();
        }

        // 4. Draw Player with isolated effect
        gc.save();
        gc.setEffect(PLAYER_GLOW);
        gc.setFill(Color.web("#00eeff"));
        double playerPadding = cellSize * 0.2;
        double playerSize = cellSize - (playerPadding * 2);
        gc.fillOval(playerPos.x * cellSize + playerPadding, playerPos.y * cellSize + playerPadding, playerSize, playerSize);
        gc.restore();
    }

    public StackPane getRoot() { return mainStack; }
}
