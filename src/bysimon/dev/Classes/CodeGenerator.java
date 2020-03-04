package bysimon.dev.Classes;

import bysimon.dev.Assets.CharContainer;

public class CodeGenerator {

    private boolean[] used = new boolean[3838];
    private final int hashLength = 99;

    public CharContainer Generate( CharContainer login1S , CharContainer login2S, int atAmount )
    {
        used = new boolean[used.length];

        CharContainer login1 = new CharContainer(login1S);
        CharContainer login2 = new CharContainer(login2S);

        for(int i = 0; i < 10; i++)
        {
            login1.Add(login1);
            login2.Add(login2);
        }

        CharContainer bin = ExtremeHash( login1, true, hashLength);

        CharContainer code = mix( login1, login2, bin, atAmount);
        CharContainer codeAsHex = new CharContainer();

        //Cryption_V5.ToHex(code, codeAsHex);
        //codeAsHex = makeVariet(codeAsHex);

        return code;
    }

    private CharContainer mix( CharContainer login1 , CharContainer login2 , CharContainer doMix, int atAmount)
    {
        CharContainer output = new CharContainer();

        //if any doMix is longer than 36(codes amount), delete some
        //make both doMix same length

        int length = doMix.GetLength();
        int atUsed = 0;
        
        if(length > 1500)
            length = 1500;
        if(length > login1.GetLength())
            length = login1.GetLength();
        if(length > login2.GetLength())
            length = login2.GetLength();

        char bool;
        char cLogin1;
        char cLogin2;

        for(int i = 0; i < length; i++)
        {
            bool = doMix.Get(i);
            cLogin1 = login1.Get(i);
            cLogin2 = login2.Get(i);

            int character = cLogin1;

            if(bool == '1')
            {
                character += cLogin2;
                character = character % cLogin1;
            }

            character = character % used.length;

            output.Add( getNextFree(character) );

            if(i >= 1 && i < length - 1 && atUsed < atAmount && i % (length/(atAmount+1)) == (length/(atAmount+1)) - 1) {

                output.Add('@');
                atUsed++;
            }
        }

        return output;
    }

    private char getNextFree(int number) {
        
        boolean used = true;

        while(used) {

            used = this.used[number];

            if(used) {
                number++;

                if(number >= this.used.length)
                    number = 0;
            }
        }
        
        this.used[number] = true;

        return (char)(number+257);
    } //Returns free character (no double use in Code)

    /*private CharContainer makeVariet(CharContainer input) {

        CharContainer output = new CharContainer();

        for(int i = 0; i < input.GetLength(); i++)
        {
            char value = input.Get(i);

            //If 0...9
            if(value >= 49 && value <= 57)
            {
                output.Add(value);
            }
            //a...f  and minimum 1 character after [i] left
            else if(i < input.GetLength() - 1)
            {
                output += (char)(value + input.charAt(i+1) + 200);
                output += input.charAt(i + 1);
                i++;
            }
        }

        return output;
    } //16 different HEX characters to 22 characters*/

    private CharContainer ExtremeHash(CharContainer code, boolean returnAsBinary, int hashLength) {

        int sum[] = new int[hashLength];

        int position = 0;

        for(int i = 0; i < 100; i++)
        {
            for(int p = 0; p < sum.length; p++)
            {
                sum[p] += (code.Get(position) * (position-sum.length));

                position++;
                if(position >= code.GetLength())
                {
                    position = 0;
                }
            }
        }

        CharContainer output = new CharContainer();

        for(int i = 0; i < sum.length; i++)
        {
            if(returnAsBinary)
            {
                output.Add(
                        new CharContainer(
                                Integer.toBinaryString(sum[i]).toCharArray()
                        )
                );
            }
            else
            {
                output.Add(Cryption.IntToHex(sum[i]));
            }
        }

        return output;
    }
}
