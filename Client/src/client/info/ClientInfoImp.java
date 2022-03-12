package client.info;

import java.io.Serializable;

public class ClientInfoImp implements Serializable
{
    private String username;

    public ClientInfoImp(String username)
    {
        this.username = username;
    }


    public String getUsername()
    {
        return username;
    }

    @Override
    public String toString()
    {
        return "Username : " + getUsername();
    }
}
