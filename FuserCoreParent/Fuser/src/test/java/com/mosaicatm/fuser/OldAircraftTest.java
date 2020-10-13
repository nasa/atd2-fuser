package com.mosaicatm.fuser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Ignore;
import org.junit.Test;

import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class OldAircraftTest
{
    @Ignore
    @Test
    public void testFindOldestAircraft()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        
        MatmAircraft earliestAircraft = null;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("C:/Dump/ATDI-5434/panthro_aircraft2.xml")))
        {
            GenericMarshaller gm = new GenericMarshaller(MatmAircraft.class);
            
            int count = 0;
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                ++count;
                
                // garbage from redis
                if (count %2 == 1)
                    continue;
                
                MatmAircraft aircraft = gm.unmarshallToObject(line);
                
                if (aircraft != null && aircraft.getTimestamp()!= null)
                {
                    if (earliestAircraft == null ||
                        aircraft.getTimestamp().before(earliestAircraft.getTimestamp()))
                    {
                        earliestAircraft = aircraft;
                    }
                }
                else
                {
                    System.out.println("Aircraft didn't have timestamp");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        System.out.println("earliest timestamp " + earliestAircraft.getAddress() + ", " +
                earliestAircraft.getRegistration() + ", " + earliestAircraft.getType() + ", " + earliestAircraft.getTimestamp());
    }
    
    @Ignore
    @Test
    public void testListOldAircraft()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        
        List<MatmAircraft> allAircraft = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader("C:/Dump/ATDI-5434/panthro_aircraft2.xml")))
        {
            GenericMarshaller gm = new GenericMarshaller(MatmAircraft.class);
            
            int count = 0;
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                ++count;
                
                // garbage from redis
                if (count %2 == 1)
                    continue;
                
                allAircraft.add(gm.unmarshallToObject(line));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        Collections.sort(allAircraft, new Comparator<MatmAircraft>() {

            @Override
            public int compare(MatmAircraft o1, MatmAircraft o2)
            {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
            
        });
        
        int count = 0;
        Date when = new Date(System.currentTimeMillis() - TimeFactory.DAY_IN_MILLIS);
        for (MatmAircraft aircraft : allAircraft)
        {
            if (aircraft.getTimestamp().before(when))
                ++count;
        }
        
        System.out.println(count + "/" + allAircraft.size());
        
        /*int max = 100;
        for (int i = 0; i < max; ++i)
        {
            MatmAircraft aircraft = allAircraft.get(i);
            System.out.println(aircraft.getRegistration() + ", " + aircraft.getTimestamp());
        }*/
    }
}
