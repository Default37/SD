package pacote;

import java.io.Serializable;

public class FileX implements Serializable
{
    private static final long serialVersionUID = 1L;
    byte[] arquivo;
    String nome;
    boolean diretorio;
    int codigo; 

    public FileX(byte[] arquivo, String nome, boolean diretorio,int codigo)
    {
        this.arquivo=arquivo;
        this.nome=nome;
        this.diretorio=diretorio;
        this.codigo=codigo;
    }
}
