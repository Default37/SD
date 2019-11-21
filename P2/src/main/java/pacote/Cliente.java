
package pacote;

import java.util.Scanner;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Cliente {
    public Cliente() {
        String cliente;
        Scanner scan = new Scanner(System.in);
        System.out.print("Cliente:");
        cliente = scan.next();
        System.out.println("Cliente:" + cliente);
        criarPasta(cliente);

    }

    public static void main(String[] args) throws IOException {
        Cliente cl1 = new Cliente();
        Scanner menusc = new Scanner(System.in);
        int menu;
        while (true) {
            System.out.println("1-Mover");
            System.out.println("2-Modificar");
            System.out.println("3-Deletar");
            System.out.println("0-Finalizar");
            menu = menusc.nextInt();
            if (menu == 1) {
                moveFile();
            } else if (menu == 2) {
                modifyFile();
            } else if (menu == 3) {
                deleteFile();
            } else if (menu == 0) {
                return;
            }
        }

    }

    public static void criarPasta(String cliente) {
        try {
            File pasta = new File("/home/aluno/Área de Trabalho/" + cliente);
            pasta.mkdirs();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao criar pasta");
            System.out.println(ex);
        }
    }

    public static void moveFile(String cliente) throws IOException {
        Scanner scanarq = new Scanner(System.in);
        String arquivo;
        System.out.println("Digite o caminho do arquivo que você deseja mover ao servidor");
        arquivo = scanarq.nextLine();
        Path pathFile = FileSystems.getDefault().getPath("", arquivo);
        byte[] fileByte = Files.readAllBytes(pathFile);
        FileOutputStream stream = new FileOutputStream("/home/aluno/Área de Trabalho/" + cliente);
        try
        {
            stream.write(fileByte);
        }
        finally
        {
            stream.close();
        }

    }

    public static void modifyFile() throws IOException
    {
        Scanner scanarq = new Scanner(System.in);
        String arquivo;
        System.out.println("Digite o caminho do arquivo que você deseja modificar");
        arquivo = scanarq.nextLine();
        
    }

    public static void deleteFile()
    {
        Scanner scanarq = new Scanner(System.in);
        String arquivo;
        System.out.println("Digite o caminho do arquivo que você deseja excluir");
        arquivo = scanarq.nextLine();
    }
}