package edu.sustech.xiangqi;

import edu.sustech.xiangqi.ui.AncientButton;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class SigninFrame extends JFrame {
    private JButton confirmPasswordButton;
    private JButton returnButton;
    private JLabel yourNameLabel;
    private JLabel yourPasswordLabel;
    private JLabel signin;
    private JLabel Confirmsuscess;
    private JTextField yourNameTextField;
    private JTextField yourPasswordTextField;
    private JLabel errorOne;
    private JLabel errorTwo;
    private JLabel errorThree;
    private BackgroundPanel backgroundPanel;


    // 独立背景面板类（复用LoginFrame的背景逻辑）
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel() {
            setLayout(null);
            setPreferredSize(new Dimension(500, 500));
            // 加载注册背景图
            URL imgUrl = getClass().getResource("/Picture/生成象棋登录界面照片2.png");
            if (imgUrl != null) backgroundImage = new ImageIcon(imgUrl).getImage();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public SigninFrame() {
        super("登录");
        this.setLocationRelativeTo(null);
        this.setSize(500, 500);
        // 初始化背景面板
        backgroundPanel = new BackgroundPanel();
        this.setContentPane(backgroundPanel);

        // 复用LoginPanel，修改文本适配注册场景
        this.setLayout(null);
        this.setVisible(false);
        returnButton = new AncientButton("返回");
        returnButton.setFont(new Font("华文行楷", Font.BOLD, 16)); // 16磅，粗体
        returnButton.setSize(100, 50);
        returnButton.setLocation(250, 350);
        this.add(returnButton);

        signin = new JLabel("注册");
        signin.setFont(new Font("华文行楷", Font.BOLD, 36)); // 24磅，粗体
        signin.setSize(100, 50);
        signin.setLocation(220, 50);
        this.add(signin);
        signin.setVisible(true);

        //用户名的设置框架（注册框的那个空格）

        yourNameTextField = new JTextField();
        // 设置加粗边框：宽度设为2（或更大），颜色匹配界面风格
        yourNameTextField.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2)); // 深棕色+2px粗边框
        yourNameTextField.setSize(150, 25);
        yourNameTextField.setLocation(190, 130);
        this.add(yourNameTextField);

        //用户名的设置框架（注册框的标签）
        yourNameLabel = new JLabel("昵称：");
        yourNameLabel.setFont(new Font("华文行楷", Font.PLAIN, 14)); // 14磅
        yourNameLabel.setSize(100, 50);
        yourNameLabel.setLocation(150, 120);
        this.add(yourNameLabel);

        //密码填写框架（注册的密码）
        yourPasswordTextField = new JTextField();
        yourPasswordTextField.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2)); // 深棕色+2px粗边框
        yourPasswordTextField.setSize(150, 25);
        yourPasswordTextField.setLocation(190, 190);
        this.add(yourPasswordTextField);

        //密码标签（注册的密码框）
        yourPasswordLabel= new JLabel("密码：");
        yourPasswordLabel.setFont(new Font("华文行楷", Font.PLAIN, 14)); // 14磅
        yourPasswordLabel.setSize(100, 50);
        yourPasswordLabel.setLocation(150, 180);
        this.add(yourPasswordLabel);


        confirmPasswordButton = new AncientButton("注册");
        confirmPasswordButton.setFont(new Font("华文行楷", Font.BOLD, 16)); // 16磅，粗体
        confirmPasswordButton.setSize(100, 50);
        confirmPasswordButton.setLocation(150, 350);
        this.add(confirmPasswordButton);

        Confirmsuscess = new JLabel("确认成功");
        Confirmsuscess.setFont(new Font("华文行楷", Font.PLAIN, 12)); // 12磅
        Confirmsuscess.setForeground(Color.BLUE); // 成功提示用蓝色
        Confirmsuscess.setSize(200, 50);
        Confirmsuscess.setLocation(230, 280);
        this.add(Confirmsuscess);
        Confirmsuscess.setVisible(false);

        errorOne = new JLabel("密码输入过短");
        errorOne.setFont(new Font("华文行楷", Font.PLAIN, 12)); // 12磅
        errorOne.setForeground(Color.BLUE); // 成功提示用蓝色
        errorOne.setSize(200, 50);
        errorOne.setLocation(210, 280);
        this.add(errorOne);
        errorOne.setVisible(false);

        errorTwo = new JLabel("用户名不合法或已有其他玩家使用");
        errorTwo.setFont(new Font("华文行楷", Font.PLAIN, 12)); // 12磅
        errorTwo.setForeground(Color.BLUE); // 成功提示用蓝色
        errorTwo.setSize(200, 50);
        errorTwo.setLocation(160, 280);
        this.add(errorTwo);
        errorTwo.setVisible(false);

        errorThree = new JLabel("昵称和密码都不符合规定");
        errorThree.setFont(new Font("华文行楷", Font.PLAIN, 12)); // 12磅
        errorThree.setForeground(Color.BLUE); // 成功提示用蓝色
        errorThree.setSize(200, 50);
        errorThree.setLocation(180, 280);
        this.add(errorThree);
        errorThree.setVisible(false);
    }
    public String getYourNameTextField() {
        return yourNameTextField.getText();
    }
    public String getYourPasswordTextField() {
        return yourPasswordTextField.getText();
    }
    public JButton getConfirmPasswordButton() {
        return confirmPasswordButton;
    }
    public JButton getReturnButton() {
        return returnButton;
    }

    public JLabel getConfirmsuscess() {
        return Confirmsuscess;
    }

    public JLabel getErrorOne() {
        return errorOne;
    }
    public JLabel getErrorTwo() {
        return errorTwo;
    }
    public JLabel getErrorThree() {
        return errorThree;
    }
}
