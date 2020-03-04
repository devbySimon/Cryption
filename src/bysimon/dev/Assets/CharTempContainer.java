package bysimon.dev.Assets;

public class CharTempContainer
{
    private char[] content;

    public CharTempContainer(int length)
    {
        content = new char[length];

        Flush();
    }


    public CharTempContainer Add( char c )
    {
        for(int i = GetLength() - 1; i > 0; i--)
        {
            content[i] = content[i - 1];
        }

        content[0] = c;

        return this;
    }

    public char Get( int index )
    {
        if(index >= 0 && index < GetLength())
        {
            return content[index];
        }
        else
        {
            return 0;
        }
    }

    public void Flush()
    {
        for(int i = 0; i < GetLength(); i++)
        {
            content[i] = (char)0;
        }
    }

    private int GetLength()
    {
        return content.length;
    }
}
