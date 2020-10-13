package com.mosaicatm.matmdata.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class MatmFlightNamespacePrefixMapper extends NamespacePrefixMapper
{
    private Map<String, String> namespaceMap = new HashMap<>();
    
    public MatmFlightNamespacePrefixMapper() {
        namespaceMap.put("http://www.mosaicatm.com/matmdata/flight/extension", "ext");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/flight", "flt");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/envelope", "env");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/common", "com");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/flightComposite", "fltcom");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/position", "pos");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/positionenvelope", "posenv");   
        namespaceMap.put(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");
    }
 
    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        return namespaceMap.getOrDefault(namespaceUri, suggestion);
    }
    
    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { 
            XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI
        };
    }

}
