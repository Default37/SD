
package pacote;

import java.util.Scanner;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class Cliente extends ReceiverAdapter
{
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    String cliente;
    JChannel channel;
    public Cliente() throws IOException 
    {
        Scanner scan = new Scanner(System.in);
        System.out.print("Cliente:");
        cliente = scan.next();
        System.out.println("Cliente:" + cliente);
        criarPasta(cliente);
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        Path dir = Paths.get("/home/aluno/Área de Trabalho/" + cliente);
        walkAndRegisterDirectories(dir);

    }

    public static void main(String[] args) throws Exception 
    {
        Cliente cl1 = new Cliente();
        cl1.processEvents();
    }

    public static void criarPasta(String cliente) 
    {
        try 
        {
            File pasta = new File("/home/aluno/Área de Trabalho/" + cliente);
            pasta.mkdirs();
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(null, "Erro ao criar pasta");
            System.out.println(ex);
        }
    }

    public void await() 
    {
        try 
        {
            channel = new JChannel();
            channel.setName(cliente);
            channel.connect("Communication");
            channel.setReceiver(this);
            Message msg = new Message(null,"Cliente conectado");
            channel.send(msg);
            processEvents();
            channel.close();
        } 
        catch (Exception e) 
        {

        }
    }

    public void receive(Message msg) 
    {
        
    }

    public void viewAccepted(View new_view) 
    {
        System.out.println("** view: " + new_view);
    }

    private void registerDirectory(Path dir) throws IOException 
    {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }
 
    /**
     * Register the given directory, and all its sub-directories, with the WatchService.
     */
    private void walkAndRegisterDirectories(final Path start) throws IOException 
    {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() 
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException 
            {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
 
    /**
     * Process all events for keys queued to the watcher
     * 
     * @throws Exception
     */
    void processEvents() throws Exception 
    {
        for (;;) 
        {
            // wait for key to be signalled
            WatchKey key;
            try 
            {
                key = watcher.take();
            } 
            catch (InterruptedException x) 
            {
                return;
            }
 
            Path dir = keys.get(key);
            if (dir == null) 
            {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
 
            for (WatchEvent<?> event : key.pollEvents()) 
            {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();
 
                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>)event).context();
                Path child = dir.resolve(name);
 
                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
 
                // if directory is created, and watching recursively, then register it and its sub-directories
                if (kind == ENTRY_CREATE) 
                {
                    try 
                    {
                        if (Files.isDirectory(child)) 
                        {
                            walkAndRegisterDirectories(child);
                            criarPasta(String.valueOf(child));
                        }
                        else
                        {
                            File caminhoArquivo = new File(child.toString());
                            byte[] fileByte = new byte[(int) caminhoArquivo.length()];
                            FileInputStream stream = new FileInputStream(caminhoArquivo);
                            stream.read(fileByte);
                            FileX aux = new FileX(fileByte, child.toString(), false, 1);
                            Message msg = new Message(null, aux);
                            channel.send(msg);
                            stream.close();
                        }
                    } 
                    catch (IOException x) 
                    {
                        // do something useful
                    }
                }
                else if (kind == ENTRY_DELETE)
                {
                    FileX aux = new FileX(null, child.toString(), false, 2);
                    Message msg = new Message(null, aux);
                    channel.send(msg);
                }
            }
 
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) 
            {
                keys.remove(key);
                // all directories are inaccessible
                if (keys.isEmpty()) 
                {
                    break;
                }
            }
        }
    }
}