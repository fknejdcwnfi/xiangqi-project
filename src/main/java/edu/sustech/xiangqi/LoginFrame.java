package edu.sustech.xiangqi;

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
        this.gameFrame = new GameFrame();
        this.loginPanel = new LoginPanel();
        this.signinFrame = new SigninFrame();
        this.changePasswordFrame = new ChangePasswordFrame();
        this.setVisible(true);
        this.add(this.loginPanel);

        try {
            Scanner nickname = new Scanner(new File(".\\src\\main\\java\\edu\\sustech\\xiangqi\\Nickname"));

            loginPanel.getLoginButton().addActionListener(e -> {//button指开始的按键
                //String importUserName = loginPanel.getUsername().getText();
                //String importUserPassword = loginPanel.getPassword().getText();

                if (enteruser(loginPanel.getUsername())>=0)//指用户存在
                {
                    if(enterpassword(loginPanel.getPassword(),enteruser(loginPanel.getUsername()))){/// /////////////////////////////////////密码正确
                        this.setVisible(false);
                        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        gameFrame.pack();
                        gameFrame.getReturntologinbutton().setVisible(true);
                        gameFrame.setLocationRelativeTo(null);
                        gameFrame.setVisible(true);////////////////////////////////////////////
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

        loginPanel.getSignInButton().addActionListener(e -> {//从登录到注册的启动按键
            signinFrame.setVisible(true);
            this.setVisible(false);
        });

        signinFrame.getReturnButton().addActionListener(e -> {//从注册界面回到登录界面
            signinFrame.setVisible(false);
            this.setVisible(true);
        });

        gameFrame.getReturntologinbutton().addActionListener(e -> {//从游戏框架回来到登录界面
            gameFrame.setVisible(false); // 隐藏游戏
            this.setVisible(true); // 显示登录
        });

        signinFrame.getConfirmPasswordButton().addActionListener(e->{//点击确认注册那个按键的操作判定
//            String putnickname=nickname.getText();
//            String putsetpassword=setpassword.getText();
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

                        // 1.2 注册成功，显示提示并设置延时跳转
                        signinFrame.getConfirmsuscess().setText("注册成功！"); // 假设 Confirmsuscess 是成功的提示标签
                        // 关键：Timer 的action 将执行跳转
                        Timer jumpTimer = new Timer(500, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                //signinFrame.getConfirmsuscess().setVisible(false);
                                // --- 延时 0.5 秒后才执行的跳转逻辑 ---
                                signinFrame.getConfirmsuscess().setVisible(false);
                                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                gameFrame.pack();
                                gameFrame.getReturntologinbutton().setVisible(true);
                                gameFrame.setLocationRelativeTo(null);
                                gameFrame.setVisible(true);
                                signinFrame.setVisible(false);
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

        ///////////////////////////////////现在来搞这个鼠标点击事件
        gameFrame.getChangeinformation().addActionListener(e->{//这个还没有做
            changePasswordFrame.setVisible(true);
            gameFrame.setVisible(false);
        });

        changePasswordFrame.getChangePasswordButton().addActionListener(e->{
//            String yourUserName = theUserNameText.getText();
//            String yourOldPassword = oldPassword.getText();
//            String yourNewPassword = newPassword.getText();

            // 1. 获取用户索引
            int userIndex = InterChecking.enteruser(changePasswordFrame.getTheUserName());

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


        changePasswordFrame.getReturnToTheGame().addActionListener(e->{
            changePasswordFrame.setVisible(false);
            gameFrame.setVisible(true);
        });

        //////////////////////////////这里是存档和退出的功能
        gameFrame.getSavaAndOutButton().addActionListener(e->{
            gameFrame.setVisible(false);
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
        /// /////////////////////////
    }
}
