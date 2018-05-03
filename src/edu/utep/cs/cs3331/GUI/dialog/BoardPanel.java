package edu.utep.cs.cs3331.GUI.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import edu.utep.cs.cs3331.Network.NetworkAdapter;
import edu.utep.cs.cs3331.sudoku.Board;

/**
 * A special panel class to display a Sudoku board modeled by the
 * {@link sudoku.model.Board} class. You need to write code for
 * the paint() method.
 *
 * @see sudoku.model.Board
 * @author Yoonsik Cheon
 *
 * @author LUISA POLANCO & ANDREA LABRADO & Brian Riveron:)
 *
 *
 *
 *
 *
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

    public interface ClickListener {
        /** Callback to notify clicking of a square.
         *
         * @param x 0-based column index of the clicked square
         * @param y 0-based row index of the clicked square
         */
        void clicked(int x, int y);
    }

    /** Background color of the board. */
    private static final Color boardColor = new Color(200, 50, 150);

    /** Board to be displayed. */
    private Board board;

    /** Width and height of a square in pixels. */
    private int squareSize;

    /** Create a new board panel to display the given board. */
    public BoardPanel(Board board, ClickListener listener) {
        this.board = board;
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int xy = locateSquaree(e.getX(), e.getY());
                if (xy >= 0) {
                    listener.clicked(xy / 100, xy % 100);
                }
            }
        });
    }

    /** Set the board to be displayed. */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Given a screen coordinate, return the indexes of the corresponding square
     * or -1 if there is no square.
     * The indexes are encoded and returned as x*100 + y,
     * where x and y are 0-based column/row indexes.
     */
    private int locateSquaree(int x, int y) {
        if (x < 0 || x > board.size * squareSize
                || y < 0 || y > board.size * squareSize) {
            return -1;
        }
        int xx = x / squareSize;
        int yy = y / squareSize;
        return xx * 100 + yy;
    }

    /** Draw the associated board.
     * @param Graphics g
     *
     *  */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // determine the square size
        Dimension dim = getSize();
        squareSize = Math.min(dim.width, dim.height) / board.size;

        // draw background

        g.setColor(boardColor);
        g.fillRect(0, 0, squareSize * board.size, squareSize * board.size);

        int height =dim.height-10;
        int width =dim.width-10;
        g.setColor(Color.BLACK);
        for (int i=0; i<=9; ++i) {
            g.drawLine(0, height*i/board.size, width, height*i/board.size);  // horizontal
            g.drawLine(width*i/board.size, 0, width*i/board.size, height);
            if (i%Math.sqrt(board.size)==0) {
                g.drawLine(0, height*i/board.size+1, width, height*i/board.size+1);
                g.drawLine(0, height*i/board.size+2, width, height*i/board.size+2);
                g.drawLine(width*i/board.size+1, 0, width*i/board.size+1, height);
                g.drawLine(width*i/board.size+2, 0, width*i/board.size+2, height);
            }

        }

        for(int x=0; x<board.size; x++) {
            for(int y=0; y<board.size; y++) {
                int[][] clone = board.getTable();
                if(clone[x][y]!=0) {
                    g.drawString(clone[x][y] + "", x * height / board.size + 10, y * height / board.size + 20);
                }
            }
        }





    }}