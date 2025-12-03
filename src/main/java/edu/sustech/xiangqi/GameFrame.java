package edu.sustech.xiangqi;


import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.ui.ChessBoardPanel;

import javax.swing.*;
import java.awt.*;
public class GameFrame  extends JFrame {

    //  NEW
    private PlayGameSession activeSession;
    private ChessBoardPanel boardPanel;
    //

    private JButton Startbutton;
    private JButton changeinformation;
    private JButton saveAndOutButton;
    private JButton restartButton;
    private JButton takeBackAMove;
    private JButton giveUpButton;
    private JButton endUpPeaceButton;
    private boolean isTourist;
    private String playerName;
    private JLabel timerLabel;
    private Timer gameTimer;
    private int secondsElapsed;
    private JLabel campGoalLabel;
    private int redCampScore;
    private int blackCampScore;

    public GameFrame(String playerName) {

        super("中国象棋");
        this.isTourist = (playerName == null ||playerName.isEmpty());
        this.playerName = playerName;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        //============================================================================

        if (isTourist) {
            activeSession = new PlayGameSession("Tourist");
            Startbutton = new JButton("点击开始");
        } else {
            // try to load the existing save game
            activeSession = GamePersistence.loadGame(playerName);

            //if load failed (returns null), create a new session
            if (activeSession == null) {
                activeSession = new PlayGameSession(playerName);
                //if new game ,enable the start button
                Startbutton = new JButton("点击开始");
            } else {
                //if loaded game ,automatically enable interaction
                Startbutton = new JButton("游戏中");
                Startbutton.setEnabled(false);
            }
        }
//===============================================================================

//===============================================================================
        // 1. creat棋盘面板 (CENTER)
        ChessBoardModel model = activeSession.getChessBoardModel();
        CurrentCamp currentCamp = activeSession.getCurrentCamp();// You'll need to update ChessBoardPanel
        this.boardPanel = new ChessBoardPanel(model, currentCamp,this);
        model.setView(this.boardPanel);
        this.boardPanel.setGameInteractionEnabled(!Startbutton.isEnabled());// Match state of button
        this.add(boardPanel, BorderLayout.CENTER);

        if (isTourist) {
            this.redCampScore = 0;
            this.blackCampScore = 0;
        } else {
            this.redCampScore = activeSession.getRedCampScore();
            this.blackCampScore = activeSession.getBlackCampScore();
        }

        // 2. 按钮的具体设置
        // 创建一个统一的尺寸，例如：宽 120，高 40 (根据你的喜好调整)
        Dimension buttonSize = new Dimension(120, 40);

        Startbutton.setPreferredSize(buttonSize); // 设置大小

        changeinformation = new JButton("修改信息");
        changeinformation.setPreferredSize(buttonSize); // 设置大小

        String exitText = isTourist ? "退出游戏" : "存档并退出";
        saveAndOutButton = new JButton(exitText);
        saveAndOutButton.setPreferredSize(buttonSize); // 设置大小

        restartButton = new JButton("重新开始");
        restartButton.setPreferredSize(buttonSize); // 设置大小

        takeBackAMove = new JButton("悔一下棋");
        takeBackAMove.setPreferredSize(buttonSize);

        giveUpButton = new JButton("认输");
        giveUpButton.setPreferredSize(buttonSize);
        giveUpButton.setVisible(false);

        endUpPeaceButton = new JButton("Peace");
        endUpPeaceButton.setPreferredSize(buttonSize);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        if (!isTourist) {
            JLabel palyNameInformation = new JLabel("当前账号：", SwingConstants.CENTER); // Center text
            palyNameInformation.setFont(new Font("宋体", Font.BOLD, 14)); // Optional: Make it look nicer

            JLabel playName = new JLabel(playerName, SwingConstants.CENTER); // Center text
            playName.setFont(new Font("宋体", Font.PLAIN, 14));

            infoPanel.add(palyNameInformation);
            infoPanel.add(playName);
        }

        //==============================================================
        JPanel buttonPanel = new JPanel();
        //aujust grid rows based on the mode (2 row for tourist ,5 for user)
        //int rows = isTourist ? 2 : 5;
        buttonPanel.setLayout(new GridLayout(0, 1,0, 20));
        // row行1列，垂直间距20
        //key! only add buttons relevant to the user

        buttonPanel.add(Startbutton);

        if (!isTourist) {
            buttonPanel.add(changeinformation);
        }

        //both all have the button
        buttonPanel.add(saveAndOutButton);
        buttonPanel.add(takeBackAMove);
        buttonPanel.add(restartButton);
        buttonPanel.add(endUpPeaceButton);
        buttonPanel.add(giveUpButton);

        String playingTime = activeSession.getPlayingTime();
        int seconds = activeSession.getSecondsElapsed();
        if (isTourist) {
            this.timerLabel = new JLabel("游戏时长: 00:00:00", SwingConstants.CENTER);
            this.timerLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            this.secondsElapsed = 0; // Initialize to 0 seconds
             } else if (playingTime == null || playingTime.equals("游戏时长: 00:00:00") || seconds == 0) {
            this.timerLabel = new JLabel("游戏时长: 00:00:00", SwingConstants.CENTER);
            this.timerLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            this.secondsElapsed = 0; // Initialize to 0 seconds
        } else {
            this.timerLabel = new JLabel(playingTime, SwingConstants.CENTER);
            this.timerLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            this.secondsElapsed = seconds;
        }


        JPanel buttonCenterWrapper = new JPanel(new GridBagLayout());
        buttonCenterWrapper.add(buttonPanel);

        String goalText = String.format("<html>" +
                "<span style='color:red;'>红方: %d</span>" +
                "<span style='color:#666;'>&nbsp;|&nbsp;</span>" + // Separator
                "<span style='color:black;'>黑方: %d</span>" +
                "</html>",redCampScore,blackCampScore);

        this.campGoalLabel = new JLabel(goalText, SwingConstants.CENTER);
        this.campGoalLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        //侧边容器 (sidePanel)我们用一个新面板包裹 buttonPanel，防止它被 BorderLayout 拉伸
        JPanel sidePanel = new JPanel(new BorderLayout());

        // A. Create a TOP wrapper for Info, Timer, and Goal (Stacked Vertically)
        JPanel topInfoContainer = new JPanel();
        topInfoContainer.setLayout(new BoxLayout(topInfoContainer, BoxLayout.Y_AXIS));

        // Align the nested panels to the center of the X-axis
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        campGoalLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // NEW: Align the new label

        // === Layout Stack in topInfoContainer (NORTH section of sidePanel) ===
        topInfoContainer.add(Box.createVerticalStrut(20)); // Top margin
        topInfoContainer.add(infoPanel);                   // 1. Player Name
        topInfoContainer.add(Box.createVerticalStrut(10)); // Gap
        topInfoContainer.add(timerLabel);                  // 2. Timer
        topInfoContainer.add(Box.createVerticalStrut(10)); // NEW: Gap
        topInfoContainer.add(campGoalLabel);               // NEW: 3. Camp Goals
        topInfoContainer.add(Box.createVerticalStrut(30)); // Gap before buttons

        // B. Put Info/Timer at the TOP (NORTH)
        sidePanel.add(topInfoContainer, BorderLayout.NORTH);

        // C. Put Buttons in the CENTER (Occupying the rest of the space)
        sidePanel.add(buttonCenterWrapper, BorderLayout.CENTER);
        this.add(sidePanel, BorderLayout.EAST);

        // 自动调整窗口大小
        // pack() 会根据棋盘和按钮的实际大小，自动把窗口收缩到最小（紧贴边缘）
        this.pack();
        this.setVisible(false);
    }


    public void setVisible(boolean b) {
        super.setVisible(b);
    }


    public JButton getRestartButton() {
        return restartButton;
    }

    public JButton getTakeBackAMove() {
        return takeBackAMove;
    }

    public JButton getChangeinformation() {
        return this.changeinformation;
    }

    public JButton getSaveAndOutButton() {
        return this.saveAndOutButton;
    }

    public PlayGameSession getActiveSession() {
        return this.activeSession;
    }

    public void setActiveSessionModel(PlayGameSession newActiveSession) {
        getActiveSession().setModel(newActiveSession.getChessBoardModel());
    }

    public void setCurrentCamp(CurrentCamp newCurrentCamp) {
        getActiveSession().setCurrentCamp(newCurrentCamp);
    }

    public ChessBoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    public JButton getStartbutton() {
        return this.Startbutton;
    }

    public JButton getGiveUpButton() {
        return this.giveUpButton;
    }

    public JButton getEndUpPeaceButton() {
        return this.endUpPeaceButton;
    }

    public void showGiveUpOption(String campName) {
        giveUpButton.setText(campName + "是否认输?");
        giveUpButton.setVisible(true);
    }

    public void hideGiveUpOption() {
        giveUpButton.setVisible(false);
    }

    public JButton getSaveButton() {
        return this.saveAndOutButton;
    }

    public JButton getStartButton() {
        return this.Startbutton;
    }

    public PlayGameSession getCurrentSession() {
        return this.activeSession;
    }

    public boolean getIsTourist() {
        return isTourist;
    }

    private void updateTimerLabel() {
        int hours = secondsElapsed / 3600;
        int minutes = (secondsElapsed % 3600) / 60;
        int seconds = secondsElapsed % 60;

        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        this.timerLabel.setText("游戏时长: " + time);
    }

    public void startGameTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            return; // Already running
        }

        // Initialize the timer to fire every 1000 milliseconds (1 second)
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

    public int  getSecondsElapsed() {
        return secondsElapsed;
    }

    public String getTimerLabel() {
       return this.timerLabel.getText();
    }
    public int getRedCampScore() {
        return redCampScore;
    }
    public void addRedCampScore() {
        this.redCampScore++;
    }
    public void removeRedCampScore() {
        this.redCampScore--;
    }
    public int getBlackCampScore() {
        return blackCampScore;
    }
    public void addBlackCampScore() {
        this.blackCampScore++;
    }
    public void removeBlackCampScore() {
        this.blackCampScore--;
    }

    public void updateScoreLabel() {
        String goalText = String.format("<html>" +
                "<span style='color:red;'>红方: %d</span>" +
                "<span style='color:#666;'>&nbsp;|&nbsp;</span>" + // Separator
                "<span style='color:black;'>黑方: %d</span>" +
                "</html>", this.redCampScore, this.blackCampScore);

        this.campGoalLabel.setText(goalText);
        // Ensure the layout manager knows a component has been updated
        this.campGoalLabel.repaint();
    }
}
