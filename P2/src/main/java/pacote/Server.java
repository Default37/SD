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
        File fr = new File("Servidores/" + name);
        fr.mkdirs();
        channel = new JChannel();
        channel.setName(name);
        channel.connect("ClienteServidor");
        Message msg = new Message(null, "Me enviem os arquivos");
        channel.send(msg);
    }

    public static void main(String[] args) throws Exception 
    {
        Server servidor = new Server();
        servidor.raise(name);
    }
}


