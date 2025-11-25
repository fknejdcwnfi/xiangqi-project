package edu.sustech.xiangqi;


import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.ui.ChessBoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame  extends JFrame {

    private JButton Startbutton;
    private JButton changeinformation;
    private JButton savaAndOutButton;
    private JButton returntologinbutton;

    public GameFrame() {

        super("中国象棋");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // 1. 棋盘面板 (CENTER) - 保持不变
        ChessBoardModel model = new ChessBoardModel();
        ChessBoardPanel boardPanel = new ChessBoardPanel(model);
        this.add(boardPanel, BorderLayout.CENTER);
        // 2. 按钮的具体设置
        // 创建一个统一的尺寸，例如：宽 120，高 40 (根据你的喜好调整)
        Dimension buttonSize = new Dimension(120, 40);

        Startbutton = new JButton("点击开始");
        Startbutton.setPreferredSize(buttonSize); // 设置大小

        changeinformation = new JButton("修改信息");
        changeinformation.setPreferredSize(buttonSize); // 设置大小

        savaAndOutButton = new JButton("存档并退出");
        savaAndOutButton.setPreferredSize(buttonSize); // 设置大小

        returntologinbutton = new JButton("返回登录");
        returntologinbutton.setPreferredSize(buttonSize); // 设置大小

        // 3. 按钮内部布局 (buttonPanel)
        // 依然使用 GridLayout 排列4个按钮，但这次只负责排列，不负责拉伸整个页面
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 0, 20)); // 4行1列，垂直间距20
        // 设置 buttonPanel 的背景色透明或与背景一致，避免出现色差（可选）
        // buttonPanel.setOpaque(false);

        buttonPanel.add(Startbutton);
        buttonPanel.add(changeinformation);
        buttonPanel.add(savaAndOutButton);
        buttonPanel.add(returntologinbutton);

        // 4. 侧边容器 (sidePanel) - 关键步骤！
        // 我们用一个新面板包裹 buttonPanel，防止它被 BorderLayout 拉伸
        JPanel sidePanel = new JPanel();
        // 使用 GridBagLayout，它会让内部组件保持“首选大小”并在垂直方向居中
        sidePanel.setLayout(new GridBagLayout());
        sidePanel.add(buttonPanel);

        // 将这个不拉伸的容器放到窗口右侧
        this.add(sidePanel, BorderLayout.EAST);

        // ================= 关键功能实现开始 =================

        // *** 1. 默认禁用棋盘交互 ***
        // 您需要在 ChessBoardPanel 中实现 setGameInteractionEnabled 方法
        // 游戏开始前，棋盘应处于不可操作状态
        boardPanel.setGameInteractionEnabled(false);

        // *** 2. 为“点击开始”按钮添加监听器 ***
        Startbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // a. 启用棋盘交互
                boardPanel.setGameInteractionEnabled(true);
                // b. 禁用“点击开始”按钮，防止重复点击，同时提示用户已开始
                Startbutton.setEnabled(false);
                Startbutton.setText("游戏中...");
            }
        });

        // ================= 关键功能实现结束 =================

        // 5. 自动调整窗口大小
        // pack() 会根据棋盘和按钮的实际大小，自动把窗口收缩到最小（紧贴边缘）
        this.pack();
        this.setVisible(false); // 建议这里设为 true，或者在主程序里设
    }


    public void setVisible(boolean b) {
        super.setVisible(b);
    }


    public JButton getReturntologinbutton() {
        return returntologinbutton;
    }


    public JButton getChangeinformation() {
        return this.changeinformation;
    }

    public JButton getSavaAndOutButton() {
        return this.savaAndOutButton;
    }
}
