package bysimon.dev;

import bysimon.dev.Assets.CharContainer;
import bysimon.dev.Classes.Cryption_Manager;

public class Main {

    public static void main(String[] args)
    {
        CharContainer textToEncrypt = new CharContainer("This is a test string to encrypt");

        CharContainer login1 = new CharContainer("devbysimon@gmail.com" );
        CharContainer login2 = new CharContainer("Password" );
        CharContainer login3 = new CharContainer("05a6sd6f6a4w8a6" );

        boolean inputIsOutput = TestCryption(login1,login2,login3,textToEncrypt);

        System.out.println("It worked: " + inputIsOutput);
    }

    public static boolean TestCryption(CharContainer login1, CharContainer login2, CharContainer login3, CharContainer textToEncrypt)
    {
        CharContainer input = new CharContainer( "Test" );

        Cryption_Manager cryption_manager = new Cryption_Manager();
        cryption_manager.Init( login1, login2, login3);

        System.out.println("\n\nManager V1");
        CharContainer data5 = cryption_manager.Encrypt( textToEncrypt );
        CharContainer output5 = cryption_manager.Decrypt(data5);

        boolean inputIsOutput = output5.toString().equals(textToEncrypt.toString());

        return inputIsOutput;
    }
}
