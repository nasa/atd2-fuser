package com.mosaicatm.fuser.common.camel;

public class FuserAmqProps
{
    private String url;
    private String user;
    private String password;
    
    public void setUrl (String url)
    {
        this.url = url;
    }
    
    public String getUrl ()
    {
        return url;
    }
    
    public void setUser (String user)
    {
        this.user = user;
    }
    
    public String getUser ()
    {
        return user;
    }
    
    public void setPassword (String password)
    {
        this.password = password;
    }
    
    public String getPassword ()
    {
        return password;
    }
}
