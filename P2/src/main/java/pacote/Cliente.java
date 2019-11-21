
package pacote;

import java.util.Scanner;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Cliente 
{
    public Cliente() 
    {
        String cliente;
        Scanner scan = new Scanner(System.in);
        System.out.print("Cliente:");
        cliente = scan.next();
        System.out.println("Cliente:" + cliente);
        criarPasta(cliente);

    }

    public static void main(String[] args) throws IOException 
    {
        Cliente cl1 = new Cliente();
        Scanner menusc = new Scanner(System.in);
        int menu;
        while (true) 
        {
            System.out.println("1-Mover");
            System.out.println("2-Deletar");
            System.out.println("0-Finalizar");
            menu = menusc.nextInt();
            if (menu == 1) 
            {
                moveFile();
            }
            else if (menu == 2) 
            {
                deleteFile();
            } 
            else if (menu == 0) 
            {
                return;
            }
        }

    }

    public static void criarPasta(String cliente) 
    {
        try 
        {
            File pasta = new File("C:\\Users\\ferna\\Desktop\\Douglas\\Douglas" + cliente);
            pasta.mkdirs();
        } catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(null, "Erro ao criar pasta");
            System.out.println(ex);
        }
    }

    public static void moveFile(String cliente) throws IOException 
    {
        Scanner scanarq = new Scanner(System.in);
        String arquivo;
        System.out.println("Digite o caminho do arquivo que voce deseja mover ao servidor");
        arquivo = scanarq.nextLine();
        File caminhoArquivo = new File(arquivo);
        byte[] fileByte = new byte[(int) caminhoArquivo.length()];
        FileInputStream stream = new FileInputStream(caminhoArquivo);
        stream.read(fileByte);
        FileOutputStream copy = new FileOutputStream(("C:\\Users\\ferna\\Desktop\\Douglas\\Douglas" + cliente + "\\" + caminhoArquivo.getName());
        try
        {
            stream.write(fileByte, 0, fileByte.length);
        }
        finally
        {
            stream.close();
        }
        

    }


    public static void deleteFile()
    {
        Scanner scanarq = new Scanner(System.in);
        String arquivo;
        System.out.println("Digite o caminho do arquivo que voce deseja excluir");
        arquivo = scanarq.nextLine();
    }
}