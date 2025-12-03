package edu.sustech.xiangqi;



import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.ui.ChessBoardPanel;
import edu.sustech.xiangqi.ui.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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

    public LoginFrame() {
        super("中国象棋 - 登录");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.loginPanel = new LoginPanel();
        this.signinFrame = new SigninFrame();
        this.changePasswordFrame = new ChangePasswordFrame();
        this.setVisible(true);
        this.add(this.loginPanel);

        try {
            Scanner nickname = new Scanner(new File(".\\src\\main\\java\\edu\\sustech\\xiangqi\\Nickname"));

            //这是登录框架的开始按键的相关响应代码部分
            loginPanel.getLoginButton().addActionListener(e -> {//button指开始的按键
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

                        //display the GameFrame
                        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        gameFrame.pack();
                        gameFrame. getRestartButton().setVisible(true);
                        gameFrame.setLocationRelativeTo(null);
                        gameFrame.setVisible(true);
                    }//密码错误
                    else{TimeForDisplayAndLater.displayAndHideJLabel(loginPanel.wrongLabel(),500);
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
            signinFrame.setVisible(true);
            signinFrame.setLocationRelativeTo(null);
            this.setVisible(false);
        });

        //从注册界面回到登录界面==============================================================
        signinFrame.getReturnButton().addActionListener(e -> {
            signinFrame.setVisible(false);
            this.setVisible(true);
        });

        //点击确认注册那个按键的操作判定（注册用户的相关判定）
        signinFrame.getConfirmPasswordButton().addActionListener(e->{
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
                    this.gameFrame = new GameFrame(null);
                    this.setupGameFrameListeners();
                    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameFrame.pack();
                    gameFrame.setLocationRelativeTo(null);
                    gameFrame.setVisible(true);
                    this.setVisible(false);
                });


        //改变密码的相关代码
        changePasswordFrame.getChangePasswordButton().addActionListener(e->{
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
            PlayGameSession newActiveSession = new PlayGameSession(gameFrame.getActiveSession().getPlayerNameID());//全新棋盘和红棋子先走
            gameFrame.setActiveSessionModel(newActiveSession);
            gameFrame.setCurrentCamp(newActiveSession.getCurrentCamp());
            //Update the ChessBoardPanel's reference to the new model and camp.
            gameFrame.getBoardPanel().setNewGameModel(newActiveSession.getChessBoardModel(), newActiveSession.getCurrentCamp());
            gameFrame.getStartbutton().setEnabled(true);
            gameFrame.getStartbutton().setText("点击开始");
            gameFrame.getBoardPanel().setGameInteractionEnabled(false);
            gameFrame.repaint(); // Force GameFrame to redraw everything
        });


        // 2. Game frame change information button
        //=====================================================
        gameFrame.getChangeinformation().addActionListener(e->{
            GamePersistence.saveGame(gameFrame.getActiveSession());
            changePasswordFrame.setVisible(true);
            gameFrame.setVisible(false);
        });
        //=====================================================


        // 3. Game frame return from password change (already defined)
        changePasswordFrame.getReturnToTheGame().addActionListener(e->{
            changePasswordFrame.setVisible(false);
            gameFrame.setVisible(true);
        });

        // 4. Save and Exit button
        gameFrame.getSaveAndOutButton().addActionListener(e->{
            // using GamePersistence.saveGame(gameFrame.activeSession)
            gameFrame.setVisible(false);
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });

        //5. 悔棋的响应器
        gameFrame.getTakeBackAMove().addActionListener(e->{
            ChessBoardModel model = gameFrame.getActiveSession().getChessBoardModel();
            CurrentCamp currentCamp = gameFrame.getActiveSession().getCurrentCamp();
            ChessBoardPanel boardPanel = gameFrame.getBoardPanel();

            if (model.getMoveHistory().isEmpty()) {
                boardPanel.setStatusMessage("无法悔棋", Color.RED, false);
                return;
            }
            if (model.getMoveHistory().isEmpty() == false && gameFrame.getBoardPanel().getInteractionEnabled() == false) {
                model.removeLastMove();
                currentCamp.returnTurn();
                boardPanel.setNewGameModel(model, currentCamp);
                gameFrame.getBoardPanel().setGameInteractionEnabled(true);
                boardPanel.setStatusMessage("悔棋成功！", Color.BLUE,  false);
                gameFrame.repaint();
            } else {
            model.removeLastMove();
            currentCamp.returnTurn();
            boardPanel.setNewGameModel(model, currentCamp);
            boardPanel.setStatusMessage("悔棋成功！", Color.BLUE,   false);
            gameFrame.repaint();
            }
        });

        //=============================================================
        gameFrame.getStartButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // a. 启用棋盘交互
                gameFrame.getBoardPanel().setGameInteractionEnabled(true);
                // b. 禁用“点击开始”按钮，防止重复点击，同时提示用户已开始
                gameFrame.getStartButton().setEnabled(false);
                gameFrame.getStartButton().setText("游戏中...");
            }
        });

        gameFrame.getSaveButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {//why this is what?
                if (!gameFrame.getIsTourist()) {
                    GamePersistence.saveGame(gameFrame.getActiveSession());
                } else {
                    System.out.println("you are Tourist exit - No save");
                }
                dispose();//close the game window
            }
        });

        gameFrame.getGiveUpButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //the current active camp is the one whose general is in check
                boolean isRedTurn = gameFrame.getActiveSession().getCurrentCamp().isRedTurn();

                String winner = isRedTurn ? "黑方" : "红方";
                String loser = isRedTurn ? "红方" : "黑方";

                gameFrame.getBoardPanel().setStatusMessage(winner + "胜利！（" + loser + "认输）", Color.GREEN, true);
                gameFrame.hideGiveUpOption();
                gameFrame.getBoardPanel().setGameInteractionEnabled(false);


            }
        });

    }
}
