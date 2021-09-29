import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * This class is holding the graphics that are not related to the board (BoardView) menu-bar etc.
 * It extends JFrame and is thus a subclass of the aforementioned.
 */
public class GUI extends JFrame implements ActionListener
{

    //JMenuItems - file menu
    public static JMenuItem newGame = null;
    public static JMenuItem resetGame = null;
    private static JMenuItem exitOthello = null;

    //BoardModel
    private final BoardModel aModel;
    //OthelloClient
    private final OthelloClient aClient;



    private GUI()
    {
        //The JFrame methods
        super("OthelloNet (Client)");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.setLocationByPlatform(true);

        //Initiating the classes and adds them to the GUI
        aModel = new BoardModel();
        aClient = new OthelloClient(aModel);
        BoardView aBoard = new BoardView(aModel, aClient);

        aBoard.addGameInfo(this);
        aBoard.addChatAndLabels(this);
        add(aBoard, BorderLayout.CENTER);
        this.pack();

        //The menu bar and the file-menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        //New game menu-item is added to the JMenu
        newGame = new JMenuItem("New Game", KeyEvent.VK_N);
        newGame.addActionListener(this);

        //Reset Game
        resetGame = new JMenuItem("Resign", KeyEvent.VK_R);
        resetGame.addActionListener(this);

        //Exit Game
        exitOthello = new JMenuItem("Exit Othello", KeyEvent.VK_X);
        exitOthello.addActionListener(this);

        //Adding menu items to file menu..
        fileMenu.add(newGame);
        fileMenu.add(resetGame);
        fileMenu.addSeparator();
        fileMenu.add(exitOthello);

        //Sets the menu bar...
        setJMenuBar(menuBar);

        //Makes it visible...
        this.setVisible(true);
    }


    /**
     * Captures the actions from the menu-bar. It first checks whether a client is connected or not.
     * If true, it goes in to the if-condition and checks if new game or reset game was pushed.
     * New game sends a start-game command, init, to the other player. It clears the board and labels
     * and initiates the game.
     * If the user resigns the if-method will set the other player as the winner and reset the board and
     * labels.
     * If exit, send msg to other player and exit game.
     *
     * @param e ActionEvent
     */

    public void actionPerformed(ActionEvent e)
    {

        if (aClient.isConnected())
        {
            if (newGame == e.getSource())
            {
                aClient.sendCommand("init");
                aClient.sendCommand("msg " + "Let's play!");

                BoardView.clearLabels();
                aModel.clearBoard();
                aModel.initGame();

                newGame.setEnabled(false);
                resetGame.setEnabled(true);
                BoardView.netStatus.setText("Connected!");

            }

            if (resetGame == e.getSource())
            {
                aClient.sendCommand("resign");
                aClient.sendCommand("msg " + "I resign!");

                BoardView.clearLabels();
                aModel.setWinner(aModel.getColor() + " lose!");
                aModel.clearBoard();

                newGame.setEnabled(true);
                resetGame.setEnabled(false);
            }
        }

        else
        {
            System.out.println("No connection. Please wait...");
            BoardView.aMsg("Cannot start game! Need another player or server was not initiated first. " +
                    "Try restarting the client");
            BoardView.netStatus.setText("Not Connected!");

        }

        if (exitOthello == e.getSource())
        {
            if (aClient.isConnected())
            {
                aClient.sendCommand("msg " + "The other player left the game. Clearing the board..");
                aClient.sendCommand("exit");
                newGame.setEnabled(true);
            }

            System.exit(0);
        }
    }

    /**
     * Initiates the GUI...
     *
     * @param args the static main variable.
     */

    public static void main(String[] args)
    {
        new GUI();
    }
}
