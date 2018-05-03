package edu.utep.cs.cs3331.GUI.dialog;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

import java.net.Socket;
import java.net.URL;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.text.DefaultCaret;

import edu.utep.cs.cs3331.sudoku.Board;
import edu.utep.cs.cs3331.Network.NetworkAdapter;

/**
 * A dialog template for playing simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(int), numberClicked(int) and boardClicked(int,int).
 *
 * @author Yoonsik Cheon
 */
@SuppressWarnings("serial")
public class SudokuDialog extends JFrame {

    private Socket socket;



    //Network Items--------------
    /** Default dimension of the dialog. */
    private final static Dimension DEFAULT_SIZE = new Dimension(310, 500);

    private final static String IMAGE_DIR = "/image/";

    /** Sudoku board. */
    private Board board;

    public static int inputVal;
    public static int max;

    /** Special panel to display a Sudoku board. */
    private BoardPanel boardPanel;

    /** Message bar to display various messages. */
    private JLabel msgBar = new JLabel("");

    /** Buttons for the ToolBar. */
    JButton newGame, endGame, solveable, solve, partial, netWork;

    /** Menu items for dropdown menu. */
    JMenuItem menuItem, menuItem2, menuItem3;

    /** Buttons for bonus problems. */
    JButton undo, redo, display;

    /** Create a new dialog. */
    public SudokuDialog() {
        this(DEFAULT_SIZE);
    }

    /** Create a new dialog of the given screen dimension. */
    public SudokuDialog(Dimension dim) {
        super("Sudoku");
        int clientNum = 0;
        setSize(dim);
        board = new Board(9);
        boardPanel = new BoardPanel(board, this::boardClicked);
        configureUI();
        //setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        //setResizable(false);
    }

    private void checkifSolve() {
        if(board.isSolved()) {//.check()
            SolveableMsg();
        }else {
            NotSolveableMsg();
        }
    }

    /**
     * Callback to be invoked when a square of the board is clicked.
     * @param x 0-based row index of the clicked square.
     * @param y 0-based column index of the clicked square.
     */
    private void boardClicked(int x, int y) {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("Position changed at X:"+ x+ " Y:"+y);
            disPos("Position changed at X:"+ x+ " Y:"+y);
            out.flush();
            board.insert(x,y,inputVal);
            showMessage(String.format("Board clicked: x = %d, y = %d",  x, y));
            if(board.isSolved()) {
                checkTrue();
            }
            repaint();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    /**
     * Callback to be invoked when a number button is clicked.
     * @param number Clicked number (1-9), or 0 for "X".
     */
    private void numberClicked(int number) {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            showMessage("Number clicked: " + number);
            if( number > board.size || number <0){
                showMessage("INVALID NUMBER FOR BOARD SIZE: "+ number);

            }if(board.isSolved()) {
                checkTrue();
            }

            else{
                inputVal = number;
                out.println("Placed number "+number);
                dis(number);
                out.flush();
            }


            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback to be invoked when a new button is clicked.
     * If the current game is over, start a new game of the given size;
     * otherwise, prompt the user for a confirmation and then proceed
     * accordingly.
     * @param size Requested puzzle size, either 4 or 9.
     */
    private void newClicked(int size) {
        max = size;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.println("Requesting New Game of size "+size);
            disSize(size);
            out.flush();
            int j = JOptionPane.showConfirmDialog(null, "Would you like to start a new game?");

            if(j==0 ) {
                board.size=size;
                //board=new Board(size);
                int[][] newTable = board.getTable();
                for(int x =0; x<board.size;x++) {
                    for(int y=0; y<board.size; y++) {
                        newTable[x][y]=0;

                    }
                }
                repaint();
                //makeControlPanel();
            }

            showMessage("New clicked: " + size);
            repaint();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    private final static String newline = "\n";
    private void dis(int msg){

        msgDisplay.append( "Player :"+" placed "+msg + newline);
        serverEdit.selectAll();

    }
    private void disPos(String msg){

        msgDisplay.append( msg + newline);
        serverEdit.selectAll();

    }
    private void disSize(int msg){

        msgDisplay.append( "Player :"+" new game of size "+msg + newline);
        serverEdit.selectAll();

    }
    /**
     * Display the given string in the message bar.
     * @param msg Message to be displayed.
     */
    private void showMessage(String msg) {
        msgBar.setText(msg);
    }
    /** Configure UI of this dialog. */
    private void configureGui() {
        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        //connectButton = new JButton("Connect");
        // connectButton.setFocusPainted(false);
        serverEdit = new JTextField("localhost", 18);
        portEdit = new JTextField("8000", 4);

        // connectPanel.add(connectButton);
        connectPanel.add(serverEdit);
        connectPanel.add(portEdit);

        msgDisplay = new JTextArea(10, 30);
        msgDisplay.setEditable(false);
        DefaultCaret caret = (DefaultCaret)msgDisplay.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // autoscroll
        JScrollPane msgScrollPane = new JScrollPane(msgDisplay);

        JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        msgEdit = new JTextField("Enter a message.", 27);
        sendButton = new JButton("Send");
        sendButton.setFocusPainted(true);
        sendPanel.add(msgEdit);
        sendPanel.add(sendButton);

        setLayout(new BorderLayout());
        add(connectPanel, BorderLayout.EAST);
        add(msgScrollPane, BorderLayout.CENTER);
        add(sendPanel, BorderLayout.EAST);
    }
    /** Configure the UI. */
    private void configureUI() {
        setIconImage(createImageIcon("sudoku.png").getImage());
        setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());

        JPanel North = new JPanel();
        North.setLayout(new BorderLayout());

        JPanel menu = createMenu();
        menu.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        add(menu, BorderLayout.NORTH);

        JPanel toolBar = createToolBar();
        toolBar.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        North.add(toolBar, BorderLayout.NORTH);

        JPanel buttons = makeControlPanel();
        // boarder: top, left, bottom, right
        buttons.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        center.add(buttons, BorderLayout.NORTH);

        JPanel board = new JPanel();
        board.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
        board.setLayout(new GridLayout(1,1));
        board.add(boardPanel);
        center.add(board, BorderLayout.CENTER);

        msgBar.setBorder(BorderFactory.createEmptyBorder(10,16,10,0));
        center.add(msgBar, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
        add(North, BorderLayout.NORTH);

        setupListeners();
    }

    /** Create a control panel consisting of new and number buttons. */
    private JPanel makeControlPanel() {
        JPanel newButtons = new JPanel(new FlowLayout());
        JButton new4Button = new JButton("New (4x4)");
        for (JButton button: new JButton[] { new4Button, new JButton("New (9x9)") }) {
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                newClicked(e.getSource() == new4Button ? 4 : 9);
            });
            newButtons.add(button);
        }
        newButtons.setAlignmentX(LEFT_ALIGNMENT);

        /** buttons labeled 1, 2, ..., 9, and X.
         *
         */
        JPanel numberButtons = new JPanel(new FlowLayout());
        int maxNumber = board.size() + 1;
        for (int i = 1; i <= maxNumber; i++) {
            int number = i % maxNumber;
            JButton button = new JButton(number == 0 ? "X" : String.valueOf(number));
            button.setFocusPainted(false);
            button.setMargin(new Insets(0,2,0,2));
            button.addActionListener(e -> numberClicked(number));
            numberButtons.add(button);
        }
        numberButtons.setAlignmentX(LEFT_ALIGNMENT);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(newButtons);
        content.add(numberButtons);
        return content;
    }



    /** Create toolbar consisting of new game, end game, and solveable buttons. */
    private JPanel createToolBar() {
        JPanel panel = new JPanel();
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        panel.add(toolBar, BorderLayout.PAGE_START);

        newGame = new JButton(createImageIcon("newgame.png"));
        newGame.setToolTipText("Play new game");
        toolBar.add(newGame);

        endGame = new JButton(createImageIcon("exit.png"));
        endGame.setToolTipText("End current game");
        toolBar.add(endGame);

        solveable = new JButton(createImageIcon("solveable.png"));
        solveable.setToolTipText("Checks if current board is solveable");
        toolBar.add(solveable);

        solve = new JButton(createImageIcon("solve.png"));
        solve.setToolTipText("solve");
        toolBar.add(solve);

        partial = new JButton(createImageIcon("partial.png"));
        partial.setToolTipText("Partially fill board");
        toolBar.add(partial);

        netWork = new JButton(createImageIcon("online.png"));
        netWork.setToolTipText("Would you like to Network Play");
        toolBar.add(netWork);


        return panel;
    }



    private JPanel createMenu() {
        JPanel menus = new JPanel();
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenu newgame = new JMenu("New Game");

        menu.setMnemonic(KeyEvent.VK_G);
        menu.getAccessibleContext().setAccessibleDescription("MENU");
        menuBar.add(menu);
        menuBar.add(newgame);


        JMenuItem menuItem = new JMenuItem("4x4");
        newgame.add(menuItem);

        JMenuItem menuItem0 = new JMenuItem("9x9");
        newgame.add(menuItem0);

        JMenuItem menuItem1 = new JMenuItem("Partial Fill", KeyEvent.VK_N);
        menuItem1.setIcon(createImageIcon("newGame.png"));

        JMenuItem menuItem2 = new JMenuItem("Is it solvable?", KeyEvent.VK_N);
        menuItem2.setIcon(createImageIcon("newGame.png"));

        JMenuItem menuItem3 = new JMenuItem("Solve", KeyEvent.VK_N);
        menuItem3.setIcon(createImageIcon("newGame.png"));


        newgame.add(menuItem);
        newgame.add(menuItem0);
        menu.add(menuItem1);
        menu.add(menuItem2);
        menu.add(menuItem3);



        menuItem.addActionListener(e ->  newClicked(4));
        menuItem0.addActionListener(e ->  newClicked(9));
        menuItem1.addActionListener(e -> partiallyFillBoard() );
        menuItem2.addActionListener(e -> checkifSolve());
        menuItem3.addActionListener(e -> solveMenuItem());

        setJMenuBar(menuBar);
        setVisible(true);
        return menus;
    }
    private void solveMenuItem() {
        board.solve();
        repaint();
    }

    private void partiallyFillBoard() {
        board.partialFill();
        repaint();
    }


    private void setupListeners() {
        endGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent click) {
                int input = JOptionPane.showConfirmDialog(null, "Do you want to close game?", "End Game?", JOptionPane.YES_NO_OPTION);
                if (input == JOptionPane.YES_OPTION) {
                    System.exit(1);
                }
            }
        });

        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent click) {
                int input = JOptionPane.showConfirmDialog(null, "Start a new game?", "New Game?", JOptionPane.YES_NO_OPTION);
                if (input == JOptionPane.YES_OPTION) {
                    board = new Board(board.size);
                    boardPanel.setBoard(board);
                    boardPanel.repaint();
                }
            }
        });

        solveable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent click) {
                int input = JOptionPane.showConfirmDialog(null, "Do you want to check if the board is solveable?", "Solveable", JOptionPane.YES_NO_OPTION);
                if (input == JOptionPane.YES_OPTION) {
                    solve.addActionListener(e -> checkifSolve());
                }
            }
        });


        solve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent click) {
                int input = JOptionPane.showConfirmDialog(null, "Do you want to solve your board?", "Solved", JOptionPane.YES_NO_OPTION);
                if (input == JOptionPane.YES_OPTION) {
                    //solve.addActionListener(e -> board.solve());
                    board.solve();
                    repaint();
                }
            }
        });

        partial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent click) {
                int input = JOptionPane.showConfirmDialog(null, "Do you want to partially fill your board?","done" , JOptionPane.YES_NO_OPTION);
                if (input == JOptionPane.YES_OPTION) {
                    board.partialFill();
                    repaint();
                }
            }
        });

        netWork.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent click) {
                configureGui();

                try {

                    socket = new Socket(serverEdit.getText(), 8008);
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    Socket s = new Socket("localhost", 8004);
                    sendButton.setFocusPainted(true);

                } catch (IOException e) {
                    // handle exception …

                }

            }

        });

    }
    private JTextField serverEdit;
    private JButton sendButton;
    private JTextArea msgDisplay;
    private JTextField msgEdit;

    private JTextField portEdit;






    /** Create an image icon from the given image file. */
    private ImageIcon createImageIcon(String filename) {
        URL imageUrl = getClass().getResource(IMAGE_DIR + filename);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        return null;
    }
    private void log(String message){
        System.out.println(message);
    }
    public static void main(String[] args) {
        new SudokuDialog();
    }

    public void checkTrue(){
        showMessage("SOLVED");
    }
    public void SolveableMsg() {
        showMessage("Solveable");
    }
    public void NotSolveableMsg() {
        showMessage("Not Solvable");
    }

}