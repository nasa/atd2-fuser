package com.mosaicatm.matmdata.util;

import javax.xml.bind.JAXBElement;

import org.jvnet.jaxb2_commons.lang.JAXBMergeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

public class MatmFlightMergeStrategy extends JAXBMergeStrategy
{
    @Override
    protected Object mergeInternal(ObjectLocator leftLocator,
            ObjectLocator rightLocator, Object left, Object right) {
        if(left instanceof JAXBElement && ((JAXBElement<?>) left).isNil()){
            return null;
        }else{
            return super.mergeInternal(leftLocator, rightLocator, left, right);
        }
    }
}
