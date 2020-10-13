package com.mosaicatm.fuser.common.matm.xfer;

import javax.xml.bind.annotation.XmlElement;

import com.mosaicatm.matmdata.common.MetaData;

public class GufiMetaData
{
    private String gufi;
    private MetaData metaData;        

    public GufiMetaData(){}
    public GufiMetaData( String gufi, MetaData metaData )
    {
        this.gufi = gufi;
        this.metaData = metaData;
    }        

    @XmlElement
    public String getGufi()
    {
        return gufi;
    }

    public void setGufi( String gufi )
    {
        this.gufi = gufi;
    }

    @XmlElement
    public MetaData getMetaData()
    {
        return metaData;
    }

    public void setMetaData( MetaData metaData )
    {
        this.metaData = metaData;
    }
}
