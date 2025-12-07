package edu.sustech.xiangqi;

import edu.sustech.xiangqi.ui.AncientButton;
import edu.sustech.xiangqi.ui.AudioPlayer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

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
    private BackgroundPanel backgroundPanel;


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

    public ChangePasswordFrame() {
        super("修改密码");
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setSize(500, 500);
        AudioPlayer.playLoopingSound("src/main/resources/Audio/我的歌声里.wav");
        // 初始化背景面板
        backgroundPanel = new BackgroundPanel();
        this.setContentPane(backgroundPanel);

        // 复用LoginPanel，修改文本适配注册场景
        this.setLayout(null);
        this.setVisible(false);
        //输入框是什么？
        //
        changePasswordLabel = new JLabel("修改密码");
        changePasswordLabel.setFont(new Font("华文行楷", Font.BOLD, 24)); // 使用默认字体，24磅，粗体
        changePasswordLabel.setSize(100, 50);
        changePasswordLabel.setLocation(210, 20);
        this.add(changePasswordLabel);
        changePasswordLabel.setVisible(true);
        //

        //用户名的设置框架
        oldPasswordField = new JTextField();
        oldPasswordField.setSize(150, 25);
        oldPasswordField.setLocation(195, 130);

        // 设置加粗边框：宽度设为2（或更大），颜色匹配界面风格
        oldPasswordField.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2)); // 深棕色+2px粗边框

        this.add(oldPasswordField);

        //用户名的设置框架
        oldPasswordText = new JLabel("原密码：");
        oldPasswordText.setFont(new Font("华文行楷", Font.PLAIN, 14)); // 14磅
        oldPasswordText.setSize(100, 50);
        oldPasswordText.setLocation(145, 120);
        this.add(oldPasswordText);

        //密码填写框架（登录注册框）
        newPasswordField = new JTextField();
        newPasswordField.setSize(150, 25);
        newPasswordField.setLocation(195, 190);
        // 设置加粗边框：宽度设为2（或更大），颜色匹配界面风格
        newPasswordField.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2)); // 深棕色+2px粗边框


        this.add(newPasswordField);

        theUserName = new JLabel("用户名：");
        theUserName.setFont(new Font("华文行楷", Font.PLAIN, 14)); // 14磅
        theUserName.setSize(100, 50);
        theUserName.setLocation(145, 60);
        this.add(theUserName);

        theUserNameText = new JTextField();
        theUserNameText.setSize(150, 25);
        theUserNameText.setLocation(195, 70);
        // 设置加粗边框：宽度设为2（或更大），颜色匹配界面风格
        theUserNameText.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2)); // 深棕色+2px粗边框

        this.add(theUserNameText);

        //密码标签（）
        newPasswordText = new JLabel("新密码：");
        newPasswordText.setFont(new Font("华文行楷", Font.PLAIN, 14)); // 14磅
        newPasswordText.setSize(100, 50);
        newPasswordText.setLocation(145, 180);


        this.add(newPasswordText);
        //
        //开始游玩按键，按了就可以进入页面
        changePasswordButton = new AncientButton("确认修改");
        changePasswordButton.setFont(new Font("华文行楷", Font.BOLD, 16)); // 16磅，粗体
        changePasswordButton.setSize(100, 50);
        changePasswordButton.setLocation(150, 350);

        // 3. 背景/前景色（适配浅色棋盘背景）
        changePasswordButton.setBackground(new Color(245, 222, 179)); // 浅棕黄色
        changePasswordButton.setForeground(new Color(82, 53, 25)); // 深棕文字

        this.add(changePasswordButton);

        returnTotheGame = new AncientButton("返回游戏");
        returnTotheGame.setFont(new Font("华文行楷", Font.BOLD, 16)); // 16磅，粗体
        returnTotheGame.setSize(100, 50);
        returnTotheGame.setLocation(250, 350);
        this.add(returnTotheGame);

        susTochangePassword = new JLabel("修改密码成功");
        susTochangePassword.setFont(new Font("华文行楷", Font.PLAIN, 12)); // 12磅
        susTochangePassword.setForeground(java.awt.Color.BLUE); // 成功提示用蓝色
        susTochangePassword.setSize(200, 50);
        susTochangePassword.setLocation(210, 280);
        this.add(susTochangePassword);
        susTochangePassword.setVisible(false);

        errorOne = new JLabel("文件写入失败");
        errorOne.setFont(new Font("华文行楷", Font.PLAIN, 12)); // 12磅
        errorOne.setForeground(java.awt.Color.BLUE); // 成功提示用蓝色
        errorOne.setSize(200, 50);
        errorOne.setLocation(210, 280);
        this.add(errorOne);
        errorOne.setVisible(false);

        errorTwo = new JLabel("验证失败，请重试！");
        errorTwo.setFont(new Font("华文行楷", Font.PLAIN, 12)); // 12磅
        errorTwo.setForeground(java.awt.Color.BLUE); // 成功提示用蓝色
        errorTwo.setSize(200, 50);
        errorTwo.setLocation(200, 280);
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

    public String theUserNameText() {
        return theUserNameText.getText();
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
