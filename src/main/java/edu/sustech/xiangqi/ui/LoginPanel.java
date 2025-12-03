package edu.sustech.xiangqi.ui;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton loginButton;
    private JButton signInButton;
    private JLabel wrongLabel;
    private JLabel unexistLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton touristButton;

    public LoginPanel() {
        this.setLayout(null); // 继续使用 null 布局以便于移植
        this.setPreferredSize(new Dimension(500, 500)); // 设定首选尺寸
        this.setVisible(true);
        //输入框是什么？
        //
        JLabel login = new JLabel("登录");
        login.setFont(new Font("Dialog", Font.BOLD, 24)); // 使用默认字体，24磅，粗体
        login.setSize(100, 50);
        login.setLocation(225, 50);
        this.add(login);
        //

        //用户名的设置框架
        usernameField = new JTextField();
        usernameField.setSize(150, 25);
        usernameField.setLocation(185, 130);
        this.add(usernameField);
        //用户名的设置框架
        usernameLabel = new JLabel("用户名：");
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 14)); // 14磅
        usernameLabel.setSize(100, 50);
        usernameLabel.setLocation(130, 120);
        this.add(usernameLabel);

        //密码填写框架（登录注册框）
        passwordField = new JTextField();
        passwordField.setSize(150, 25);
        passwordField.setLocation(185, 190);
        this.add(passwordField);
        //密码标签（）

        passwordLabel = new JLabel("密码：");
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 14)); // 14磅
        passwordLabel.setSize(100, 50);
        passwordLabel.setLocation(140, 180);
        this.add(passwordLabel);

        //开始游玩按键，按了就可以进入页面
        loginButton = new JButton("用户登录");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 16)); // 16磅，粗体
        loginButton.setSize(100, 50);
        loginButton.setLocation(200, 300);
        this.add(loginButton);

        wrongLabel = new JLabel("密码错误");
        wrongLabel.setFont(new Font("Dialog", Font.PLAIN, 12)); // 12磅
        wrongLabel.setForeground(java.awt.Color.RED); // 设置颜色为红色
        wrongLabel.setSize(100, 50);
        wrongLabel.setLocation(230, 230);
        this.add(wrongLabel);
        wrongLabel.setVisible(false);

        unexistLabel = new JLabel("用户名不存在");
        unexistLabel.setFont(new Font("Dialog", Font.PLAIN, 12)); // 12磅
        unexistLabel.setForeground(java.awt.Color.RED); // 设置颜色为红色
        unexistLabel.setSize(200, 50);
        unexistLabel.setLocation(210, 230);
        this.add(unexistLabel);
        unexistLabel.setVisible(false);

        signInButton = new JButton("注册");
        signInButton.setFont(new Font("Dialog", Font.BOLD, 16)); // 16磅，粗体
        signInButton.setSize(100, 50);
        signInButton.setLocation(200, 350);
        this.add(signInButton);

        touristButton = new JButton("游客登录");
        touristButton.setFont(new Font("Dialog", Font.BOLD, 16)); // 16磅，粗体
        touristButton.setSize(100, 50);
        touristButton.setLocation(200, 400);
        this.add(touristButton);
    }

    public JButton getLoginButton() { return loginButton; }
    public JButton getSignInButton() { return signInButton; }
    public JButton getTouristButton() { return touristButton; }
    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public JLabel wrongLabel() { return wrongLabel; }
    public JLabel unexistLabel() { return unexistLabel; }

}
