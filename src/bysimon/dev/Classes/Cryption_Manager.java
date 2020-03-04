package bysimon.dev.Classes;

import bysimon.dev.Assets.CharContainer;
import bysimon.dev.Interfaces.ICrypt;

import java.util.ArrayList;
import java.util.List;

public class Cryption_Manager
{
    private List<ICrypt> crypts;

    public boolean Init(CharContainer login1 , CharContainer login2 , CharContainer login3 )
    {
        CodeGenerator generator = new CodeGenerator();

        CharContainer code1 = generator.Generate(login1, login2, 1);
        CharContainer code2 = generator.Generate(login2, login3, 2);
        CharContainer code3 = generator.Generate(login1, login3, 2);

        crypts = new ArrayList<>();

        Cryption cryption = null;
        boolean initiationsCorrect = true;

        cryption = new Cryption();
        if(!cryption.Init( code1 ,  code2, Cryption.CRYPTION_LEVEL.HIGH) ) initiationsCorrect = false;
        crypts.add(cryption);

        cryption = new Cryption();
        if(!cryption.Init( code1, code3, Cryption.CRYPTION_LEVEL.LOW ) ) initiationsCorrect = false;
        crypts.add(cryption);

        if(!initiationsCorrect)
        {
            System.out.println("CryptionManager could not be initialized correctly");
        }

        return initiationsCorrect;
    }

    public CharContainer Encrypt( CharContainer input )
    {
        long before = System.currentTimeMillis();

        for ( ICrypt crypt : crypts )
        {
            input = crypt.Encrypt( input );
        }

        long after = System.currentTimeMillis() - before;
        System.out.println("Encrypting took " + after + " milliseconds\n");

        return input;
    }

    public CharContainer Decrypt( CharContainer input )
    {
        long before = System.currentTimeMillis();

        for ( int i = crypts.size() - 1; i >= 0; i--)
        {
            input = crypts.get(i).Decrypt( input );
        }

        long after = System.currentTimeMillis() - before;
        System.out.println("Decrypting took " + after + " milliseconds\n");

        return input;
    }
}
