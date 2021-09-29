import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import static javax.swing.BorderFactory.createTitledBorder;

/**
 * A board view represents the view of the board-model. It extends JPanel and implements an observer and
 * action listener. In addition it consists of JComponents, Icons and two final integers.
 * A board view takes a board-model and an othello-aClient as its parameters.
 */
class BoardView extends JPanel implements Observer, ActionListener
{
    //Dimensions
    private final int cols = 8;
    private final int rows = 8;

    //GUI
    private final JButton[][] board;
    private final BoardModel aBoardModel;
    private final OthelloClient aClient;

    //Game status
    private JLabel yourColor;
    public static JTextArea movesMade;

    private static JLabel whiteCount;
    private static JLabel blackCount;
    private static JLabel turn;
    private static JLabel result;

    //Chat
    private static JTextField textField;
    private static JTextArea textArea;
    public static JLabel netStatus;


    //localColor
    private ColorSt localColor;

    //Icons
    private final Icon iBlack = new ImageIcon(getClass().getResource("Images/black.png"));
    private final Icon iWhite = new ImageIcon(getClass().getResource("Images/white.png"));
    private final Icon iBackGround = new ImageIcon(getClass().getResource("Images/background.png"));


    public BoardView(BoardModel model, OthelloClient client)
    {
        aClient = client;
        aBoardModel = model;
        aBoardModel.addObserver(this);

        this.setBorder(createTitledBorder(new EtchedBorder(EtchedBorder.LOWERED)));

        setLayout(new GridLayout(rows, cols));
        board = new JButton[rows][cols];

        for (int y = 0; y < cols; y++)
        {
            for (int x = 0; x < rows; x++)
            {
                board[x][y] = new JButton(x + " " + y, iBackGround);
                board[x][y].addActionListener(this);
                board[x][y].setPreferredSize(new Dimension(70, 60));
                add(board[x][y]);
            }
        }
    }

    /**
     * Sets the chat and labels in the south-area of a specified JFrame.
     *
     * @param aFrame the specified frame.
     */
    public void addChatAndLabels(JFrame aFrame)
    {
        JPanel southPane = new JPanel(new BorderLayout());
        aFrame.add(southPane, BorderLayout.SOUTH);

        chatArea(southPane);
    }

    /**
     * Sets the game info in the east-area of a specified JFrame.
     *
     * @param aFrame the specified frame.
     */
    public void addGameInfo(JFrame aFrame)
    {
        JPanel eastPane = new JPanel(new BorderLayout());
        aFrame.add(eastPane, BorderLayout.EAST);

        movesMade(eastPane);
    }

    /**
     * Sets and shows the current moves that is made on a specified JPanel.
     *
     * @param aPanel the panel that holds the information
     */
    void movesMade(JPanel aPanel)
    {

        JPanel infoPane = new JPanel(new BorderLayout());
        aPanel.add(infoPane, BorderLayout.CENTER);

        movesMade = new JTextArea(15, 10);
        movesMade.setEditable(false);
        movesMade.setLineWrap(true);

        DefaultCaret caret = (DefaultCaret) movesMade.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(movesMade);

        TitledBorder titleMoves = createTitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "Moves Made");
        titleMoves.setTitleJustification(TitledBorder.LEFT);
        scrollPane.setBorder(titleMoves);

        infoPane.add(scrollPane, BorderLayout.CENTER);

        JPanel statePane = new JPanel();
        statePane.setLayout(new BoxLayout(statePane, BoxLayout.PAGE_AXIS));
        aPanel.add(statePane, BorderLayout.NORTH);

        TitledBorder titleCount = createTitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "Game State");
        titleCount.setTitleJustification(TitledBorder.LEFT);
        statePane.setBorder(titleCount);

        yourColor = new JLabel("You are: ");
        whiteCount = new JLabel("White Pieces: ");
        blackCount = new JLabel("Black Pieces: ");
        turn = new JLabel("Turn: ");
        result = new JLabel("Result: ");

        statePane.add(yourColor);
        statePane.add(turn);
        statePane.add(Box.createRigidArea(new Dimension(0, 10)));
        statePane.add(whiteCount);
        statePane.add(blackCount);
        statePane.add(Box.createRigidArea(new Dimension(0, 10)));
        statePane.add(result);

    }

    /**
     * Sets and shows the current moves that is made on a specified JPanel.
     *
     * @param aPanel the panel that holds the chatGUI
     */
    void chatArea(JPanel aPanel)
    {

        JPanel chatPane = new JPanel(new BorderLayout());
        aPanel.add(chatPane, BorderLayout.NORTH);

        TitledBorder title = createTitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "Chat");
        title.setTitleJustification(TitledBorder.LEFT);
        chatPane.setBorder(title);

        textField = new JTextField(40);
        textArea = new JTextArea(5, 40);
        movesMade.setLineWrap(true);
        textArea.setAutoscrolls(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);

        netStatus = new JLabel("Not connected...");

        chatPane.add(scrollPane, BorderLayout.CENTER);
        chatPane.add(textField, BorderLayout.NORTH);
        chatPane.add(netStatus, BorderLayout.SOUTH);

        textField.addActionListener(this);
    }

    /**
     * Shows your message in the specified text-field.
     *
     * @param aMsg the message you want to be showed
     */
    private static void yourMsg(String aMsg)
    {
        // Clear out text input field
        textField.setText("");

        // Writes the message to the window
        textArea.append("You: " + aMsg + "\n");
    }

    /**
     * Shows a message in the specified text-field.
     *
     * @param aMsg the message you want to be showed
     */
    public static void aMsg(String aMsg)
    {
        // Writes the message to the window
        textArea.append("Other: " + aMsg + "\n");
    }

    /**
     * Set the color of the specified button.
     *
     * @param x      the x-coordinate. Between 0-7.
     * @param y      the y-coordinate. Between 0-7.
     * @param aColor the color you want to set.
     */
    void setColor(int x, int y, ColorSt aColor)
    {
        if (aColor == ColorSt.WHITE)
        {
            board[x][y].setIcon(iWhite);
        } else if (aColor == ColorSt.BLACK)
        {
            board[x][y].setIcon(iBlack);
        } else
        {
            board[x][y].setIcon(iBackGround);
        }
    }


    /**
     * Clears the labels...
     */
    public static void clearLabels()
    {
        whiteCount.setText("White Pieces: ");
        blackCount.setText("Black Pieces: ");
        turn.setText("Turn: ");
        result.setText("Result: ");
        movesMade.setText("");

    }

    /**
     * The listener class is connected to the text field and buttons. They are only allowed to be accessed if there
     * is a connection with another aClient or server.
     *
     * @param e the event-object.
     */
    public void actionPerformed(ActionEvent e)
    {

        if (aClient.isConnected())
        {
            if (e.getSource() == textField)
            {
                yourMsg(e.getActionCommand());
                aClient.sendCommand("msg " + e.getActionCommand());
            } else
            {
                Scanner in = new Scanner(e.getActionCommand());
                int x = in.nextInt();
                int y = in.nextInt();

                System.out.println(x + "," + y);

                if (aBoardModel.getTurn() == localColor)
                {

                    if (aBoardModel.isLegalMove(x, y, localColor))
                    {
                        aBoardModel.doMove(x, y, localColor);
                        aClient.sendCommand("move " + x + " " + y);
                        movesMade.append(localColor + ": " + x + "," + y + "\n");
                    } else
                    {
                        System.out.println("Not a legal move, try again!");
                    }
                } else
                {
                    System.out.println("Not your turn yet");
                }
            }
        }

        else
        {
            System.out.println("No connection. Please wait...");
            netStatus.setText("Not Connected!");

        }
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    public void update(Observable o, Object arg)
    {

        if (localColor == null)
        {
            localColor = aBoardModel.getColor();
            yourColor.setText("Your are: " + localColor);
        }

        ColorSt[][] updatedBoard = aBoardModel.getBoard();

        for (int y = 0; y < cols; y++)
        {
            for (int x = 0; x < rows; x++)
            {
                ColorSt color = updatedBoard[x][y];
                setColor(x, y, color);
            }
        }

        turn.setText("Turn: " + aBoardModel.getTurn());

        if (aBoardModel.getResult() == null)
        {
            result.setText("Result: " + "Undecided");
        } else
        {
            result.setText("Result: " + aBoardModel.getResult());
            GUI.newGame.setEnabled(true);
        }

        whiteCount.setText("White Pieces: " + aBoardModel.count(ColorSt.WHITE));
        blackCount.setText("Black Pieces: " + aBoardModel.count(ColorSt.BLACK));
    }
}





