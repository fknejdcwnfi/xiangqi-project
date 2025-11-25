package edu.sustech.xiangqi;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordFrame extends JFrame {
    private JLabel changePasswordLabel;
    private JTextField oldPasswordField;
    private JLabel oldPasswordText;
    private JTextField newPasswordField;
    private JLabel newPasswordText;
    private JButton changePasswordButton;
    private JButton returnTotheGame;
    private JLabel theUserName;
    private JTextField theUserNameText;
    private JLabel susTochangePassword;
    private JLabel errorOne;
    private JLabel errorTwo;

    public ChangePasswordFrame() {
        super("修改密码");
        this.setLayout(null); // 继续使用 null 布局以便于移植
        this.setPreferredSize(new Dimension(500, 500)); // 设定首选尺寸
        this.setVisible(false);
        //输入框是什么？
        //
        changePasswordLabel = new JLabel("修改密码");
        changePasswordLabel.setFont(new Font("Dialog", Font.BOLD, 24)); // 使用默认字体，24磅，粗体
        changePasswordLabel.setSize(100, 50);
        changePasswordLabel.setLocation(210, 20);
        this.add(changePasswordLabel);
        changePasswordLabel.setVisible(false);
        //

        //用户名的设置框架
        oldPasswordField = new JTextField();
        oldPasswordField.setSize(150, 25);
        oldPasswordField.setLocation(195, 130);
        this.add(oldPasswordField);

        //用户名的设置框架
        oldPasswordText = new JLabel("原密码：");
        oldPasswordText.setFont(new Font("Dialog", Font.PLAIN, 14)); // 14磅
        oldPasswordText.setSize(100, 50);
        oldPasswordText.setLocation(145, 120);
        this.add(oldPasswordText);

        //密码填写框架（登录注册框）
        newPasswordField = new JTextField();
        newPasswordField.setSize(150, 25);
        newPasswordField.setLocation(195, 190);
        this.add(newPasswordField);

        theUserName = new JLabel("用户名：");
        theUserName.setFont(new Font("Dialog", Font.PLAIN, 14)); // 14磅
        theUserName.setSize(100, 50);
        theUserName.setLocation(145, 60);
        this.add(theUserName);

        theUserNameText = new JTextField();
        theUserNameText.setSize(150, 25);
        theUserNameText.setLocation(195, 70);
        this.add(theUserNameText);

        //密码标签（）
        newPasswordText = new JLabel("新密码：");
        newPasswordText.setFont(new Font("Dialog", Font.PLAIN, 14)); // 14磅
        newPasswordText.setSize(100, 50);
        newPasswordText.setLocation(145, 180);
        this.add(newPasswordText);
        //
        //开始游玩按键，按了就可以进入页面
        changePasswordButton = new JButton("确认修改");
        changePasswordButton.setFont(new Font("Dialog", Font.BOLD, 16)); // 16磅，粗体
        changePasswordButton.setSize(100, 50);
        changePasswordButton.setLocation(150, 350);
        this.add(changePasswordButton);

        returnTotheGame = new JButton("返回游戏");
        returnTotheGame.setFont(new Font("Dialog", Font.BOLD, 16)); // 16磅，粗体
        returnTotheGame.setSize(100, 50);
        returnTotheGame.setLocation(250, 350);
        this.add(returnTotheGame);

        susTochangePassword = new JLabel("修改密码成功");
        susTochangePassword.setFont(new Font("Dialog", Font.PLAIN, 12)); // 12磅
        susTochangePassword.setForeground(java.awt.Color.BLUE); // 成功提示用蓝色
        susTochangePassword.setSize(200, 50);
        susTochangePassword.setLocation(210, 280);
        this.add(susTochangePassword);
        susTochangePassword.setVisible(false);

        errorOne = new JLabel("文件写入失败");
        errorOne.setFont(new Font("Dialog", Font.PLAIN, 12)); // 12磅
        errorOne.setForeground(java.awt.Color.BLUE); // 成功提示用蓝色
        errorOne.setSize(200, 50);
        errorOne.setLocation(210, 280);
        this.add(errorOne);
        errorOne.setVisible(false);

        errorTwo = new JLabel("验证失败：用户名不存在、旧密码错误或新密码过短");
        errorTwo.setFont(new Font("Dialog", Font.PLAIN, 12)); // 12磅
        errorTwo.setForeground(java.awt.Color.BLUE); // 成功提示用蓝色
        errorTwo.setSize(200, 50);
        errorTwo.setLocation(170, 280);
        this.add(errorTwo);
        errorTwo.setVisible(false);
    }

    public JButton getChangePasswordButton() {
        return changePasswordButton;
    }

    public JButton getReturnToTheGame() {
        return returnTotheGame;
    }

    public String getOldPassword() {
        return oldPasswordField.getText();
    }

    public String getNewPassword() {
        return newPasswordField.getText();
    }

    public String getTheUserName() {
        return theUserName.getText();
    }
    public JLabel getErrorOne() {
        return errorOne;
    }
    public JLabel getErrorTwo() {
        return errorTwo;
    }
    public JLabel getsusTochangePassword() {
        return susTochangePassword;
    }
}
