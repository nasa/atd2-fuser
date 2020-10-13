package com.mosaicatm.fuser.services.util;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class IgnoreUnexpectedElementsHandler
implements ValidationEventHandler 
{

	public static final String NOTICE = 
		"Copyright (c) 2012 by Mosaic ATM, Inc.  All rights reserved."
		+ "This file has been developed by Mosaic ATM under US federal "
		+ "government funding via FAA contract DTFAWA-08-C-00076. The US "
		+ "federal government has a world-wide, royalty-free and irrevocable "
		+ "license to use this file for any government purpose.";


	// Only ignore validation errors related to unexpected elements in order
	// to maintain forward compatibility.
	@Override
	public boolean handleEvent(ValidationEvent event) 
	{
		return !event.getMessage().startsWith("unexpected element(");
	}

}
