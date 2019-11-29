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
                if (fileServer.diretorio == true)
                {
                    File refreshFile = null;
                    if (msg.src().toString().contains("Server")) 
                    {
                        refreshFile = new File("Servers/" + name + "/" + fileServer.nome);
                    } 
                    else 
                    {
                        refreshFile = new File("Servers/" + name + "/" + msg.src() + "/" + fileServer.nome);
                    }
                    refreshFile.mkdirs();
                }
                else
                {
                    File refreshFile = null;
                    if (msg.src().toString().contains("Server")) 
                    {
                        refreshFile = new File("Servers/" + name + "/" + fileServer.nome);
                    } 
                    else 
                    {
                        refreshFile = new File("Servers/" + name + "/" + msg.src() + "/" + fileServer.nome);
                    }
                    File pasta = new File(refreshFile.getParent());
                    pasta.mkdirs();
                    try 
                    {
                        FileOutputStream outputStream = new FileOutputStream(refreshFile);
                        byte[] fileByte = fileServer.arquivo;
                        outputStream.write(fileByte, 0, fileByte.length);
                        outputStream.close();
                    } 
                    catch (FileNotFoundException e) 
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } 
                    catch (IOException e) 
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                    
            }
            else if (fileServer.codigo == 2)
            {
                File refreshFile = new File("Servers/" + name + "/" + msg.src() + "/" + fileServer.nome);
                if (refreshFile.isFile())
                {  
                    refreshFile.delete();
                }
                else
                {
                    try 
                    {
                        del(new File("Servers/" + name + "/" + msg.src() + "/" + fileServer.nome),msg.src());
                    }
                    catch (Exception e) 
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }
        else if(msg.getObject().toString().equals("Send Files"))
        {
            try 
            {
                sync(msg.src(), new File("Servers/" + name + "/"));
            } 
            catch (FileNotFoundException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (Exception e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  
        }
        else if (msg.getObject().toString().equals("Cliente conectado"))
        {
            File path = new File("Servers/" + name);
            File[] randfile = path.listFiles();
            for (int i = 0; i < randfile.length; i++) 
            {
                if (randfile[i].getName().equals(msg.src().toString())) 
                {
                    try {
                        enviarDadosCliente(msg.src(), new File("Servers/" + name + "/" + msg.src()));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

        
    }

    public void enviarDadosCliente(Address address, File fileAux) throws Exception
    {
        File[] randfile = fileAux.listFiles();
        for(int i=0;i<randfile.length;i++){
            if(randfile[i].isFile())
            {
                FileInputStream inputStream = new FileInputStream(randfile[i]);
                byte[] fileByte = new byte[(int) randfile[i].length()];
                inputStream.read(fileByte);
                String[] aux2 = randfile[i].toString().split("Servers/" + name + "/" + address + "/");
                FileX aux = new FileX(fileByte, aux2[1], false,1);
                Message msg = new Message(address,aux);
                channel.send(msg);
                inputStream.close();
            }
            else
            {
                enviarDadosCliente(address, randfile[i]);
            }
        }
        if(!fileAux.toString().equals("Servers/" + name + "/" + address)){
            String[] aux2 = fileAux.toString().split("Servers/" + name + "/" + address + "/");
            FileX aux3 = new FileX(null, aux2[1], true,100);
            Message msg = new Message(address, aux3);
            channel.send(msg);
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

    public void sync(Address address, File fileAux) throws Exception
    {
        File[] randfile = fileAux.listFiles();
        for(int i=0;i<randfile.length;i++)
        {
            if(randfile[i].isFile())
            {
                FileInputStream inputStream = new FileInputStream(randfile[i]);
                byte[] fileByte = new byte[(int) randfile[i].length()];
                inputStream.read(fileByte);
                String[] aux2 = randfile[i].toString().split("Servidores/"+name+"/");
                FileX aux = new FileX(fileByte, aux2[1], false, 1);
                Message msg = new Message(address,aux);
                channel.send(msg);
                inputStream.close();
            }
            else
            {
                sync(address, randfile[i]);
            }
        }
        if(!fileAux.toString().equals("Servers/"+name)){
            String[] aux2 = fileAux.toString().split("Servers/"+name+"/");
            FileX aux3 = new FileX(null, aux2[1], true, 1);
            Message msg = new Message(address, aux3);
            channel.send(msg);
        }

    }

    public void del(File fileAux, Address address)
    {
        File[] randfile = fileAux.listFiles();
        for(int i=0;i<randfile.length;i++)
        {
            if(randfile[i].isFile()){
                randfile[i].delete();
            }
            else
            {
                del(randfile[i], address);
            }
        }
        fileAux.delete();

    }

}
