package edu.sustech.xiangqi;



import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.ui.AudioPlayer;
import edu.sustech.xiangqi.ui.ChessBoardPanel;
import edu.sustech.xiangqi.ui.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.Scanner;

import static edu.sustech.xiangqi.InterChecking.*;
import static edu.sustech.xiangqi.SignInChecking.passworkcheck;
import static edu.sustech.xiangqi.SignInChecking.rightname;


public class LoginFrame extends JFrame{
    //this is not a Frame but an object which includes the frame and the frame is called through this class so it just looks like a frame but actually not !

    private GameFrame gameFrame;
    private LoginPanel loginPanel;
    private ChangePasswordFrame changePasswordFrame;
    private SigninFrame signinFrame;


    // 内部类：带背景的面板（直接嵌套在LoginFrame中）
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel() {
            // 加载背景图（路径根据实际情况调整）
            URL imgUrl = getClass().getResource("/Picture/生成象棋登录界面照片2.png");
            if (imgUrl != null) {
                backgroundImage = new ImageIcon(imgUrl).getImage();
            } else {
                System.out.println("背景图路径错误！");
            }
            setLayout(null); // 和原有布局保持一致
            setPreferredSize(new Dimension(500, 500)); // 匹配窗口大小
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public LoginFrame() {
        super("中国象棋 - 登录");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);

        // 初始化登录面板并添加到背景面板
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        this.setContentPane(backgroundPanel);
        // 最后启用布局并刷新
        // 1. 先初始化背景面板和登录组件
        this.loginPanel = new LoginPanel();
        this.loginPanel.setOpaque(false); // 关键：LoginPanel透明，显示背景图
        this.loginPanel.setBounds(0, 0, 500, 500); // 关键：手动指定LoginPanel的位置和大小（和窗口一致
        backgroundPanel.add(this.loginPanel, BorderLayout.CENTER); // 指定布局位置
        // 初始化其他窗口（原有逻辑不变）


        this.signinFrame = new SigninFrame();
        this.changePasswordFrame = new ChangePasswordFrame();
        this.setVisible(true);

        AudioPlayer.playLoopingSound("src/main/resources/Audio/我的歌声里.wav");

        try {
            Scanner nickname = new Scanner(new File(".\\src\\main\\java\\edu\\sustech\\xiangqi\\Nickname"));

            //这是登录框架的开始按键的相关响应代码部分
            loginPanel.getLoginButton().addActionListener(e -> {//button指开始的按键
                AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
                if (enteruser(loginPanel.getUsername())>=0) {//指用户存在
                    String inputUsername = loginPanel.getUsername();//Capture the name here

                    if(enterpassword(loginPanel.getPassword(),enteruser(loginPanel.getUsername()))){//密码正确
                        this.setVisible(false);

                        //=================================================================
                        //initialize GameFrame using the successful uername
                        this.gameFrame = new GameFrame(inputUsername);

                        //set up listeners for the new instance
                        setupGameFrameListeners();
                        //=================================================================

                        if(gameFrame.getStartButton().isEnabled()) {
                            gameFrame.getRestartButton().setEnabled(false);
                        } else {
                            AudioPlayer.playSound("src/main/resources/Audio/游戏进行中.wav");
                        }
                        //display the GameFrame
                        AudioPlayer.stopLoopingSound("src/main/resources/Audio/我的歌声里.wav");
                        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        gameFrame.pack();
                        gameFrame. getRestartButton().setVisible(true);
                        gameFrame.setLocationRelativeTo(null);
                        gameFrame.setVisible(true);
                    }//密码错误
                    else{
                        TimeForDisplayAndLater.displayAndHideJLabel(loginPanel.wrongLabel(),500);
                        loginPanel.unexistLabel().setVisible(false);
                    }
                }//下面是用户不存在
                else {
                    loginPanel.wrongLabel().setVisible(false);
                    TimeForDisplayAndLater.displayAndHideJLabel(loginPanel.unexistLabel(),500);
                }
            });
        } catch (FileNotFoundException e) {
            System.out.println("操作错误1");
        } catch (IOException e) {
            System.out.println("操作错误2");
        }

        //从登录到注册的启动按键
        loginPanel.getSignInButton().addActionListener(e -> {
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            signinFrame.setVisible(true);
            signinFrame.setLocationRelativeTo(null);
            this.setVisible(false);
        });

        //从注册界面回到登录界面==============================================================
        signinFrame.getReturnButton().addActionListener(e -> {
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            signinFrame.setVisible(false);
            this.setVisible(true);
        });

        //点击确认注册那个按键的操作判定（注册用户的相关判定）
        signinFrame.getConfirmPasswordButton().addActionListener(e->{
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            if(rightname(signinFrame.getYourNameTextField()) && passworkcheck(signinFrame.getYourPasswordTextField())){//正确的密码格式和账号格式 //后面是写入对应文本的方法
                    try{
                        String roadN=".\\src\\main\\java\\edu\\sustech\\xiangqi\\Nickname";
                        String roadP=".\\src\\main\\java\\edu\\sustech\\xiangqi\\Password";
                        BufferedWriter Nicknamewrite=new BufferedWriter(new FileWriter(roadN,true));
                        BufferedWriter Passwordwrite=new BufferedWriter(new FileWriter(roadP,true));
                        Nicknamewrite.write("\n");
                        Nicknamewrite.write(signinFrame.getYourNameTextField());
                        Passwordwrite.write("\n");
                        Passwordwrite.write(signinFrame.getYourPasswordTextField());
                        Nicknamewrite.close();
                        Passwordwrite.close();

                        // 注册成功，显示提示并设置延时跳转
                        //this text is created but not visible
                        signinFrame.getConfirmsuscess().setText("注册成功！"); // 假设 Confirmsuscess 是成功的提示标签

                        // 关键：Timer 的action 将执行跳转
                        Timer jumpTimer = new Timer(500, new ActionListener() {//this is the same to a object and use it on the code below.
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // --- 延时 0.5 秒后才执行的跳转逻辑 ---
                                signinFrame.getConfirmsuscess().setVisible(false);

                                //create the GameFrame======================================
                                LoginFrame.this.gameFrame = new GameFrame(signinFrame.getYourNameTextField());

                                //set up listeners for the new instance
                                LoginFrame.this.setupGameFrameListeners();
                                //=============================================================
                                AudioPlayer.stopLoopingSound("src/main/resources/Audio/我的歌声里.wav");
                                LoginFrame.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                LoginFrame.this.gameFrame.pack();
                                LoginFrame.this.gameFrame.getRestartButton().setVisible(true);
                                LoginFrame.this.gameFrame.setLocationRelativeTo(null);
                                LoginFrame.this.gameFrame.setVisible(true);
                                LoginFrame.this.signinFrame.setVisible(false);
                                // --- 延时跳转逻辑结束 ---
                                ((Timer) e.getSource()).stop();
                            }
                        });

                        jumpTimer.setRepeats(false);
                        signinFrame.getConfirmsuscess().setVisible(true); // 立即显示成功提示
                        jumpTimer.start();              // 启动延时跳转

                    }catch(IOException e1){
                        System.out.println("无法写入相应文件");
                        signinFrame.getConfirmsuscess().setText("写入文件失败！");
                        TimeForDisplayAndLater.displayAndHideJLabel(signinFrame.getConfirmsuscess(), 500); // 提示写入失败
                    }

                } else if(rightname(signinFrame.getYourNameTextField())) { // 昵称正确，但密码错误////////////////////////////////////
                // 密码输入过短
                TimeForDisplayAndLater.displayAndHideJLabel(signinFrame.getErrorOne(),500);

                }  else if(passworkcheck(signinFrame.getYourPasswordTextField())){// 密码正确，但昵称错误/已存在
                // 用户名不合法或已有其他玩家使用
                TimeForDisplayAndLater.displayAndHideJLabel(signinFrame.getErrorTwo(),500);
            }
            else {// 昵称和密码都不符合规定
                TimeForDisplayAndLater.displayAndHideJLabel(signinFrame.getErrorThree(),500);
            }

        });

        // 游客登录相关的相应
        this.loginPanel.getTouristButton().addActionListener(e -> {
                    AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
                    AudioPlayer.stopLoopingSound("src/main/resources/Audio/我的歌声里.wav");
                    this.gameFrame = new GameFrame(null);
                    this.setupGameFrameListeners();
                    gameFrame.getRestartButton().setEnabled(false);
                    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameFrame.pack();
                    gameFrame.setLocationRelativeTo(null);
                    gameFrame.setVisible(true);
                    this.setVisible(false);
                });


        //改变密码的相关代码
        changePasswordFrame.getChangePasswordButton().addActionListener(e->{
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            // 1. 获取用户索引
            int userIndex = InterChecking.enteruser(changePasswordFrame.theUserNameText());

            // 2. 验证：用户存在 && 旧密码正确 && 新密码格式正确
            if (userIndex >= 0
                    && InterChecking.enterpassword(changePasswordFrame.getOldPassword(), userIndex)
                    && SignInChecking.passworkcheck(changePasswordFrame.getNewPassword())) {

                // 3. 执行修改文件操作 (调用上面写好的工具方法)
                // 注意：这里调用的是新写的方法 updatePasswordInFile
                boolean updateSuccess = updatePasswordInFile(userIndex, changePasswordFrame.getNewPassword());

                if (updateSuccess) {
                    // 4. UI 反馈
                    TimeForDisplayAndLater.displayAndHideJLabel(changePasswordFrame.getsusTochangePassword(), 800);
                    TimeForDisplayAndLater.onlyHideFrame(changePasswordFrame, 1000);
                    TimeForDisplayAndLater.onlyDisplayFrame(this, 1000);
                } else {
                   TimeForDisplayAndLater.displayAndHideJLabel(changePasswordFrame.getErrorOne(), 800);
                }

            } else {
                TimeForDisplayAndLater.displayAndHideJLabel(changePasswordFrame.getErrorTwo(), 800);
            }
        });
    }

    //把所有相应器都放到这里了
    private void setupGameFrameListeners() {
        // =====================================================================
        //重新开始的响应器
        gameFrame.getRestartButton().addActionListener(e -> {
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            PlayGameSession newActiveSession = new PlayGameSession(gameFrame.getActiveSession().getPlayerNameID());//全新棋盘和红棋子先走
            gameFrame.setActiveSessionModel(newActiveSession);
            gameFrame.setCurrentCamp(newActiveSession.getCurrentCamp());
            //Update the ChessBoardPanel's reference to the new model and camp.
            gameFrame.getBoardPanel().setNewGameModel(newActiveSession.getChessBoardModel(), newActiveSession.getCurrentCamp());
            gameFrame.refreshLastMoveVisuals();
            gameFrame.getStartbutton().setEnabled(true);
            gameFrame.getStartbutton().setText("点击开始");
            gameFrame.getRestartButton().setEnabled(false);
            gameFrame.getBoardPanel().setGameInteractionEnabled(true);
            gameFrame.getEndUpPeaceButton().setEnabled(true);
            gameFrame.getTakeBackAMove().setEnabled(true);
            gameFrame.repaint(); // Force GameFrame to redraw everything
        });


        // 2. Game frame change information button
        //=====================================================
        gameFrame.getChangeinformation().addActionListener(e->{
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            AudioPlayer.stopLoopingSound("src/main/resources/Audio/斗地主.wav");
            AudioPlayer.playLoopingSound("src/main/resources/Audio/我的歌声里.wav");
            gameFrame.stopGameTimer();
            gameFrame.getActiveSession().setPlayingTime(gameFrame.getTimerLabel());
            gameFrame.getActiveSession().setSecondsElapsed(gameFrame.getSecondsElapsed());
            GamePersistence.saveGame(gameFrame.getActiveSession());
            changePasswordFrame.setVisible(true);
            gameFrame.setVisible(false);
        });
        //=====================================================


        // 3. Game frame return from password change (already defined)
        changePasswordFrame.getReturnToTheGame().addActionListener(e->{
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            AudioPlayer.stopLoopingSound("src/main/resources/Audio/我的歌声里.wav");
            AudioPlayer.playLoopingSound("src/main/resources/Audio/斗地主.wav");
            changePasswordFrame.setVisible(false);
            gameFrame.setVisible(true);
        });

        // 4. Save and Exit button
        gameFrame.getSaveAndOutButton().addActionListener(e->{
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            // using GamePersistence.saveGame(gameFrame.activeSession)
            gameFrame.setVisible(false);
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });

        //5. 悔棋的响应器
        gameFrame.getTakeBackAMove().addActionListener(e->{
            AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
            ChessBoardModel model = gameFrame.getActiveSession().getChessBoardModel();
            CurrentCamp currentCamp = gameFrame.getActiveSession().getCurrentCamp();
            ChessBoardPanel boardPanel = gameFrame.getBoardPanel();

            if (model.getMoveHistory().isEmpty()) {
                gameFrame.updateStatusMessage("无法悔棋", Color.RED, false);
                return;
            }
            if (model.getMoveHistory().isEmpty() == false && gameFrame.getBoardPanel().getInteractionEnabled() == false && gameFrame.getEndUpPeaceButton().isEnabled() == false) {
                if (currentCamp.isRedTurn()) {
                    gameFrame.removeBlackCampScore();
                    gameFrame.updateScoreLabel();
                    repaint();
                } else  {
                    gameFrame.removeRedCampScore();
                    gameFrame.updateScoreLabel();
                    repaint();
                }
                model.removeLastMove();
                currentCamp.returnTurn();
                boardPanel.setNewGameModel(model, currentCamp);
                gameFrame.updateStatusMessage("悔棋成功！", Color.BLUE, false);
                gameFrame.repaint();
            } else {
            model.removeLastMove();
            currentCamp.returnTurn();
            boardPanel.setNewGameModel(model, currentCamp);
            gameFrame.updateStatusMessage("悔棋成功！", Color.BLUE, false);
            gameFrame.repaint();
            }
            if (!gameFrame.getEndUpPeaceButton().isEnabled()) {
                gameFrame.getEndUpPeaceButton().setEnabled(true);
            }
            gameFrame.getBoardPanel().setGameInteractionEnabled(true);
            gameFrame.refreshLastMoveVisuals();
            AudioPlayer.playSound("src/main/resources/Audio/悔棋.WAV");
            gameFrame.getActiveSession().setPlayingTime(gameFrame.getTimerLabel());
            gameFrame.getActiveSession().setSecondsElapsed(gameFrame.getSecondsElapsed());
            gameFrame.getActiveSession().setRedCampScore(gameFrame.getRedCampScore());
            gameFrame.getActiveSession().setBlackCampScore(gameFrame.getBlackCampScore());
            GamePersistence.saveGame(gameFrame.getActiveSession());
        });

        //=============================================================
        gameFrame.getStartButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
                // a. 启用棋盘交互
                gameFrame.getBoardPanel().setGameInteractionEnabled(true);
                // b. 禁用“点击开始”按钮，防止重复点击，同时提示用户已开始
                gameFrame.getStartButton().setEnabled(false);
                AudioPlayer.playSound("src/main/resources/Audio/游戏开始.wav");
                if (!gameFrame.getRestartButton().isEnabled()) {
                    gameFrame.getRestartButton().setEnabled(true);
                }
                gameFrame.getStartButton().setText("游戏中...");
            }
        });

        gameFrame.getSaveButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {//why this is what?
                AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
                if (!gameFrame.getIsTourist()) {
                    gameFrame.stopGameTimer();
                    gameFrame.updateScoreLabel();
                    gameFrame.getActiveSession().setPlayingTime(gameFrame.getTimerLabel());
                    gameFrame.getActiveSession().setSecondsElapsed(gameFrame.getSecondsElapsed());
                    gameFrame.getActiveSession().setRedCampScore(gameFrame.getRedCampScore());
                    gameFrame.getActiveSession().setBlackCampScore(gameFrame.getBlackCampScore());
                    GamePersistence.saveGame(gameFrame.getActiveSession());
                } else {
                    System.out.println("you are Tourist exit - No save");
                }
                AudioPlayer.stopAllLoopingSounds();
                dispose();//close the game window
            }
        });

        gameFrame.getGiveUpButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
                //the current active camp is the one whose general is in check
                boolean isRedTurn = gameFrame.getActiveSession().getCurrentCamp().isRedTurn();

                String winner = isRedTurn ? "黑方" : "红方";
                String loser = isRedTurn ? "红方" : "黑方";
                if (isRedTurn) {
                    gameFrame.addBlackCampScore();
                    AudioPlayer.playSound("src/main/resources/Audio/红方投降.wav");
                }  else {
                    gameFrame.addRedCampScore();
                    AudioPlayer.playSound("src/main/resources/Audio/黑方投降.wav");
                }
                gameFrame.updateScoreLabel();
                gameFrame.updateStatusMessage(winner + "胜利！（" + loser + "认输）", Color.BLUE, true);
                gameFrame.hideGiveUpOption();
                gameFrame.getEndUpPeaceButton().setEnabled(false);
                gameFrame.getBoardPanel().setGameInteractionEnabled(false);
                gameFrame.stopGameTimer();
                gameFrame.getActiveSession().setPlayingTime(gameFrame.getTimerLabel());
                gameFrame.getActiveSession().setSecondsElapsed(gameFrame.getSecondsElapsed());
                gameFrame.getActiveSession().setRedCampScore(gameFrame.getRedCampScore());
                gameFrame.getActiveSession().setBlackCampScore(gameFrame.getBlackCampScore());
                GamePersistence.saveGame(gameFrame.getActiveSession());
                gameFrame.repaint();
            }
        });

        gameFrame.getEndUpPeaceButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioPlayer.playSound("src/main/resources/Audio/按键音效.wav");
                if (!gameFrame.getActiveSession().getChessBoardModel().getMoveHistory().isEmpty()) {
                    gameFrame.updateStatusMessage("双方和棋！", Color.BLUE, true);
                    AudioPlayer.playSound("src/main/resources/Audio/双方和棋.WAV");
                gameFrame.hideGiveUpOption();
                gameFrame.getBoardPanel().setGameInteractionEnabled(false);
                gameFrame.stopGameTimer();
                gameFrame.updateScoreLabel();
                gameFrame.getEndUpPeaceButton().setEnabled(false);
                gameFrame.getTakeBackAMove().setEnabled(false);
                gameFrame.getActiveSession().setPlayingTime(gameFrame.getTimerLabel());
                gameFrame.getActiveSession().setSecondsElapsed(gameFrame.getSecondsElapsed());
                gameFrame.getActiveSession().setRedCampScore(gameFrame.getRedCampScore());
                gameFrame.getActiveSession().setBlackCampScore(gameFrame.getBlackCampScore());
                GamePersistence.saveGame(gameFrame.getActiveSession());
                gameFrame.repaint();
                } else {
                    return;
                }
            }
        });
    }
}
