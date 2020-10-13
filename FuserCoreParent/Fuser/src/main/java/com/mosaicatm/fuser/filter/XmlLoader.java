package com.mosaicatm.fuser.filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mosaicatm.matmdata.common.FuserSource;


public class XmlLoader {

	private final Log log = LogFactory.getLog(getClass());
	private Map<FuserSource, List<String>> includesDeparture;
	private Map<FuserSource, List<String>> excludesDeparture;
    private Map<FuserSource, List<String>> includesArrival;
    private Map<FuserSource, List<String>> excludesArrival;

	public XmlLoader() {
	    includesDeparture = new HashMap<FuserSource, List<String>>();
	    excludesDeparture = new HashMap<FuserSource, List<String>>();
	    includesArrival = new HashMap<FuserSource, List<String>>();
	    excludesArrival = new HashMap<FuserSource, List<String>>();
	}

	public void init(String xmlLocation) {
		//read includes and excludes
		File file = new File(xmlLocation);
		Document doc = null;
		if (file.exists()) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				

                Node node = null;
				
				NodeList arrivalAttributeFilters = doc.getElementsByTagName("arrivalAttributeFilters");
                for (int i = 0; i < arrivalAttributeFilters.getLength(); i++)
                {
                    node = arrivalAttributeFilters.item(i);
                    getAttributes(node.getChildNodes(), includesArrival, excludesArrival);
                }

                NodeList departureAttributeFilters = doc.getElementsByTagName("departureAttributeFilters");
                for (int i = 0; i < departureAttributeFilters.getLength(); i++)
                {
                    node = departureAttributeFilters.item(i);
                    getAttributes(node.getChildNodes(), includesDeparture, excludesDeparture);
                }
			} catch (ParserConfigurationException e) {
				log.error("fail to initialize DocumentBuilder.");
			} catch (SAXException e) {
				log.error("failt to parse xml file " + xmlLocation);
			} catch (IOException e) {
				log.error("failt to parse xml file " + xmlLocation);
			}

		} else {
			log.warn("File not exist: " + xmlLocation);
		}
	}

	private void getAttributes(NodeList region, Map<FuserSource, List<String>> includes , Map<FuserSource, List<String>> excludes) {
        Node node = null;
        Node source = null;
        Node type = null;
        
		for(int filterIndex = 0; filterIndex < region.getLength(); filterIndex++) {
			node = region.item(filterIndex);

			//going through each attribute filter
			if(node != null && node.getNodeType() == Node.ELEMENT_NODE)
			{
			    source = node.getAttributes().getNamedItem("source");
	            type = node.getAttributes().getNamedItem("type");

	            if(source != null){
	                FuserSource key = null;
	                try {
	                    key = FuserSource.valueOf(source.getNodeValue());
	                } catch (Exception e) {
	                    log.warn("Fail to parse unknown source: " + source.getNodeValue());
	                }

	                if (key != null && type != null) {
	                    if(type.getNodeValue().equalsIgnoreCase("include")){
	                        addToHashMap(key, node, includes);
	                    }else if(type.getNodeValue().equalsIgnoreCase("exclude")){
	                        addToHashMap(key, node, excludes);
	                    }
	                }
	            }
			}
			
		}
	}


	private void addToHashMap(FuserSource key, Node filter, Map<FuserSource, List<String>> hash){
		if (!hash.containsKey(key)) {
			hash.put(key, new ArrayList<String>());
		}

		NodeList children = filter.getChildNodes();
		int length = children.getLength();
		//going through each attribute
		for (int i = 0; i < length; i++) {
			if (Node.ELEMENT_NODE == children.item(i).getNodeType()) {
				Element attribute = (Element) children.item(i);
				hash.get(key).add(attribute.getNodeName());
			}
		}
	}

    public Map<FuserSource, List<String>> getIncludesDeparture()
    {
        return includesDeparture;
    }

    public Map<FuserSource, List<String>> getExcludesDeparture()
    {
        return excludesDeparture;
    }

    public Map<FuserSource, List<String>> getIncludesArrival()
    {
        return includesArrival;
    }

    public Map<FuserSource, List<String>> getExcludesArrival()
    {
        return excludesArrival;
    }

}
