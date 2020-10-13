package com.mosaicatm.fuser.client.api.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CloneUtil
{

    static final String NOTICE =
        "Copyright (c), 2011 by Mosaic ATM\nAll Rights Reserved " +
        "This file has been developed by Mosaic ATM under US federal " +
        "government funding. The US federal government has a world-wide, " +
        "royalty-free and irrevocable license to use this file for any " +
        "government purpose.";



    /**
     * This function will create a copy of an object. The method requires the
     * object to be Serializable.
     * 
     * @param object
     *            the object to be copied
     * @return an exact copy of the original object
     * @throws IOException
     *             if the object is not Serialziable
     */
    public static Object clone(Object object) 
    {
        if (object == null)
            return null;
        try
        {
        // The object is first writen out to a ObjectOutputStream
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
        objectOutput.writeObject(object);
        objectOutput.flush();
        objectOutput.close();
        // ObjectInputStream reads the in-memeory output stream to create the
        // cloned object
        ByteArrayInputStream byteInput = new ByteArrayInputStream(byteOutput
                .toByteArray());
        ObjectInputStream objectInput = new ObjectInputStream(byteInput);
        
        return objectInput.readObject();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
    
    public static <T> T cloneObject (Object object)
    {
        return (T) clone(object);
    }

}
