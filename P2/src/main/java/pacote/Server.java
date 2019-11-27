package pacote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class Server extends ReceiverAdapter
{
    static String name;
    JChannel channel;

    public Server() throws Exception 
    {
        Random r = new Random();
        name = "Server" + (r.nextInt(65536) - 32768);
        File fr = new File("Servers/" + name);
        fr.mkdirs();
        channel = new JChannel();
        channel.setName(name);
        channel.connect("Communication");
        Message msg = new Message(null, "Send Files");
        channel.send(msg);
    }

    public static void main(String[] args) throws Exception 
    {
        Server servidor = new Server();
        servidor.raise(name);
    }

    public void receive(Message msg) 
    {
        if (msg.getObject() instanceof FileX)
        {
            FileX fileServer = msg.getObject();
            if (fileServer.codigo == 1)
            {
                newDir();

            }
            else if (fileServer.codigo == 2)
            {
                del();

            }
        }
        else if(msg.getObject().toString().equals("Send Files"))
        {
            sync();
            
        }
        
    }

    public void raise(String name) 
    {
        try 
        {
            channel.setReceiver(this);
        } 
        catch (Exception e) 
        {
            
        }
    }
    
    public void viewAccepted(View new_view) 
    {
        System.out.println("** view: " + new_view);
    }

    public void sync()
    {

    }

    public void del()
    {

    }

    public void newDir()
    {

    }
}


