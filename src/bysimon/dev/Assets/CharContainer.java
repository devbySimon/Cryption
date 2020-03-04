package bysimon.dev.Assets;

public class CharContainer
{
    private char[] content;


    public CharContainer()
    {
        content = new char[0];
    }

    public CharContainer(int length)
    {
        content = new char[length];
    }

    public CharContainer( char[] c )
    {
        content = c;
    }

    public CharContainer( String s )
    {
        content = s.toCharArray();
    }

    public CharContainer( CharContainer c )
    {
        content = c.content.clone();
    }

    public CharContainer( char[] c , int length )
    {
        content = new char[length];

        for(int i = 0; i < c.length; i++)
        {
            Set(i,c[i]);
        }
    }

    public CharContainer( CharContainer c , int length )
    {
        content = new char[length];

        for(int i = 0; i < c.GetLength(); i++)
        {
            Set(i,c.content[i]);
        }
    }

    public CharContainer ReInit( int length )
    {
        content = new char[length];

        return this;
    }



    public char Get( int index )
    {
        if(index >= 0 && index < content.length)
        {
            return content[index];
        }
        else
        {
            return 0;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();

        for( char c : content )
        {
            output.append(c);
        }

        return output.toString();
    }

    public int GetSum()
    {
        int sum = 0;

        for(char c : content)
        {
            sum += c;
        }

        return sum;
    }

    public int GetIndexOf( char c )
    {
        return GetIndexOf( c , 1 );
    }

    public int GetIndexOf( char c , int hit )
    {
        int index = 0;
        int hits = 0;

        while (index < GetLength())
        {
            if(content[index] == c)
            {
                hits++;

                if(hits == hit)
                {
                    return index;
                }
            }

            index++;
        }

        return -1;
    }

    public int GetHighestValue()
    {
        int max = -1;

        for( char c : content )
        {
            if( c > max )
            {
                max = c;
            }
        }

        return max;
    }

    public int GetHighestValueAsHexLength()
    {
        int highest = GetHighestValue();

        int hexLength = 1;
        int maxPossibleValue = (int)(Math.pow( 16, hexLength ) - 1);

        while (highest > maxPossibleValue)
        {
            hexLength++;
            maxPossibleValue = (int)(Math.pow( 16, hexLength ) - 1);
        }

        return hexLength;
    }

    public CharContainer Set( int index , char c )
    {
        if(index >= 0 && index < GetLength())
        {
            content[index] = c;
        }

        return this;
    }

    public CharContainer Add( char c )
    {
        CharContainer tempContainer = new CharContainer(this, GetLength() + 1);

        tempContainer.Set(GetLength(), c);

        this.content = tempContainer.content.clone();

        tempContainer.Flush();

        return this;
    }

    public CharContainer Add( CharContainer c )
    {
        CharContainer tempContainer = new CharContainer(this, GetLength() + c.GetLength());

        for(int i = 0; i < c.GetLength(); i++)
        {
            tempContainer.Set(GetLength() + i, c.content[i]);
        }

        this.content = tempContainer.content.clone();

        tempContainer.Flush();

        return this;
    }

    public CharContainer Swap( int index1 , int index2 )
    {
        if(index1 >= 0 && index1 < GetLength() && index2 >= 0 && index2 < GetLength())
        {
            char cache = content[index1];
            content[index1] = content[index2];
            content[index2] = cache;
        }

        return this;
    }

    public CharContainer Reverse()
    {
        CharContainer tempContainer = new CharContainer(GetLength());

        for(int i = 0; i < GetLength(); i++)
        {
            tempContainer.Set(GetLength() - i - 1, content[i]);
        }

        this.content = tempContainer.content.clone();

        tempContainer.Flush();

        return this;
    }

    public void Flush()
    {
        for(int i = 0; i < GetLength(); i++)
        {
            content[i] = (char)(Math.random() * 100);
        }
    }

    public int GetLength()
    {
        return content.length;
    }
}
