package com.mosaicatm.matmdata.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class MatmSectorAssignmentNamespacePrefixMapper extends NamespacePrefixMapper
{
    private Map<String, String> namespaceMap = new HashMap<>();
    
    public MatmSectorAssignmentNamespacePrefixMapper() {
        namespaceMap.put("http://www.mosaicatm.com/matmdata/envelope", "env");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/common", "com");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/sector", "sa");
        namespaceMap.put("http://www.mosaicatm.com/matmdata/sectorenvelope", "sae");     
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
