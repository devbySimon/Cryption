package bysimon.dev.Interfaces;

import bysimon.dev.Assets.CharContainer;

public interface ICrypt
{
    abstract boolean Init(Object initParam1, Object initParam2, Object initParam3);

    abstract CharContainer Encrypt(CharContainer input);

    abstract CharContainer Decrypt(CharContainer input);
}
