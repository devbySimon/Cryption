package bysimon.dev.Classes;

import bysimon.dev.Assets.CharContainer;
import bysimon.dev.Assets.CharTempContainer;
import bysimon.dev.Interfaces.ICrypt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cryption implements ICrypt
{
    private final static List<Character> hex = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'));

    public static enum CRYPTION_LEVEL
    {
        LOW( 4 , 10 ),
        HIGH( 1 , 2 );

        CRYPTION_LEVEL( int value1 , int value2 )
        {
            StartFaktor = value1;
            Multiplicator = value2;
        }

        public int StartFaktor;
        public int Multiplicator;
    }

    private final int neededAmountOfSnippets = 5;

    private CharContainer code1, code2;

    private int[] offsets;
    private int shiftInterval,shiftLength,shiftOffset;
    private CRYPTION_LEVEL level;

    protected boolean usable;

    private boolean errorAtCreation = false;
    protected String errorLog = "";

    @Override
    public boolean Init( Object initParam1 , Object initParam2 , Object initParam3 )
    {
        code1 = (CharContainer) initParam1;
        code2 = (CharContainer) initParam2;
        this.level = (CRYPTION_LEVEL) initParam3;

        this.offsets = generateOffsets();
        generateShift(); //Generate Displacement Factors

        usable = !checkUsability(); //CheckUsability returns TRUE when FAILED
        if(!usable)
            System.out.println("Cryption code could not be created correctly");

        System.out.println(errorLog);

        return usable;
    }

    /**
     * Normal text to encrypted text
     * @param dataRaw
     * @return
     */
    @Override
    public CharContainer Encrypt(CharContainer dataRaw) {
        
        if(usable)
        {
            System.out.println("\nStarting Encryption");

            long before = System.currentTimeMillis();
            long before2 = System.currentTimeMillis();

            CharContainer data = new CharContainer(dataRaw);

            //<editor-fold> Caesar
            int startOffset = code1.GetSum() + code2.GetSum();
            int offsetPos = startOffset % offsets.length;
            CharTempContainer tempContainer = new CharTempContainer(5);

            for(int i = 0; i < data.GetLength(); i++)
            {
                int pos = (i + startOffset) % data.GetLength();

                // Insert new Char into Temp
                tempContainer.Add(data.Get(pos));

                // Set data to new value
                data.Set( pos,

                        (char)
                        (data.Get(pos) +
                        tempContainer.Get(1) +
                        tempContainer.Get(2) +
                        offsets[offsetPos % offsets.length])
                );

                offsetPos++;
            }

            tempContainer.Flush();
            
            System.out.println("Done ceasar | " + (System.currentTimeMillis() - before2));
            before2 = System.currentTimeMillis();
            //</editor-fold>


            //<editor-fold> Convert to Hex
            CharContainer output = new CharContainer();
            int hexLength = ToHex(data, output);

            System.out.println("Done hex_migration " + hexLength + " | " + (System.currentTimeMillis() - before2));
            before2 = System.currentTimeMillis();
            //</editor-fold>


            //<editor-fold> Dislocate
            int sumShifts = 0;
            int factor = level.StartFaktor;
            while(shiftInterval / factor >= 10)
            {
                sumShifts += Dislocate(output, shiftInterval / factor, shiftOffset / factor, shiftLength);
                factor *= level.Multiplicator;
            }

            System.out.println("Done Dislocation: " + sumShifts + " | " + (System.currentTimeMillis() - before2));
            before2 = System.currentTimeMillis();
            //</editor-fold>


            // Add HexLength to result
            output.Add( (char)hexLength );


            // Encryption done
            long after = System.currentTimeMillis() - before;
            System.out.println("Encrypting took " + after + " milliseconds\n");


            return output;
        }
        else
        {
            System.out.println("\nEncryption not possible\n");

            return null;
        }
    }

    /**
     * Encrypted text to normal text
     * @param dataRaw
     * @return
     */
    @Override
    public CharContainer Decrypt(CharContainer dataRaw) {
        
        if(usable)
        {
            System.out.println("\nStarting Decryption");

            long before = System.currentTimeMillis();
            long before2 = System.currentTimeMillis();


            // Get HexLength from Data (last Character)
            int hexLength = dataRaw.Get(dataRaw.GetLength() - 1);

            // Remove last char from data (HexLength)
            CharContainer data = new CharContainer(dataRaw, dataRaw.GetLength() - 1);


            //<editor-fold> Relocate
            int factor = level.StartFaktor;
            while(shiftInterval / factor >= 10)
                factor *= level.Multiplicator;
            factor /= level.Multiplicator;

            int sumShifts = 0;

            while(factor >= level.StartFaktor)
            {
                sumShifts += Relocate(data, shiftInterval / factor, shiftOffset / factor, shiftLength); //Relocate CharContainer
                factor /= level.Multiplicator;
            }

            System.out.println("Done Relocation: " + sumShifts + " | " + (System.currentTimeMillis() - before2));
            before2 = System.currentTimeMillis();
            //</editor-fold>


            //<editor-fold> Hex to Char
            CharContainer output = FromHex(data, hexLength);

            System.out.println("Done hex_migration " + hexLength + " | " + (System.currentTimeMillis() - before2));
            before2 = System.currentTimeMillis();
            //</editor-fold>


            //<editor-fold> Reverse Ceasar
            int startOffset = code1.GetSum() + code2.GetSum();
            int offsetPos = startOffset % offsets.length;
            CharTempContainer tempContainer = new CharTempContainer(5);

            for(int i = 0; i < output.GetLength(); i++)
            {
                int pos = (i + startOffset) % output.GetLength();

                output.Set(pos,

                          (char)
                          (output.Get(pos) -
                          tempContainer.Get(0) -
                          tempContainer.Get(1) -
                          offsets[offsetPos % offsets.length])
                );

                tempContainer.Add(output.Get(pos));

                offsetPos++;
            }

            tempContainer.Flush();
            
            System.out.println("Done caesar | " + (System.currentTimeMillis() - before2));
            before2 = System.currentTimeMillis();
            //</editor-fold>

            
            
            long after = System.currentTimeMillis() - before;
            System.out.println("Decrypting took " + after + " milliseconds\n");
            
            return new CharContainer(output);
        }
        else
        {
            System.out.println("\nDecryption not possible\n");

            return null;
        }
        
    }

    /**
     *
     * @param data
     * @param shiftInterval
     * @param shiftOffset
     * @param shiftLength
     * @return
     */
    private int Dislocate(CharContainer data, int shiftInterval, int shiftOffset, int shiftLength) { //Part of Encrypt
        
        int frequency = shiftInterval / 10;
        frequency = SumAndRepeat(frequency,-10,10,20);
        
        int amount = 0;
        
        for(int i=0;i<frequency;i++)
        {
            int pos = 0;
            
            while(pos + shiftOffset + shiftLength < data.GetLength())
            {
                for(int posC = 1; posC <= shiftLength; posC++)
                {
                    data.Swap(
                            pos + posC + shiftOffset,
                            pos + posC
                    );

                    /*for(int posN = shiftOffset; posN >= 0; posN--) {
        
                        char cache = data[pos + posC + posN];
                        data[pos + posC + posN] = data[pos + posC + posN - 1];
                        data[pos + posC + posN - 1] = cache;
                    }*/
                }
                
                amount++;
                pos += shiftInterval;
            }
        }

        return amount;
    } //e.g. a012bc => abc012

    /**
     *
     * @param data
     * @param shiftInterval
     * @param shiftOffset
     * @param shiftLength
     * @return
     */
    private int Relocate(CharContainer data, int shiftInterval, int shiftOffset, int shiftLength) { //Part of Decrypt
        
        int frequency = shiftInterval / 10;
        frequency = SumAndRepeat(frequency,-10,10,20);
        
        int startPos = 0; //Position of last Dislocation
        while(startPos + shiftOffset + shiftLength < data.GetLength())
            startPos += shiftInterval;
        
        int amount = 0;
        
        for(int i = 0; i < frequency; i++)
        {
            int pos = startPos;
            
            while(pos >= 0)
            {
                if(pos + shiftOffset + shiftLength < data.GetLength())
                {
                    for(int posC = shiftLength; posC >= 1; posC--)
                    {
                        data.Swap(
                                pos + posC + shiftOffset,
                                pos + posC
                        );
                    }
                    
                    amount++;
                }
                pos -= shiftInterval;
            }
        }

        return amount;
    } //e.g. abc012 => a012bc

    /**
     *
     * @return
     */
    private boolean checkUsability()
    {
        boolean found = false;
        String output = "";

        if(errorAtCreation) {
            
            output += "There was an error at creation \n";
            
            System.out.println("There was an error at creation");
            found = true;
        }
        
        if(found) {
            
            errorLog = output;
            System.out.println("Error: " + errorLog);
        }
        
        return found;
    } //Check if everything is Generated correctly

    /**
     * Generates Offsets
     * @return
     */
    private int[] generateOffsets()
    {
        int[] offsets = new int[5];
        boolean[] blockShuffle = new boolean[5];
        int[] shuffles = new int[5];
        List<CharContainer> snippets = new ArrayList<>();


        // Parse Snippets out of codes (Split by @ (code1 + @ + code2))
        CharContainer container = new CharContainer(code1).Add('@').Add(code2);

        char c;
        snippets.add(new CharContainer());
        for(int i = 0; i < container.GetLength(); i++)
        {
            c = container.Get(i);

            if(c == '@')
            {
                // Set new Last Snippet
                snippets.add(new CharContainer());
            }
            else
            {
                // Add new Char to Last Snippet (CharContainer will automatically get longer)
                snippets.get(snippets.size() - 1).Add(c);
            }
        }

        // Flush unused Container
        container.Flush();


        // Check if snippet creation was successfull
        if(snippets.size() != neededAmountOfSnippets)
        {
            errorLog += "Not correct amount of snippets [" + snippets.size() + "]";
            errorAtCreation = true;

            //Flush all Containers
            for(CharContainer container1 : snippets)
            {
                container1.Flush();
            }

            return null;
        }


        // Generate values from Snippets
        int sum;
        for(int i = 0; i < neededAmountOfSnippets; i++)
        {
            container = snippets.get(i);

            // Collect sum of all Chars in Snippet
            sum = i + container.GetSum();
            
            offsets[i] = sum;

            // If Sum is Odd, don't shuffle it at the end
            if(sum % 2 == 1)
            {
                blockShuffle[i] = true;
            }
            else
            {
                shuffles[i] = sum;
            }

            // Flush Container, not used again
            container.Flush();
        }


        for(int i = 0; i < neededAmountOfSnippets; i++) System.out.println(i + " " + shuffles[i] + " " + blockShuffle[i] + " " + offsets[i]);


        // Sort Shuffles by size (Biggest to Smallest)
        boolean switched = true;
        while (switched)
        {
            switched = false;
            for(int i = 0; i < (neededAmountOfSnippets - 1); i++)
            {
                if(shuffles[i] < shuffles[i + 1])
                {
                    switched = true;
                    
                    int cache = shuffles[i];
                    shuffles[i] = shuffles[i + 1];
                    shuffles[i + 1] = cache;
                }
            }
        }


        for(int i = 0; i < neededAmountOfSnippets; i++) System.out.println(i + " " + shuffles[i] + " " + blockShuffle[i] + " " + offsets[i]);

        // Override Offsets (Only those to shuffle) with position in Sorted Order
        int shufflePos = 0;
        for(int i = 0; i < neededAmountOfSnippets; i++)
        {
            if(!blockShuffle[i])
            {
                offsets[i] = shuffles[shufflePos];
                shufflePos++;
            }
        }


        for(int i = 0; i < neededAmountOfSnippets; i++) System.out.println(i + " " + shuffles[i] + " " + blockShuffle[i] + " " + offsets[i]);


        return offsets;
    }

    /**
     *
     */
    private void generateShift()
    {
        // Get Positions of '@'
        int positionA = code1.GetIndexOf('@' , 1);
        int positionB1 = code2.GetIndexOf('@' , 1);
        int positionB2 = code2.GetIndexOf('@' , 2);


        // Calculate Intervals
        shiftInterval = code1.Get(positionA-1) + code2.Get(positionB1-1);
        shiftLength = code1.Get(positionA) + code2.Get(positionB2);
        shiftOffset = code2.Get(positionB1-1) + code2.Get(positionB2);


        // Calculate Difference
        int diff = Math.abs(shiftInterval - shiftLength) + Math.abs(shiftLength - shiftOffset) + Math.abs(shiftInterval - shiftOffset);

        // Normalize all
        shiftInterval = Math.abs(shiftInterval - diff) + 1;
        shiftLength = Math.abs(shiftLength - diff) + 1;
        shiftOffset = Math.abs(shiftOffset - diff) + 1;
        
        System.out.println(shiftInterval + " " + shiftLength + " " + shiftOffset + " " + diff);
    }

    private int SumAndRepeat(int a, int b, int min, int max) {
        
        int sum = min + a + b;
        
        while(sum >= max) {
            
            sum -= max;
            sum += min;
            
        }
        
        return sum;
    }


    /**
     *
     * @param data
     * @param output
     * @return
     */
    public static int ToHex( CharContainer data , CharContainer output )
    {
        int hexLength = data.GetHighestValueAsHexLength() + 1;

        output.ReInit( data.GetLength() * hexLength );

        // Calculate Offset, needed in order to bring all values to this Hex-Length
        int hexLengthOffset = (int)(Math.pow( 16, (hexLength-1) ));
        CharContainer hex;

        for(int index = 0; index < data.GetLength(); index++)
        {
            int c = data.Get(index);

            c += hexLengthOffset; //Letter as Integer value (e.g. "a" == 97 + hexLengthOffset)
            // + hexLengthOffset in order to not be shorter than 5 letters (0 + hexLengthOffset equals 100000 (hexLength = 6))

            hex = IntToHex( c );

            for(int i = 0; i < hex.GetLength(); i++)
            {
                output.Set((index * hexLength) + i, hex.Get(i));
            }

            hex.Flush();
        }

        return hexLength;
    }

    /**
     *
     * @param data
     * @param hexLength
     * @return
     */
    public static CharContainer FromHex( CharContainer data , int hexLength )
    {
        CharContainer output = new CharContainer( data.GetLength() / hexLength );

        int hexLengthOffset = (int)(Math.pow( 16, (hexLength-1) ));
        CharContainer hex;
        int position = 0;

        //Go trough whole data and put hexes together
        for(int i = 0; i + (hexLength-1) < data.GetLength(); i += hexLength)
        {
            hex = new CharContainer();

            // Collect Hex
            for(int hexPos = 0; hexPos < hexLength; hexPos++)
            {
                hex.Add(data.Get(i + hexPos));
            }

            // Convert Hex to Char
            int hexAsInteger = HexToInt( hex );
            hexAsInteger -= hexLengthOffset; // Subtract offset

            output.Set(position, (char)hexAsInteger );
            position++;

            hex.Flush();
        }

        return output;
    }

    /**
     * https://www.permadi.com/tutorial/numDecToHex/
     * @param value
     * @return
     */
    public static CharContainer IntToHex( int value )
    {
        CharContainer hexResult = new CharContainer();

        while (value > 0)
        {
            int result = value / 16;
            int remainder = value % 16;

            hexResult.Add( hex.get(remainder) );

            value = result;
        }


        hexResult.Reverse();

        return hexResult;
    }

    /**
     *
     * @param c
     * @return
     */
    public static int HexToInt( CharContainer c )
    {
        int value = 0;

        for(int i = 0; i < c.GetLength(); i++)
        {
            char currentHex = c.Get(i);
            int factor = c.GetLength() - i - 1;

            value += (hex.indexOf(currentHex) * Math.pow(16, factor));
        }

        return value;
    }
}
