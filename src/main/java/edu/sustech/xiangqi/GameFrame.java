package edu.sustech.xiangqi;


import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.ui.ChessBoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private boolean isTourist;

    public GameFrame(String playerName) {

        super("中国象棋");
        this.isTourist = (playerName == null ||playerName.isEmpty());
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
        this.boardPanel = new ChessBoardPanel(model, currentCamp);
        this.boardPanel.setGameInteractionEnabled(!Startbutton.isEnabled());// Match state of button
        this.add(boardPanel, BorderLayout.CENTER);

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

        //==============================================================
        JPanel buttonPanel = new JPanel();
        //aujust grid rows based on the mode (2 row for tourist ,5 for user)
        // 按钮内部布局 (buttonPanel)
        // 依然使用 GridLayout 排列4个按钮，但这次只负责排列，不负责拉伸整个页面
        int rows = isTourist ? 2 : 5;
        buttonPanel.setLayout(new GridLayout(rows, 1,0, 20));
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


        //侧边容器 (sidePanel)我们用一个新面板包裹 buttonPanel，防止它被 BorderLayout 拉伸
        JPanel sidePanel =new JPanel();
        // 使用 GridBagLayout，它会让内部组件保持“首选大小”并在垂直方向居中
        sidePanel.setLayout(new GridBagLayout());
        sidePanel.add(buttonPanel);
        // 将这个不拉伸的容器放到窗口右侧
        this.add(sidePanel, BorderLayout.EAST);
        //==========================================================// 将这个不拉伸的容器放到窗口右侧


        //=============================================================
        Startbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // a. 启用棋盘交互
                boardPanel.setGameInteractionEnabled(true);
                // b. 禁用“点击开始”按钮，防止重复点击，同时提示用户已开始
                Startbutton.setEnabled(false);
                Startbutton.setText("游戏中...");
            }
        });

       //save logic
        saveAndOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {//why this is what?
                if (!isTourist) {
                GamePersistence.saveGame(activeSession);
                } else {
                    System.out.println("you are Tourist exit - No save");
                }
                dispose();//close the game window
            }
        });

        // 自动调整窗口大小
        // pack() 会根据棋盘和按钮的实际大小，自动把窗口收缩到最小（紧贴边缘）
        this.pack();
        this.setVisible(false); // 建议这里设为 true，或者在主程序里设
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

}
