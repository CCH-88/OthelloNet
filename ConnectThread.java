import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A connect thread reads the input from the specified datainputstream and sends any information through it's
 * dataoutputstream.
 */
class ConnectThread implements Runnable
{

    private final DataInputStream fromOther;
    private final DataOutputStream toOther;


    public ConnectThread(DataInputStream in, DataOutputStream out)
    {
        fromOther = in;
        toOther = out;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     * In this case it reads anything from "fromOther" and sends the input
     * "toOther".
     *
     * @see Thread#run()
     */
    public void run()
    {
        String input;

        try
        {
            while ((input = fromOther.readUTF()) != null)
            {
                sendToOther(input);
            }
        }
        catch (IOException e)
        {
            System.out.println("ConnectThread: Unable to read from the other socket. Error is: " + e.getMessage());
            cleanUp();

        }

    }

    /**
     * Sends a message to the other person connected.
     *
     * @param msg the message
     */
    void sendToOther(String msg)
    {
        try
        {
            toOther.writeUTF(msg);
        }
        catch (IOException e)
        {
            System.out.println("Unable to send from the socket. Error thrown: " + e.getMessage());
        }
    }

    /**
     * Closes any open connections.
     */
    void cleanUp()
    {
        try
        {
            fromOther.close();
            toOther.close();
        }

        catch (IOException e)
        {
            System.out.println("Couldn't close the connection-streams, the error is: " + e.getMessage());
        }

    }
}
