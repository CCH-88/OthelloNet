import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * An OthelloClient connects to a server. It takes a BoardModel as a parameter and consists of socket variables such as
 * streams and a client socket.
 */
class OthelloClient implements Runnable
{
    private DataOutputStream dOut;
    private DataInputStream dIn;
    private Socket aSocket;
    private boolean connection;

    private final BoardModel aBoard;

    public OthelloClient(BoardModel board)
    {
        aBoard = board;

        try
        {
            aSocket = new Socket("localhost", 4444);
            System.out.println("Connected to " + aSocket);


            dIn = new DataInputStream(aSocket.getInputStream());
            dOut = new DataOutputStream(aSocket.getOutputStream());

            new Thread(this).start();
        }
        catch (Exception e)
        {
            System.out.println("Couldn't find server. The error is: " + e.getMessage());
        }
    }

    /**
     * Is set to true if there is a connection to a server or/and another player.
     *
     * @return true or false.
     */
    public boolean isConnected()
    {
        return connection;
    }

    /**
     * Sends the a message.
     *
     * @param aCommand the message.
     */
    public void sendCommand(String aCommand)
    {
        try
        {
            dOut.writeUTF(aCommand);
            dOut.flush();
        }

        catch (IOException ie)
        {
            System.out.println("Couldn't send command to clients. The error is: " + ie);
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     * In this case it's main task is to read any incoming messages from the
     * other client and interpret them in accordance to the protocol.
     *
     * @see java.lang.Thread#run()
     */
    public void run()
    {
        ColorSt localColor = null;
        ColorSt opponentColor = null;
        String handshake;
        String input;

        try
        {
            handshake = dIn.readUTF();

            Scanner scan = new Scanner(handshake);
            handshake = scan.next();

            if (handshake.equalsIgnoreCase("WHITE"))
            {
                localColor = ColorSt.WHITE;
                opponentColor = ColorSt.BLACK;
                aBoard.setColor(localColor);

            }

            else if (handshake.equalsIgnoreCase("BLACK"))
            {
                localColor = ColorSt.BLACK;
                opponentColor = ColorSt.WHITE;
                aBoard.setColor(localColor);
            }

            else
            {
                System.out.println("Unable to identify handshake from server...\n Please restart client!");
            }

            connection = true;

            while ((input = dIn.readUTF()) != null)
            {
                scan = new Scanner(input);
                String msg = scan.next();


                if (msg.equalsIgnoreCase("move"))
                {
                    int x = scan.nextInt();
                    int y = scan.nextInt();

                    if (aBoard.isLegalMove(x, y, opponentColor))
                    {
                        aBoard.doMove(x, y, opponentColor);
                        BoardView.movesMade.append(opponentColor + ": " + x + "," + y + "\n");
                    } else
                    {
                        System.out.println("Move received is not legal");
                    }
                }

                else if (msg.equalsIgnoreCase("init"))
                {
                    BoardView.clearLabels();
                    aBoard.clearBoard();
                    aBoard.initGame();

                    GUI.newGame.setEnabled(false);
                    GUI.resetGame.setEnabled(true);
                    BoardView.netStatus.setText("Connected!");
                }

                else if (msg.equalsIgnoreCase("resign"))
                {
                    aBoard.setWinner(localColor + " wins!");
                    BoardView.clearLabels();
                    aBoard.clearBoard();

                    GUI.newGame.setEnabled(true);
                    GUI.resetGame.setEnabled(false);
                }

                else if (msg.equalsIgnoreCase("exit"))
                {
                    aBoard.setWinner(localColor + " wins!");
                    aBoard.clearBoard();
                    BoardView.clearLabels();

                    GUI.newGame.setEnabled(true);
                    BoardView.netStatus.setText("No Connection...");
                    connection = false;
                }

                else if (msg.equalsIgnoreCase("msg"))
                {
                    BoardView.aMsg(scan.nextLine());
                }

                else if (!isConnected())
                {
                    GUI.newGame.setEnabled(false);
                    GUI.resetGame.setEnabled(false);
                }
            }

            connection = false;
            cleanUp();
        }

        catch (IOException e)
        {
            System.out.println("Couldn't read or send to other player, the error is: " + e.getMessage());
            cleanUp();
        }

        connection = false;
        cleanUp();


    }

    /**
     * Closes the connections that may still be open.
     */
    void cleanUp()
    {
        try
        {
            dIn.close();
            dOut.close();
            aSocket.close();
        }

        catch (IOException e)
        {
            System.out.println("Couldn't close the connection-streams, the error is: " + e.getMessage());
        }

    }

}
