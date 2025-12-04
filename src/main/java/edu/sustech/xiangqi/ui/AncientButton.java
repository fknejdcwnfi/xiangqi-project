package edu.sustech.xiangqi.ui;

import javax.swing.*;
import java.awt.*;

public class AncientButton extends JButton {
    // 可选：支持自定义颜色/圆角
    private Color bgColor = new Color(245, 222, 179); // 默认浅棕背景
    private Color borderColor = new Color(139, 69, 19); // 默认深棕边框
    private int borderRadius = 15; // 默认圆角大小

    // 构造方法（直接传按钮文字）
    public AncientButton(String text) {
        super(text);
        initStyle(); // 初始化按钮样式
    }

    // 重载构造方法（支持自定义背景色）
    public AncientButton(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        initStyle();
    }

    // 初始化按钮基础样式
    private void initStyle() {
        this.setContentAreaFilled(false); // 禁用默认背景
        this.setBorderPainted(false); // 禁用默认边框
        this.setOpaque(false);            // 可选：设置按钮透明，避免底色干扰

        this.setFont(new Font("华文行楷", Font.BOLD, 16)); // 书法字体
        this.setForeground(new Color(82, 53, 25)); // 文字颜色
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // 手型光标
    }

    // 重写绘制逻辑（封装所有美化效果）
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. 绘制圆角背景（支持悬停变色）
        if (this.getModel().isRollover()) { // 鼠标悬停时
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 215, 0), 0, getHeight(), bgColor));
        } else {
            g2.setPaint(new GradientPaint(0, 0, bgColor, 0, getHeight(), new Color(210, 180, 140)));
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);

        // 2. 绘制祥云纹理（简化版，可根据需要调整）
        g2.setColor(new Color(255, 248, 220, 150));
        g2.fillOval(10, 10, 20, 15);
        g2.fillOval(25, 5, 25, 20);
        g2.fillOval(45, 12, 20, 15);
        g2.fillOval(70, 10, 20, 15);

        // 3. 绘制描边
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, borderRadius, borderRadius);

        // 4. 绘制文字（调用父类方法，保证文字居中）
        super.paintComponent(g);
        g2.dispose();
    }

    // 可选：提供setter方法，动态修改样式
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        repaint();
    }

    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
        repaint();
    }


}
