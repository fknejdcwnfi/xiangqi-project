package edu.sustech.xiangqi;
import edu.sustech.xiangqi.ui.AncientButton;
import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.ui.ChessBoardPanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    // Logic Components
    private PlayGameSession activeSession;
    private ChessBoardPanel boardPanel;
    private Timer gameTimer;
    private int secondsElapsed;

    // UI Components - Buttons
    private JButton Startbutton;
    private JButton changeinformation;
    private JButton saveAndOutButton;
    private JButton restartButton;
    private JButton takeBackAMove;
    private JButton giveUpButton;
    private JButton endUpPeaceButton;

    // UI Components - Labels
    private JLabel timerLabel;
    private JLabel campGoalLabel;

    // Data
    private boolean isTourist;
    private String playerName;
    private int redCampScore;
    private int blackCampScore;

    // CONSTANTS
    private static final int SIDE_PANEL_WIDTH = 300; // Width for Left and Right panels to ensure symmetry
    private static final Dimension BUTTON_SIZE = new Dimension(140, 45);

    public GameFrame(String playerName) {
        super("中国象棋");
        this.isTourist = (playerName == null || playerName.isEmpty());
        this.playerName = playerName;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout()); // Main Layout

        // ==================== 1. Session & Logic Init ====================
        initializeSession();
        initializeScores();

        // ==================== 2. Center: Chess Board ====================
        ChessBoardModel model = activeSession.getChessBoardModel();
        CurrentCamp currentCamp = activeSession.getCurrentCamp();

        this.boardPanel = new ChessBoardPanel(model, currentCamp, this);
        model.setView(this.boardPanel);
        // Sync button state with board interaction
        boolean isGameRunning = !Startbutton.isEnabled();
        this.boardPanel.setGameInteractionEnabled(isGameRunning);

        this.add(boardPanel, BorderLayout.CENTER);

        // ==================== 3. Left Panel: Text Info ====================
        JPanel leftPanel = createLeftPanel();
        this.add(leftPanel, BorderLayout.WEST);

        // ==================== 4. Right Panel: Buttons ====================
        JPanel rightPanel = createRightPanel();
        this.add(rightPanel, BorderLayout.EAST);

        // ==================== 5. Finalize Window ====================
        this.pack(); // Adjusts window size based on components
        this.setVisible(false);
    }

    /**
     * Helper to Initialize Game Session Logic
     */
    private void initializeSession() {
        if (isTourist) {
            activeSession = new PlayGameSession("Tourist");
            Startbutton = new AncientButton("点击开始");
        } else {
            activeSession = GamePersistence.loadGame(playerName);
            if (activeSession == null) {
                activeSession = new PlayGameSession(playerName);
                Startbutton = new AncientButton("点击开始");
            } else {
                Startbutton = new AncientButton("游戏中");
                Startbutton.setEnabled(false);
            }
        }
    }

    /**
     * Helper to Initialize Scores and Timer Data
     */
    private void initializeScores() {
        if (isTourist) {
            this.redCampScore = 0;
            this.blackCampScore = 0;
        } else {
            this.redCampScore = activeSession.getRedCampScore();
            this.blackCampScore = activeSession.getBlackCampScore();
        }

        String playingTime = activeSession.getPlayingTime();
        int seconds = activeSession.getSecondsElapsed();

        if (isTourist || playingTime == null || seconds == 0) {
            this.secondsElapsed = 0;
        } else {
            this.secondsElapsed = seconds;
        }
    }

    /**
     * Creates the Left Panel containing Text, Timer, and Score
     */
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, 0)); // Set fixed width
        leftPanel.setLayout(new GridBagLayout()); // Centered vertically
        // Optional: Set background to see the "space"
        // leftPanel.setBackground(Color.WHITE);

        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        // contentContainer.setBackground(Color.WHITE);

        // 1. Player Info
        if (!isTourist) {
            JLabel infoTitle = new JLabel("当前账号：");
            infoTitle.setFont(new Font("宋体", Font.BOLD, 16));
            infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = new JLabel(playerName);
            nameLabel.setFont(new Font("宋体", Font.PLAIN, 16));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            contentContainer.add(infoTitle);
            contentContainer.add(Box.createVerticalStrut(5));
            contentContainer.add(nameLabel);
            contentContainer.add(Box.createVerticalStrut(30));
        }

        // 2. Timer
        String initialTimeText = (secondsElapsed == 0) ? "游戏时长: 00:00:00" : activeSession.getPlayingTime();
        if(initialTimeText == null) initialTimeText = "游戏时长: 00:00:00";

        timerLabel = new JLabel(initialTimeText);
        timerLabel.setFont(new Font("华文行楷", Font.BOLD, 24));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentContainer.add(timerLabel);

        contentContainer.add(Box.createVerticalStrut(30));

        // 3. Score / Goal
        String goalText = String.format("<html>" +
                "<div style='text-align:center; font-size:14px;'>" +
                "<span style='color:red;'>红方: %d</span><br/><br/>" +
                "<span style='color:black;'>---- VS ----</span><br/><br/>" +
                "<span style='color:black;'>黑方: %d</span>" +
                "</div></html>", redCampScore, blackCampScore);

        campGoalLabel = new JLabel(goalText, SwingConstants.CENTER);
        campGoalLabel.setFont(new Font("华文行楷", Font.PLAIN, 14));
        campGoalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentContainer.add(campGoalLabel);

        leftPanel.add(contentContainer); // Add container to centered GridBag
        return leftPanel;
    }

    /**
     * Creates the Right Panel containing all Buttons
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, 0)); // Same fixed width as left
        rightPanel.setLayout(new GridBagLayout()); // Center the button stack vertically

        JPanel buttonContainer = new JPanel();
        // Use GridLayout for uniform button sizes and spacing
        buttonContainer.setLayout(new GridLayout(0, 1, 0, 15));

        // Initialize Buttons
        Startbutton.setPreferredSize(BUTTON_SIZE);

        changeinformation = new AncientButton("修改信息");
        changeinformation.setPreferredSize(BUTTON_SIZE);

        String exitText = isTourist ? "退出游戏" : "存档并退出";
        saveAndOutButton = new AncientButton(exitText);
        saveAndOutButton.setPreferredSize(BUTTON_SIZE);

        restartButton = new AncientButton("重新开始");
        restartButton.setPreferredSize(BUTTON_SIZE);

        takeBackAMove = new AncientButton("悔一下棋");
        takeBackAMove.setPreferredSize(BUTTON_SIZE);

        giveUpButton = new AncientButton("认输");
        giveUpButton.setPreferredSize(BUTTON_SIZE);
        giveUpButton.setVisible(false);

        endUpPeaceButton = new AncientButton("求和");
        endUpPeaceButton.setPreferredSize(BUTTON_SIZE);

        // Add Buttons to Container
        buttonContainer.add(Startbutton);

        if (!isTourist) {
            buttonContainer.add(changeinformation);
        }

        buttonContainer.add(saveAndOutButton);
        buttonContainer.add(takeBackAMove);
        buttonContainer.add(restartButton);
        buttonContainer.add(endUpPeaceButton);
        buttonContainer.add(giveUpButton);

        rightPanel.add(buttonContainer);
        return rightPanel;
    }

    // ========================================================================
    //                         Getters & Setters & Helpers
    // ========================================================================

    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    public JButton getRestartButton() { return restartButton; }
    public JButton getTakeBackAMove() { return takeBackAMove; }
    public JButton getChangeinformation() { return this.changeinformation; }
    public JButton getSaveAndOutButton() { return this.saveAndOutButton; }
    public PlayGameSession getActiveSession() { return this.activeSession; }

    public void setActiveSessionModel(PlayGameSession newActiveSession) {
        getActiveSession().setModel(newActiveSession.getChessBoardModel());
    }

    public void setCurrentCamp(CurrentCamp newCurrentCamp) {
        getActiveSession().setCurrentCamp(newCurrentCamp);
    }

    public ChessBoardPanel getBoardPanel() { return this.boardPanel; }
    public JButton getStartbutton() { return this.Startbutton; }
    public JButton getGiveUpButton() { return this.giveUpButton; }
    public JButton getEndUpPeaceButton() { return this.endUpPeaceButton; }

    public void showGiveUpOption(String campName) {
        giveUpButton.setText(campName + "认输?");
        giveUpButton.setVisible(true);
    }

    public void hideGiveUpOption() {
        giveUpButton.setVisible(false);
    }

    public JButton getSaveButton() { return this.saveAndOutButton; }
    public JButton getStartButton() { return this.Startbutton; }
    public PlayGameSession getCurrentSession() { return this.activeSession; }
    public boolean getIsTourist() { return isTourist; }

    private void updateTimerLabel() {
        int hours = secondsElapsed / 3600;
        int minutes = (secondsElapsed % 3600) / 60;
        int seconds = secondsElapsed % 60;

        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        this.timerLabel.setText("游戏时长: " + time);
        this.timerLabel.setFont(new Font("华文行楷", Font.PLAIN, 24));
    }

    public void startGameTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            return;
        }
        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            updateTimerLabel();
        });
        gameTimer.start();
    }

    public void stopGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    public int getSecondsElapsed() { return secondsElapsed; }
    public String getTimerLabel() { return this.timerLabel.getText(); }

    public int getRedCampScore() { return redCampScore; }
    public void addRedCampScore() { this.redCampScore++; }
    public void removeRedCampScore() { this.redCampScore--; }

    public int getBlackCampScore() { return blackCampScore; }
    public void addBlackCampScore() { this.blackCampScore++; }
    public void removeBlackCampScore() { this.blackCampScore--; }

    public void updateScoreLabel() {
        // Updated HTML format for the Score label in the Left Panel
        String goalText = String.format("<html>" +
                "<div style='text-align:center; font-size:14px;'>" +
                "<span style='color:red;'>红方: %d</span><br/><br/>" +
                "<span style='color:black;'>---- VS ----</span><br/><br/>" +
                "<span style='color:black;'>黑方: %d</span>" +
                "</div></html>", this.redCampScore, this.blackCampScore);

        this.campGoalLabel.setText(goalText);
        this.campGoalLabel.repaint();
    }
}