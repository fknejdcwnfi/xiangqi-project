package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.CurrentCamp;
import edu.sustech.xiangqi.GameFrame;
import edu.sustech.xiangqi.MoveEveryStep;
import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.AbstractPiece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.Timer;



public class ChessBoardPanel extends JPanel {
    private ChessBoardModel model;

    // 新增：定义一个全局的提示标签
    private JLabel statusLabel;

    /**
     * 单个棋盘格子的尺寸（px）
     */
    private static final int CELL_SIZE = 64;

    /**
     * 棋盘边界与窗口边界的边距
     */
    private static final int MARGIN = 40;

    /**
     * 棋子的半径
     */
    private static final int PIECE_RADIUS = 25;

    private AbstractPiece selectedPiece = null;

    private boolean interactionEnabled = false;

    private CurrentCamp currentCamp;

    //调用那个检测下一步的红圈标记方法。
    private java.util.List<Point> legalMoves = new ArrayList<>();
    private GameFrame gameFrame;

    public ChessBoardPanel(ChessBoardModel model, CurrentCamp camp, GameFrame gameFrame) {
        this.model = model;
        this.currentCamp = camp; //initialize with the passed object
        this.gameFrame = gameFrame;
        // 1. 设置布局为 null，这样我们可以用 setBounds 随意放置 Label
        this.setLayout(null);

        setPreferredSize(new Dimension(
                CELL_SIZE * (ChessBoardModel.getCols() - 1) + MARGIN * 2,
                CELL_SIZE * (ChessBoardModel.getRows() - 1) + MARGIN * 2
        ));
        setBackground(new Color(220, 179, 92));


        // 2. 初始化提示标签
        initStatusLabel();

        addMouseListener(new MouseAdapter() {//鼠标点击获取坐标的核心逻辑
            @Override
            public void mouseClicked(MouseEvent e) {//获取MouseEvent对象的坐标
                handleMouseClick(e.getX(), e.getY());//执行相应的方法
            }
        });
    }

    //  新增：初始化标签的方法
    private void initStatusLabel() {
        statusLabel = new JLabel("请点击右侧“点击开始”", SwingConstants.CENTER); // 文字居中
        statusLabel.setFont(new Font("楷体", Font.BOLD, 24)); // 字体大一点
        statusLabel.setForeground(Color.RED); // 醒目的颜色

        // 计算居中位置 (假设棋盘大概宽500-600)
        // setBounds(x, y, width, height)
        statusLabel.setBounds(100, 300, 400, 60);

        statusLabel.setVisible(true); // 默认显示提示
        this.add(statusLabel); //  把标签加到“自己”（this）上面
    }

    //  修改：updateTurnLabel 方法，用于在切换回合时更新提示文字
    private void updateTurnLabel() {
        if (!interactionEnabled) return; // 如果游戏还没开始，不更新回合文字

        if (currentCamp.isRedTurn()) {
            statusLabel.setText("当前回合：红方");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("当前回合：黑方");
            statusLabel.setForeground(Color.BLACK);
        }
        statusLabel.setVisible(true); // 确保文字显示
    }
    public ChessBoardModel model() {
        return model;
    }

    //=====================================================================
    public void setGameInteractionEnabled(boolean enabled) {
        this.interactionEnabled = enabled; // 使用 this

        if (interactionEnabled) {
            // 游戏开始
            updateTurnLabel();   // 显示 "当前回合：红方"
            gameFrame.startGameTimer();
        } else {
            gameFrame.stopGameTimer();
            // 禁用时
            if (this.model.getMoveHistory().isEmpty()) {
            statusLabel.setText("请点击开始");
            statusLabel.setForeground(Color.RED);
            statusLabel.setVisible(true);
        }else {
                return;
            }
        }
        // 重新绘制，确保 Label 刷新
        repaint();
    }

    //===========================================================================

    private void handleMouseClick(int x, int y) {

        // *** 关键检查 ***//===========================================================
        if (!interactionEnabled) {
            return;
        }
        //==============================================================================

        int col = Math.round((float) (x - MARGIN) / CELL_SIZE);
        int row = Math.round((float) (y - MARGIN) / CELL_SIZE);

        //吃子移动的代码如下
        if (!model.isValidPosition(row, col)) {//model 是‌棋盘模型对象‌
            return;
        }

        if (selectedPiece == null) {//selectedPiece是指我当前选中的棋子
            AbstractPiece piece = model.getPieceAt(row, col);
            //  校验：只有当前回合的阵营才能选中棋子
            if (piece != null) {
                if (piece.isRed() == currentCamp.isRedTurn()) {
                    selectedPiece = piece; // 选中成功
                    calculateLegalMoves(selectedPiece);
                } else {
                    statusLabel.setText("现在不是你的回合！");
                    statusLabel.setForeground(Color.BLUE);
                    statusLabel.setVisible(true);
                    // 2. 设置一个计时器，1秒后隐藏文字，防止挡住棋子
                    Timer timer = new Timer(1000, e -> statusLabel.setVisible(false));
                    timer.setRepeats(false);
                    timer.start();
                }
            }

        } else {//此时有选中的状态，当前已选中棋子 -> 尝试移动或吃子
            AbstractPiece target = model.getPieceAt(row, col);
            boolean moveSuccess = false; // 标记是否移动成功
            // A. 点击了自己的棋子 -> 重新选中（换一个子）
            if (target != null && target.isRed() == selectedPiece.isRed()) {
                // 只有也是己方棋子才换选中，否则视为移动目标
                selectedPiece = target;
                calculateLegalMoves(selectedPiece);//=====================

                repaint();
                return; // 仅仅是换了选中的子，不切换回合
            }
            // B. 点击了空地或敌人 -> 尝试移动
            if (target != null) {
                if (target.isRed() == selectedPiece.isRed()) {
                    selectedPiece = target;
                    calculateLegalMoves(selectedPiece); // 重新计算合法位置
                    repaint();
                    return; // 直接返回，不执行后续吃子逻辑
                }

                if (selectedPiece.canMoveTo(row, col, model)) {

                    // Start of Self-Check Logic for Capture
                    ChessBoardModel currentModel = model.deepCopy();
                    AbstractPiece pieceToMoveInCopy = currentModel.getPieceAt(selectedPiece.getRow(), selectedPiece.getCol());
                    AbstractPiece targetPieceInCopy = currentModel.getPieceAt(row, col);

                    // Simulate the move in the copy
                    if (targetPieceInCopy != null) {
                        currentModel.remove(targetPieceInCopy); // Remove target
                    }
                    if (pieceToMoveInCopy != null) {
                        currentModel.movePieceForce(pieceToMoveInCopy, row, col);
                    }

                    // Check for self-check
                    boolean isSelfCheck = currentModel.isInCheck(currentCamp.isRedTurn());
                    if (isSelfCheck) {
                        setStatusMessage("将导致将军！请重新选择！", Color.RED, false);
                        selectedPiece = null;
                        legalMoves.clear();
                        repaint();
                        return; // ABORT move
                    }
                    // End of Self-Check Logic

                    //记录吃子的情况先记录在移除
                    MoveEveryStep move = new MoveEveryStep(selectedPiece, row, col, target, this.currentCamp);
                    model.recordMove(move);
                    //这是记录棋子的吃子情况

                    // 保存 target 引用，先移除目标
                    model.remove(target);
                    // 直接强制移动（跳过再次校验，避免因目标已被移除影响逻辑）
                    model.movePieceForce(selectedPiece, row, col);//？
                    selectedPiece = null;
                    moveSuccess = true;
                } else {
                    selectedPiece = null;
                    repaint();//失败后也要重新绘制来清除选中框
                    return;
                }
            } else {
                // 目标为空，走普通移动，使用正常的 movePiece（含校验）
                if (selectedPiece.canMoveTo(row, col, model)) {
                    //start of self-check logic for simple move
                    ChessBoardModel currentModel = model.deepCopy();
                    AbstractPiece pieceToMoveInCopy = currentModel.getPieceAt(selectedPiece.getRow(), selectedPiece.getCol());

                    if (pieceToMoveInCopy != null) {
                        currentModel.movePieceForce(pieceToMoveInCopy, row, col);
                    }

                    boolean isSelfCheck = currentModel.isInCheck(currentCamp.isRedTurn());
                    if (isSelfCheck) {
                        setStatusMessage("将导致将军！请重新选择！", Color.RED, false);
                        selectedPiece = null;
                        legalMoves.clear();
                        repaint();
                        return;
                    }
                    //the end of self check logic

                    //记录普哦她那个移动的情况
                    MoveEveryStep move = new MoveEveryStep(selectedPiece, row, col, null, this.currentCamp);
                    model.recordMove(move);//实现存储和输出文字（在Model方法里面）
                    //这是记录普通的移动过情况

                    model.movePiece(selectedPiece, row, col);
                    selectedPiece = null;
                    moveSuccess = true;
                } else {
                    selectedPiece = null;
                    legalMoves.clear(); // 新增：清空红圈
                    repaint();//同上
                    return;
                }
            }
            if (moveSuccess) {
                // 1. 先获取移动前的阵营（关键：此时还未切换回合）
                boolean isRedTurn = currentCamp.isRedTurn();
                boolean isCheck;
                boolean messageShown = false; // <--- ADD THIS FLAG

                // 2. 根据移动前的阵营，检测对方是否被将军
                if (isRedTurn) {
                    isCheck = model.isInCheck(false); // 红方走后检测黑方
                    if (isCheck) {
                        setStatusMessage("红方将黑方", Color.BLACK,  true);
                        messageShown = true;
                    }
                } else {
                    isCheck = model.isInCheck(true); // 黑方走后检测红方
                    if (isCheck) {
                        setStatusMessage("黑方将红方", Color.RED, true);
                        messageShown = true;
                    }
                }
                currentCamp.nextTurn(); // 切换红黑
                legalMoves.clear();
                selectedPiece = null;

                if (!messageShown) {//Only update turn label if we didn't just show a warning
                    updateTurnLabel();
                }

            }
        }
        // 处理完点击事件后，需要重新绘制ui界面才能让界面上的棋子“移动”起来
        // Swing 会将多个请求合并后再重新绘制，因此调用 repaint 后gui不会立刻变更
        // repaint 中会调用 paintComponent，从而重新绘制gui上棋子的位置等
        repaint();
        boolean inCheck = model.isInCheck(currentCamp.isRedTurn());
        boolean hasMoves = model.hasLegalMoves(currentCamp.isRedTurn());
        String currentCampName = currentCamp.isRedTurn() ? "红方" : "黑方";
        if (!hasMoves) {
            String message;
            Color color = Color.GREEN;
            if (inCheck) {
                // Checkmate: The previous player wins
                String winner = currentCamp.isRedTurn() ? "黑方" : "红方";
                message = winner + "胜利！（将军且无路可走）";
                if (currentCamp.isRedTurn()) {
                    gameFrame.addBlackCampScore();
                    gameFrame.updateScoreLabel();
                    repaint();
                } else  {
                    gameFrame.addRedCampScore();
                    gameFrame.updateScoreLabel();
                    repaint();
                }
            } else {
                // Stalemate: Draw
                message = "和局！（无路可走但未被将军）";
                color = Color.BLUE;
            }

            // Show persistent message (no timer to fade it)
            setStatusMessage(message + "被将军！", color, true);
            gameFrame.hideGiveUpOption(); // 确保认输按钮隐藏
            gameFrame.getEndUpPeaceButton().setEnabled(false);
            this.setGameInteractionEnabled(false);

        } else if (inCheck) {
            Color checkColor = currentCamp.isRedTurn() ? Color.RED : Color.BLACK;
            setStatusMessage(currentCampName + "被将军！", checkColor, true);
            gameFrame.showGiveUpOption(currentCampName);
        } else {
            updateTurnLabel(); // 更新回合文字
            gameFrame.hideGiveUpOption(); // 确保认输按钮隐藏
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Demo的GUI都是由Swing中基本的组件组成的，比如背景的格子是用许多个line组合起来实现的，棋子是先绘制一个circle再在上面绘制一个text实现的
        // 因此绘制GUI的过程中需要自己手动计算每个组件的位置（坐标）
        drawBoard(g2d);
        drawPieces(g2d);
        drawLegalMoves(g);//new bulid
    }

    /**
     * 绘制棋盘
     */
    private void drawBoard(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));

        // 绘制横线
        for (int i = 0; i < ChessBoardModel.getRows(); i++) {
            int y = MARGIN + i * CELL_SIZE;
            g.drawLine(MARGIN, y, MARGIN + (ChessBoardModel.getCols() - 1) * CELL_SIZE, y);
        }

        // 绘制竖线
        for (int i = 0; i < ChessBoardModel.getCols(); i++) {
            int x = MARGIN + i * CELL_SIZE;
            if (i == 0 || i == ChessBoardModel.getCols() - 1) {
                // 两边的竖线贯通整个棋盘
                g.drawLine(x, MARGIN, x, MARGIN + (ChessBoardModel.getRows() - 1) * CELL_SIZE);
            } else {
                // 中间的竖线分为上下两段（楚河汉界断开）
                g.drawLine(x, MARGIN, x, MARGIN + 4 * CELL_SIZE);
                g.drawLine(x, MARGIN + 5 * CELL_SIZE, x, MARGIN + (ChessBoardModel.getRows() - 1) * CELL_SIZE);
            }
        }

        //画两个x线用于将和帅
        g.drawLine(MARGIN  + 3 * CELL_SIZE, MARGIN, MARGIN + 5 * CELL_SIZE, MARGIN + 2 * CELL_SIZE);
        g.drawLine(MARGIN  + 5 * CELL_SIZE, MARGIN, MARGIN + 3 * CELL_SIZE, MARGIN + 2 * CELL_SIZE);
        g.drawLine(MARGIN  + 3 * CELL_SIZE, MARGIN + 9 * CELL_SIZE, MARGIN + 5 * CELL_SIZE, MARGIN + 7 * CELL_SIZE);
        g.drawLine(MARGIN  + 3 * CELL_SIZE, MARGIN + 7 * CELL_SIZE, MARGIN + 5 * CELL_SIZE, MARGIN + 9 * CELL_SIZE);

        // 绘制“楚河”和“汉界”这两个文字
        g.setColor(Color.BLACK);
        g.setFont(new Font("楷体", Font.BOLD, 24));

        int riverY = MARGIN + 4 * CELL_SIZE + CELL_SIZE / 2;

        String chuHeText = "楚河";
        FontMetrics fm = g.getFontMetrics();
        int chuHeWidth = fm.stringWidth(chuHeText);
        g.drawString(chuHeText, MARGIN + CELL_SIZE * 2 - chuHeWidth / 2, riverY + 8);

        String hanJieText = "汉界";
        int hanJieWidth = fm.stringWidth(hanJieText);
        g.drawString(hanJieText, MARGIN + CELL_SIZE * 6 - hanJieWidth / 2, riverY + 8);
    }

    /**
     * 绘制棋子
     */
    private void drawPieces(Graphics2D g) {
        // 遍历棋盘上的每一个棋子，每次循环绘制该棋子
        for (AbstractPiece piece : model.getPieces()) {
            // 计算每一个棋子的坐标
            int x = MARGIN + piece.getCol() * CELL_SIZE;
            int y = MARGIN + piece.getRow() * CELL_SIZE;

            boolean isSelected = (piece == selectedPiece);

            // 先绘制circle
            g.setColor(new Color(245, 222, 179));
            g.fillOval(x - PIECE_RADIUS, y - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);
            // 绘制circle的黑色边框
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawOval(x - PIECE_RADIUS, y - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);

            if (isSelected) {
                drawCornerBorders(g, x, y);
            }

            // 再在circle上面绘制对应的棋子名字
            if (piece.isRed()) {
                g.setColor(new Color(200, 0, 0));
            } else {
                g.setColor(Color.BLACK);
            }
            g.setFont(new Font("楷体", Font.BOLD, 22));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(piece.getName());
            int textHeight = fm.getAscent();
            g.drawString(piece.getName(), x - textWidth / 2, y + textHeight / 2 - 2);
        }
    }

    /**
     * 绘制选中棋子时的蓝色外边框效果
     */
    private void drawCornerBorders(Graphics2D g, int centerX, int centerY) {
        g.setColor(new Color(0, 100, 255));
        g.setStroke(new BasicStroke(3));

        int cornerSize = 32;
        int lineLength = 12;

        // 选中效果的边框实际上是8条line，每两个line组成一个角落的边框

        // 左上角的边框
        g.drawLine(centerX - cornerSize, centerY - cornerSize,
                centerX - cornerSize + lineLength, centerY - cornerSize);
        g.drawLine(centerX - cornerSize, centerY - cornerSize,
                centerX - cornerSize, centerY - cornerSize + lineLength);

        // 右上角的边框
        g.drawLine(centerX + cornerSize, centerY - cornerSize,
                centerX + cornerSize - lineLength, centerY - cornerSize);
        g.drawLine(centerX + cornerSize, centerY - cornerSize,
                centerX + cornerSize, centerY - cornerSize + lineLength);

        // 左下角的边框
        g.drawLine(centerX - cornerSize, centerY + cornerSize,
                centerX - cornerSize + lineLength, centerY + cornerSize);
        g.drawLine(centerX - cornerSize, centerY + cornerSize,
                centerX - cornerSize, centerY + cornerSize - lineLength);

        // 右下角的边框
        g.drawLine(centerX + cornerSize, centerY + cornerSize,
                centerX + cornerSize - lineLength, centerY + cornerSize);
        g.drawLine(centerX + cornerSize, centerY + cornerSize,
                centerX + cornerSize, centerY + cornerSize - lineLength);
    }

    public void setNewGameModel(ChessBoardModel newModel, CurrentCamp newCamp) {//重新为棋盘赋值（重新开始游戏）
        this.model = newModel;
        this.currentCamp = newCamp;
        this.selectedPiece = null; // Clear any selected piece
        this.repaint();
    }

    //全局的文字 method
    public void setStatusMessage(String statusMessage, Color color,boolean isPermanent) {
        statusLabel.setText(statusMessage);
        statusLabel.setForeground(color);
        statusLabel.setVisible(true);

        if (!isPermanent) {
        Timer timer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // After the message fades, restore the turn display
                if (interactionEnabled) {
                    updateTurnLabel();
                } else {
                    statusLabel.setVisible(false);
                }
                ((Timer) e.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
        }
    }

    public boolean getInteractionEnabled() {
        return interactionEnabled;
    }

    private void calculateLegalMoves(AbstractPiece piece) {
        System.out.println("进入calculateLegalMoves方法");
        legalMoves.clear(); // 清空旧位置

        if (piece == null) {
            System.out.println("选中棋子为null，退出");
            return;
        }
        // 遍历棋盘所有位置，检查是否合法
        for (int row = 0; row < ChessBoardModel.getRows(); row++) {
            for (int col = 0; col < ChessBoardModel.getCols(); col++) {
                AbstractPiece target = model.getPieceAt(row, col);
                if (target != null && target.isRed() == piece.isRed()) {
                    continue; // Skip this position (cannot eat own piece)
                }
                if (piece.canMoveTo(row, col, model)) {
                    legalMoves.add(new Point(row, col)); // 保存合法位置
                }
            }
        }
        System.out.println("合法位置数量：" + legalMoves.size());
    }

    private void drawLegalMoves(Graphics g) {
        // 未选中棋子或无合法位置，直接返回
        if (selectedPiece == null || legalMoves.isEmpty()) {
            return;
        }

        // 设置红圈样式：红色、细边框（避免太粗）
        g.setColor(new Color(255, 60, 60));
        ((Graphics2D) g).setStroke(new BasicStroke(2)); // 细一点的线

        // 遍历合法位置绘制红圈
        for (Point p : legalMoves) {
            int row = p.x; // 行坐标（对应y轴）
            int col = p.y; // 列坐标（对应x轴）

            // 计算红圈中心点坐标（格子的正中心）
            int centerX = MARGIN + col * CELL_SIZE;//change
            int centerY = MARGIN + row * CELL_SIZE;//change

            // 绘制红圈：半径为格子的1/4，避免太大
            int radius = PIECE_RADIUS / 3;
            g.fillOval(
                    centerX - radius,  // 左上角x
                    centerY - radius,  // 左上角y
                    radius * 2,        // 宽
                    radius * 2         // 高
            );
        }
    }
}
