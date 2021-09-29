import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A connect, connects to client sockets through it streams which in turn will allow them to communicate.
 */
class Connect
{
    private DataInputStream fromClient1;
    private DataInputStream fromClient2;

    private DataOutputStream toClient1;
    private DataOutputStream toClient2;

    private final int CLIENT1 = 1;
    private final int CLIENT2 = 2;

    public Connect(Socket client1, Socket client2)
    {
        if (client1 != null && client2 != null)
        {
            try
            {
                //Outputstreams
                fromClient1 = new DataInputStream(client1.getInputStream());
                fromClient2 = new DataInputStream(client2.getInputStream());

                //Inputstreams
                toClient2 = new DataOutputStream(client2.getOutputStream());
                toClient1 = new DataOutputStream(client1.getOutputStream());

                //Start the read-threads...
                new Thread(new ConnectThread(fromClient1, toClient2)).start();
                new Thread(new ConnectThread(fromClient2, toClient1)).start();

                //Initiating-handshake....
                sendTo("WHITE", CLIENT1);
                sendTo("BLACK", CLIENT2);

            }

            catch (IOException e)
            {
                System.out.println("Couldn't create streams. The error is: " + e.getMessage());
                cleanUp();
            }
        } else
        {
            System.out.println("There are no clients or the sockets passed are not valid!");
        }
    }

    /**
     * Sends a message to the specified client or all clients in this connect.
     *
     * @param aString the message.
     * @param aClient the client.
     */
    void sendTo(String aString, int aClient)
    {
        try
        {
            int ALL = 0;
            if (aClient == ALL)
            {
                toClient1.writeUTF(aString);
                toClient2.writeUTF(aString);
            } else if (aClient == CLIENT1)
            {
                toClient1.writeUTF(aString);
            } else if (aClient == CLIENT2)
            {
                toClient2.writeUTF(aString);
            } else
            {
                System.out.println("Invalid playerNr.");
            }
        }
        catch (IOException e)
        {
            System.out.println("Error in class: Connect, method: send(). The error is: " + e.getMessage());
            cleanUp();
        }
    }

    /**
     * Closes any open connections.
     */
    void cleanUp()
    {
        try
        {
            fromClient1.close();
            fromClient2.close();
            toClient1.close();
            toClient2.close();
        }

        catch (IOException e)
        {
            System.out.println("Couldn't close the connection-streams, the error is: " + e.getMessage());
        }

    }

}
