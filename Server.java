import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;

import static javax.swing.BorderFactory.createTitledBorder;

/**
 * A server listens for incoming sockets on port the specified port. When two client sockets are received it passes them to the
 * connect-class. A server consists of a port, which needs to be entered in order for it to start-up.
 * For testing reasons only one connectionThreads is allowed pr. port. This can be altered by
 * changing the final variable, LIMIT.
 */
public class Server implements ActionListener
{

    private JButton startButton;
    private JTextField field;
    private JFrame serverWindow;
    private JLabel status;

    private ServerSocket serverSocket;
    private int port = 4444;

    private int count = 0;
    private boolean isInteger = true;

    /**
     * This method initiates the GUI of the server.
     */
    void startServer()
    {
        serverWindow = new JFrame("Server");
        JPanel pane = new JPanel();
        serverWindow.setContentPane(pane);
        serverWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverWindow.setSize(300, 200);
        serverWindow.setResizable(false);
        serverWindow.setLayout(null);
        serverWindow.setLocationByPlatform(true);

        TitledBorder port = createTitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "Server");
        port.setTitleJustification(TitledBorder.LEFT);
        pane.setBorder(port);

        JLabel label = new JLabel("Listening on port (std. 4444):");
        label.setSize(200,30);
        label.setLocation(50,20);
        serverWindow.add(label);

        status = new JLabel("Not Initiated - Press Start Server");
        status.setSize(288, 20);
        status.setLocation(2,150);
        status.setBorder(new BevelBorder(BevelBorder.LOWERED));
        serverWindow.add(status);

        field = new JTextField("4444");
        field.setSize(200,30);
        field.setLocation(50, 50);
        serverWindow.add(field);

        startButton = new JButton("Start Server");
        startButton.setSize(110,30);
        startButton.setLocation(40, 100);
        serverWindow.add(startButton);

        JButton closeButton = new JButton("Close Server");
        closeButton.setSize(110, 30);
        closeButton.setLocation(150, 100);
        serverWindow.add(closeButton);

        startButton.addActionListener(this);
        closeButton.addActionListener(this);
        field.addActionListener(this);

        serverWindow.setVisible(true);
    }

    /**
     * This is the action listener for the Server-GUI.
     * @param e The event-object.
     */
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == field)
        {
            try
            {
                port = Integer.parseInt(e.getActionCommand());

                status.setText("Port set to: " + port);
            }
            catch (NumberFormatException exception)
            {
                JOptionPane.showMessageDialog(serverWindow,
                        "Not a valid port. Please enter port 4444","Error",
                        JOptionPane.ERROR_MESSAGE);
                System.out.println(port + " is not a valid port number");
                isInteger = false;
            }
        }

        if("Start Server".equalsIgnoreCase(e.getActionCommand()))
        {
            if(isInteger && port == 4444)
            {
                try
                {
                    serverSocket = new ServerSocket(port);
                    final int LIMIT = 1;

                    do
                    {
                        new Connect(serverSocket.accept(), serverSocket.accept());
                        status.setText("Connected " + (count += 1) + "players");
                    }
                    while(count < LIMIT);

                    status.setText("Maximum limit of players on server reached!");
                    startButton.setEnabled(false);

                }
                catch (IOException exception)
                {
                    JOptionPane.showMessageDialog(serverWindow,
                            "Port not available or invalid port number","Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println("Server couldn't create server-socket on port: " + port + ". Try again.");
                    System.out.println("The error is: " + exception.getMessage());
                }
            }
            else
                System.out.println("Not an integer or port nr. not valid!");
        }

        if("Close Server".equalsIgnoreCase(e.getActionCommand()))
        {
            if(serverSocket != null)
            {
                try
                {
                    serverSocket.close();
                    System.exit(0);
                }
                catch (IOException e1)
                {
                    JOptionPane.showMessageDialog(serverWindow,
                            "Couldn't close server","Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println("Error is: " + e1.getMessage());
                }

            }
            else
                System.out.println("Server not initiated!");

        }

    }

    /**
     * Starts the server, when a port has been specified.
     *
     * @param args the main static variable
     */
    public static void main(String[] args)
    {
        Server server = new Server();
        server.startServer();
    }

}

