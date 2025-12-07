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




public class ChessBoardPanel extends JPanel {
    private ChessBoardModel model;
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

    private MoveEveryStep lastMove = null;

    private boolean isGameOver=false;

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

        addMouseListener(new MouseAdapter() {//鼠标点击获取坐标的核心逻辑
            @Override
            public void mouseClicked(MouseEvent e) {//获取MouseEvent对象的坐标
                handleMouseClick(e.getX(), e.getY());//执行相应的方法
            }
        });
    }

    //  修改：updateTurnLabel 方法，用于在切换回合时更新提示文字
    public void updateTurnLabel() {
        if (!interactionEnabled) return; // 如果游戏还没开始，不更新回合文字

        if (currentCamp.isRedTurn()) {

            gameFrame.updateStatusMessage("当前回合：红方", Color.RED, true);
        } else {

            gameFrame.updateStatusMessage("当前回合：黑方", Color.BLACK, true);
        }
    }
    public ChessBoardModel model() {
        return model;
    }

    //=====================================================================
    public void setGameInteractionEnabled(boolean enabled) {

        String bgmPath="src/main/resources/Audio/" + "斗地主.wav";
        if (isGameOver) {
            return;
        }
        if (enabled) {
            // 启动循环音效（单例防止叠加）
            AudioPlayer.playLoopingSound(bgmPath);
        } else {
        }

        this.interactionEnabled = enabled; // 使用 this

        if (interactionEnabled) {
            // 游戏开始
            updateTurnLabel();   // 显示 "当前回合：红方"
            gameFrame.startGameTimer();
        } else {
            gameFrame.stopGameTimer();
            // 禁用时
            if (this.model.getMoveHistory().isEmpty()) {
                gameFrame.updateStatusMessage("请点击开始", Color.BLUE, true);
        }else {
                return;
            }
        }
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

        int eatintCount = 0;
        int isInCheckCount = 0;
        int mustDie = 0;

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
                        gameFrame.updateStatusMessage("请重新选择！", Color.BLUE, false);
                        selectedPiece = null;
                        legalMoves.clear();
                        repaint();
                        return; // ABORT move
                    }

                    MoveEveryStep move = new MoveEveryStep(selectedPiece, row, col, target, this.currentCamp);
                    eatintCount++;
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
                        gameFrame.updateStatusMessage("请重新选择！", Color.BLUE, false);
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
                        isInCheckCount++;
                        gameFrame.updateStatusMessage("红方将黑方", Color.RED, true);
                        messageShown = true;
                    }
                } else {
                    isCheck = model.isInCheck(true); // 黑方走后检测红方
                    if (isCheck) {
                        isInCheckCount++;
                        gameFrame.updateStatusMessage("黑方将红方", Color.BLACK, true);
                        messageShown = true;
                    }
                }
                currentCamp.nextTurn(); // 切换红黑
                legalMoves.clear();
                selectedPiece = null;

                gameFrame.refreshLastMoveVisuals();

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
        //haslegalmoves is to the piece which the currentcamp do not move a piece but it actually has no movement(simulate the move but actually do not happen!)
        //isincheck(is the situation now the camp face to!) is that the currnet camp in last step of the conpent is going to be eaten the general, then it start to find if
        // it has legal moves to break the situation
        if (!hasMoves) {
            String message;
            Color color = Color.BLUE;
            if (inCheck) {
                // Checkmate: The previous player wins
                isGameOver = true;
                //AudioPlayer.stopAllLoopingSounds();
                setGameInteractionEnabled(false);
                String winner = currentCamp.isRedTurn() ? "黑方" : "红方";
                String loser = currentCamp.isRedTurn() ? "红方" : "黑方";
                message = winner + "将死" + loser;
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
                isGameOver = true;
                //AudioPlayer.stopAllLoopingSounds();
                setGameInteractionEnabled(false);

                String winner = currentCamp.isRedTurn() ? "黑方" : "红方";
                String loser = currentCamp.isRedTurn() ? "红方" : "黑方";
                message ="困毙：" +  winner + "胜利";
                if (currentCamp.isRedTurn()) {
                    gameFrame.addBlackCampScore();
                    gameFrame.updateScoreLabel();
                    repaint();
                } else  {
                    gameFrame.addRedCampScore();
                    gameFrame.updateScoreLabel();
                    repaint();
                }
            }

            // Show persistent message (no timer to fade it)
            mustDie++;
            gameFrame.updateStatusMessage(message, Color.RED, true);
            gameFrame.hideGiveUpOption(); // 确保认输按钮隐藏
            gameFrame.getEndUpPeaceButton().setEnabled(false);
            this.setGameInteractionEnabled(false);

        } else if (inCheck) {
            Color checkColor = currentCamp.isRedTurn() ? Color.RED : Color.BLACK;
            gameFrame.updateStatusMessage(currentCampName + "被将军！", Color.RED, true);
            gameFrame.showGiveUpOption(currentCampName);
        } else {
            updateTurnLabel(); // 更新回合文字
            gameFrame.hideGiveUpOption(); // 确保认输按钮隐藏
        }

        if (eatintCount == 1 && mustDie == 0 && isInCheckCount == 0) {
            AudioPlayer.playSound("src/main/resources/Audio/吃.wav");
        }
        if (eatintCount == 1 && mustDie == 0 && isInCheckCount == 1) {
            AudioPlayer.playSound("src/main/resources/Audio/将军.wav");
        }
        if (eatintCount == 1 && mustDie == 1 && isInCheckCount == 1) {
            AudioPlayer.playSound("src/main/resources/Audio/绝杀.wav");
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
        drawLastMoveHighlights(g2d);
        drawPieces(g2d);
        drawLegalMoves(g);//new bulid
    }

    /**
     * 绘制棋盘
     */
    private void drawBoard(Graphics2D g) {
        g.setColor(Color.BLACK);
        // 使用较粗的线条绘制主棋盘线
        g.setStroke(new BasicStroke(3));

        // --- L 形标记常量 ---
        final int MARK_LENGTH = 12; // L 形标记单边长度
        final int MARK_GAP = 8;     // **L 形标记内角与棋盘交点的间隙增大 (原 MARK_OFFSET = 3)**

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

        //画两个x线用于将和帅 (保持不变)
        g.drawLine(MARGIN  + 3 * CELL_SIZE, MARGIN, MARGIN + 5 * CELL_SIZE, MARGIN + 2 * CELL_SIZE);
        g.drawLine(MARGIN  + 5 * CELL_SIZE, MARGIN, MARGIN + 3 * CELL_SIZE, MARGIN + 2 * CELL_SIZE);
        g.drawLine(MARGIN  + 3 * CELL_SIZE, MARGIN + 9 * CELL_SIZE, MARGIN + 5 * CELL_SIZE, MARGIN + 7 * CELL_SIZE);
        g.drawLine(MARGIN  + 3 * CELL_SIZE, MARGIN + 7 * CELL_SIZE, MARGIN + 5 * CELL_SIZE, MARGIN + 9 * CELL_SIZE);


        // --- 绘制炮位和兵卒位的 L 形标记 ---
        g.setStroke(new BasicStroke(3));

        // 标记点 (行, 列)
        int[][] marks = {
                // 炮位：(2, 1), (2, 7), (7, 1), (7, 7)
                {2, 1}, {2, 7}, {7, 1}, {7, 7},
                // 兵卒位：(3, 0), (3, 2), (3, 4), (3, 6), (3, 8)
                {3, 0}, {3, 2}, {3, 4}, {3, 6}, {3, 8},
                // 兵卒位：(6, 0), (6, 2), (6, 4), (6, 6), (6, 8)
                {6, 0}, {6, 2}, {6, 4}, {6, 6}, {6, 8}
        };

        for (int[] mark : marks) {
            int r = mark[0];
            int c = mark[1];
            int x = MARGIN + c * CELL_SIZE; // 棋盘交点 X 坐标
            int y = MARGIN + r * CELL_SIZE; // 棋盘交点 Y 坐标

            // 特殊处理：0列和8列的兵卒标记只有内侧两个L
            boolean isEdge = (c == 0 || c == ChessBoardModel.getCols() - 1);

            // 绘制 L 形标记的四个角（四个 90° 角）

            // 1. 左上角 L
            if (!isEdge || c != 0) {
                // 绘制的内角点位于 (x - MARK_GAP, y - MARK_GAP) 处
                // 横线 (向左延伸)
                g.drawLine(x - MARK_GAP, y - MARK_GAP, x - MARK_GAP - MARK_LENGTH, y - MARK_GAP);
                // 竖线 (向上延伸)
                g.drawLine(x - MARK_GAP, y - MARK_GAP, x - MARK_GAP, y - MARK_GAP - MARK_LENGTH);
            }

            // 2. 右上角 L
            if (!isEdge || c != ChessBoardModel.getCols() - 1) {
                // 绘制的内角点位于 (x + MARK_GAP, y - MARK_GAP) 处
                // 横线 (向右延伸)
                g.drawLine(x + MARK_GAP, y - MARK_GAP, x + MARK_GAP + MARK_LENGTH, y - MARK_GAP);
                // 竖线 (向上延伸)
                g.drawLine(x + MARK_GAP, y - MARK_GAP, x + MARK_GAP, y - MARK_GAP - MARK_LENGTH);
            }

            // 3. 左下角 L
            if (!isEdge || c != 0) {
                // 绘制的内角点位于 (x - MARK_GAP, y + MARK_GAP) 处
                // 横线 (向左延伸)
                g.drawLine(x - MARK_GAP, y + MARK_GAP, x - MARK_GAP - MARK_LENGTH, y + MARK_GAP);
                // 竖线 (向下延伸)
                g.drawLine(x - MARK_GAP, y + MARK_GAP, x - MARK_GAP, y + MARK_GAP + MARK_LENGTH);
            }

            // 4. 右下角 L
            if (!isEdge || c != ChessBoardModel.getCols() - 1) {
                // 绘制的内角点位于 (x + MARK_GAP, y + MARK_GAP) 处
                // 横线 (向右延伸)
                g.drawLine(x + MARK_GAP, y + MARK_GAP, x + MARK_GAP + MARK_LENGTH, y + MARK_GAP);
                // 竖线 (向下延伸)
                g.drawLine(x + MARK_GAP, y + MARK_GAP, x + MARK_GAP, y + MARK_GAP + MARK_LENGTH);
            }
        }

        // --- 绘制“楚河”和“汉界”这两个文字 (保持不变) ---
        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体", Font.BOLD, 30));

        int riverY = MARGIN + 4 * CELL_SIZE + CELL_SIZE / 2;

        String chuHeText = "楚河";
        FontMetrics fm = g.getFontMetrics();
        int chuHeWidth = fm.stringWidth(chuHeText);
        g.drawString(chuHeText, MARGIN + CELL_SIZE * 2 - chuHeWidth / 2, riverY + 10);

        String hanJieText = "汉界";
        int hanJieWidth = fm.stringWidth(hanJieText);
        g.drawString(hanJieText, MARGIN + CELL_SIZE * 6 - hanJieWidth / 2, riverY + 10);
    }

    /**
     * 绘制棋子
     */
    private void drawPieces(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
            g.setStroke(new BasicStroke(3));
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
            g.setFont(new Font("宋体", Font.BOLD, 28));
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
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(4));

        int highlightRadius = PIECE_RADIUS + 3;
        g.drawOval(centerX - highlightRadius, centerY - highlightRadius,
                highlightRadius * 2, highlightRadius * 2);
    }

    public void setNewGameModel(ChessBoardModel newModel, CurrentCamp newCamp) {//重新为棋盘赋值（重新开始游戏）
        this.model = newModel;
        this.currentCamp = newCamp;
        this.selectedPiece = null; // Clear any selected piece
        this.repaint();
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

        Graphics2D g2d = (Graphics2D) g;
        // 确保抗锯齿开启，获得平滑边缘
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- 移动点常量保持不变 ---
        final int MOVE_DOT_RADIUS = PIECE_RADIUS / 3;
        final Color MOVE_COLOR = new Color(50, 180, 255, 220);
        final int MOVE_RING_THICKNESS = 2;

        // --- 瞄准镜常量更新：线段更突出圆环 ---
        // 将偏移量增大，让线段更突出圆环
        final int SCOPE_OFFSET = PIECE_RADIUS + 15;         // 线段起点到中心距离增大 (原为 +10)
        final int SCOPE_RING_RADIUS = PIECE_RADIUS + 9;    // 瞄准环的半径保持不变
        final int SCOPE_LINE_LENGTH = 10;
        final int SCOPE_THICKNESS = 3;
        final Color CAPTURE_COLOR = new Color(255, 50, 50);  // 鲜艳的亮红色

        // 遍历合法位置绘制标记
        for (Point p : legalMoves) {
            int row = p.x; // 行坐标（对应y轴）
            int col = p.y; // 列坐标（对应x轴）

            // 计算中心点坐标
            int centerX = MARGIN + col * CELL_SIZE;
            int centerY = MARGIN + row * CELL_SIZE;

            AbstractPiece targetPiece = model.getPieceAt(row, col);

            if (targetPiece == null) {
                // --- 目标为空地：绘制醒目的实心圆点 + 外环 (保持不变) ---

                // 1. 绘制内部填充圆
                g2d.setColor(MOVE_COLOR);
                g2d.fillOval(
                        centerX - MOVE_DOT_RADIUS,
                        centerY - MOVE_DOT_RADIUS,
                        MOVE_DOT_RADIUS * 2,
                        MOVE_DOT_RADIUS * 2
                );

                // 2. 绘制外部白色细环，增强可见度
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(MOVE_RING_THICKNESS));
                g2d.drawOval(
                        centerX - MOVE_DOT_RADIUS,
                        centerY - MOVE_DOT_RADIUS,
                        MOVE_DOT_RADIUS * 2,
                        MOVE_DOT_RADIUS * 2
                );

            } else {
                // --- 目标为敌方棋子：绘制瞄准目标图标 ---
                g2d.setColor(CAPTURE_COLOR);

                // 1. 绘制外围圆环
                g2d.setStroke(new BasicStroke(SCOPE_THICKNESS));
                g2d.drawOval(
                        centerX - SCOPE_RING_RADIUS,
                        centerY - SCOPE_RING_RADIUS,
                        SCOPE_RING_RADIUS * 2,
                        SCOPE_RING_RADIUS * 2
                );

                // 2. 绘制水平和垂直的瞄准线（现在会更突出圆环）

                // 左侧水平线
                // 起点: centerX - SCOPE_OFFSET
                // 终点: 起点 + SCOPE_LINE_LENGTH
                g2d.drawLine(centerX - SCOPE_OFFSET, centerY,
                        centerX - SCOPE_OFFSET + SCOPE_LINE_LENGTH, centerY);

                // 右侧水平线
                // 起点: centerX + SCOPE_OFFSET
                // 终点: 起点 - SCOPE_LINE_LENGTH
                g2d.drawLine(centerX + SCOPE_OFFSET, centerY,
                        centerX + SCOPE_OFFSET - SCOPE_LINE_LENGTH, centerY);

                // 上侧垂直线
                g2d.drawLine(centerX, centerY - SCOPE_OFFSET,
                        centerX, centerY - SCOPE_OFFSET + SCOPE_LINE_LENGTH);
                // 下侧垂直线
                g2d.drawLine(centerX, centerY + SCOPE_OFFSET,
                        centerX, centerY + SCOPE_OFFSET - SCOPE_LINE_LENGTH);

                // 恢复默认的细线
                g2d.setStroke(new BasicStroke(1));
            }
        }

        // 关闭抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public void setLastMove(MoveEveryStep move) {
        this.lastMove = move;
        repaint(); // Trigger a redraw
    }

    // 3. Helper method to calculate X/Y (You likely already have this logic, use yours)
    // This is just an example assuming you have 'cellSize' and 'margin'
    private int getX(int col) {
        // REPLACE THIS with your actual calculation logic
        // Example: return margin + col * cellSize;
        return MARGIN + col * CELL_SIZE;
    }

    private int getY(int row) {
        // REPLACE THIS with your actual calculation logic
        return MARGIN + row * CELL_SIZE;
    }

    private void drawLastMoveHighlights(Graphics g) {
        if (lastMove == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get move coordinates
        int startRow = lastMove.getStartRow();
        int startCol = lastMove.getStartCol();
        int endRow = lastMove.getEndRow();
        int endCol = lastMove.getEndCol();

        // The piece that was moved (used for its name and color)
        AbstractPiece movedPiece = lastMove.getMovingPiece();

        // Calculate pixel positions
        int sX = getX(startCol);
        int sY = getY(startRow);
        int eX = getX(endCol);
        int eY = getY(endRow);

        final int HIGHLIGHT_RADIUS = PIECE_RADIUS + 5;
        final int DIAMETER = HIGHLIGHT_RADIUS * 2;

        // =========================================================================
        // A. Draw PHANTOM PIECE at Start Position (Origin) - Unchanged
        // =========================================================================
        if (movedPiece != null) {
            // Set up high transparency for the phantom effect (Alpha: 0.25f)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));

            // 1. Draw Phantom Circle Background
            g2d.setColor(new Color(245, 222, 179)); // Piece background
            g2d.fillOval(sX - PIECE_RADIUS, sY - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);

            // 2. Draw Phantom Circle Border (solid dark gray, high opacity)
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawOval(sX - PIECE_RADIUS, sY - PIECE_RADIUS, PIECE_RADIUS * 2, PIECE_RADIUS * 2);

            // 3. Draw Phantom Piece Name
            g2d.setColor(movedPiece.isRed() ? new Color(200, 0, 0) : Color.BLACK);
            g2d.setFont(new Font("宋体", Font.BOLD, 28));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(movedPiece.getName());
            int textHeight = fm.getAscent();
            g2d.drawString(movedPiece.getName(), sX - textWidth / 2, sY + textHeight / 2 - 2);

            // Reset Composite/Opacity back to 1.0f for subsequent drawing
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            // 4. Draw a distinct dashed outline around the phantom piece
            float[] dashPattern = {4f, 4f};
            Stroke dashedStroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dashPattern, 0.0f);

            g2d.setStroke(dashedStroke);
            g2d.setColor(new Color(150, 150, 150)); // Medium gray
            g2d.drawOval(sX - HIGHLIGHT_RADIUS, sY - HIGHLIGHT_RADIUS, DIAMETER, DIAMETER);
        }


        // =========================================================================
        // B. Draw End Position (Striking BLUE Highlight)
        // =========================================================================

        // Define Blue Colors
        final Color BLUE_INNER = new Color(50, 150, 255, 150); // Bright Blue center
        final Color BLUE_OUTER = new Color(50, 150, 255, 0);   // Transparent edge
        final Color BLUE_RING = new Color(50, 150, 255, 255); // Solid Blue ring

        // 1. Draw Radial Gradient (Blue)
        g2d.setStroke(new BasicStroke(1.0f));

        RadialGradientPaint gradient = new RadialGradientPaint(
                eX, eY, HIGHLIGHT_RADIUS,
                new float[]{0.0f, 1.0f},
                new Color[]{BLUE_INNER, BLUE_OUTER} // Use new blue colors
        );

        g2d.setPaint(gradient);
        g2d.fillOval(eX - HIGHLIGHT_RADIUS, eY - HIGHLIGHT_RADIUS, DIAMETER, DIAMETER);

        // 2. Add a solid striking ring (Blue)
        g2d.setColor(BLUE_RING);
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawOval(eX - HIGHLIGHT_RADIUS, eY - HIGHLIGHT_RADIUS, DIAMETER, DIAMETER);
    }
}

