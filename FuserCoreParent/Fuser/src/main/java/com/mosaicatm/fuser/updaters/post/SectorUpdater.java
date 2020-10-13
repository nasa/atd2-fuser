package com.mosaicatm.fuser.updaters.post;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.sector.geometry.model.GeoPoint3D;
import com.mosaicatm.sector.geometry.model.ModuleFinder;
import com.mosaicatm.sector.geometry.utils.SectorUtils;
import com.mosaicatm.sector.geometry.model.Module;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.common.ObjectFactory;
import com.mosaicatm.matmdata.common.FavType;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightStateUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;

/**
 * This updater is responsible for updating the sector geometry criteria.
 */
public class SectorUpdater extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private static final long MISSING_DYNAMIC_SECTOR_WARNING_REPEAT_MILLIS = 5 * 60 * 1000L;
     
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final Map<String, Long> artccDynamicSectorMissingTime = new HashMap<>();
    private final Map<String, Long> dynamicSectorMissingTime = new HashMap<>();
    
    private ModuleFinder moduleFinder;
    private SectorAssignmentDataStore sectorAssignmentDataStore;

    @Override
    public void update( MatmFlight update, MatmFlight target )
    {
        if( !isActive() )
        {
            return;
        }

        if( update == null )
        {
            log.error("Cannot update sector data, null input.");
            return;
        }
        
        if( moduleFinder == null )
        {
            log.error("Cannot update sector data, moduleFinder is null!");
            return;
        }   
        
        if( sectorAssignmentDataStore == null )
        {
            log.error("Cannot update sector data, sectorAssignmentDataStore is null!");
            return;
        }        

        // Simple check to ignore most messages and skip processing
        if( possibleUpdate( update, target ))
        {
            updateSectorData( update.getGufi(), update.getPosition() );
        }
    }

    public void setModuleFinder( ModuleFinder moduleFinder )
    {
        this.moduleFinder = moduleFinder;
    }
    
    public void setSectorAssignmentDataStore( SectorAssignmentDataStore sectorAssignmentDataStore )
    {
        this.sectorAssignmentDataStore = sectorAssignmentDataStore;
    }    
    
    private boolean possibleUpdate( MatmFlight update, MatmFlight target )
    {     
        if(( update.getPosition() == null ) ||
                    ( update.getPosition().getLatitude() == null ) || 
                    ( update.getPosition().getLongitude() == null ) || 
                    ( update.getPosition().getAltitude() == null ))
        {
            return( false );
        }
                
        if( !MatmFlightStateUtil.isFlightAirborne( update ) &&
                !MatmFlightStateUtil.isFlightAirborne( target ))
        {
            return( false );
        }
        
        return( true );
    }

    private void updateSectorData( String gufi, Position position )
    {        
        position.setFav( objectFactory.createPositionFav( null ));
        position.setFavType( objectFactory.createPositionFavType( null ));
        position.setFavModule( objectFactory.createPositionFavModule( null ));
        position.setStaticSector( objectFactory.createPositionStaticSector( null ));
        position.setDynamicSector( objectFactory.createPositionDynamicSector( null ));
        
        List<Module> modulesList = moduleFinder.searchForModulesContaining( toGeoPoint( position ));
        
        if( log.isDebugEnabled() && ( modulesList.size() > 1 ))
        {
            StringBuilder sb = new StringBuilder();
            for( Module module : modulesList )
            {
                sb.append( SectorUtils.getModuleName( module ));
                sb.append( " " );
            }
                    
            log.debug( "Found {} modules containing point {} / {} / {}: {}",
                modulesList.size(), position.getLatitude(), position.getLongitude(), 
                position.getAltitude(), sb.toString() );
        }

        if( modulesList.isEmpty() )
        {
            // TODO: might be worth making this a warning if we're in CONUS and above 18k feet
            log.debug( "Unable to find FAV module for gufi {} at point {},{},{}",
                    gufi, position.getLatitude(), position.getLongitude(), position.getAltitude() );
        }
        else
        {
            Module firstModule = modulesList.get( 0 );
            
            position.getFavModule().setValue( SectorUtils.getModuleName( firstModule ));
            position.getFav().setValue( SectorUtils.getFavName( firstModule ));
            
            FavType favType = null;
            if( firstModule.getFavType() != null )
            {
                favType = FavType.valueOf( firstModule.getFavType().toString() );
            }            
            position.getFavType().setValue( favType );
            
            position.getStaticSector().setValue( SectorUtils.getSectorName( firstModule ));
            position.getDynamicSector().setValue( 
                    sectorAssignmentDataStore.getDynamicSectorForModule( position.getFavModule().getValue() ));
            
            if( position.getDynamicSector().isNil() && !position.getStaticSector().isNil() )
            {
                String artcc = SectorUtils.getArtcc( position.getFavModule().getValue() );
                
                // The sectorization data exists
                if( sectorAssignmentDataStore.dynamicSectorAssignmentsExist( artcc ))
                {
                    //Approach sectors don't always appear in the dynamic sector messages
                    if( Objects.equals( FavType.APPROACH, favType ))
                    {
                        log.debug( "Dynamic sector assignments exist for ARTCC {}, but no dynamically assigned sector defined for " +
                                "approach sector module {}. This is not necessarily a problem. FAV: {}, StaticSector: {}. Flight: {}",
                                artcc, position.getFavModule().getValue(), position.getFav().getValue(), 
                                position.getStaticSector().getValue(), gufi );    
                        
                        position.getDynamicSector().setValue( position.getStaticSector().getValue() );
                    }
                    else                    
                    {
                        Long lastLogTime = dynamicSectorMissingTime.get( position.getFav().getValue() );
                        
                        if(( lastLogTime == null ) || 
                                (( System.currentTimeMillis() - lastLogTime ) > MISSING_DYNAMIC_SECTOR_WARNING_REPEAT_MILLIS ))
                        {           
                            log.warn( "Dynamic sector assignments exist for ARTCC {}, but no dynamically assigned sector defined for " +
                                    "sector module: {}, FAV: {}, StaticSector: {}. Flight: {}. Is adaptation current?",
                                    artcc, position.getFavModule().getValue(), position.getFav().getValue(), 
                                    position.getStaticSector().getValue(), gufi );      
                            
                            dynamicSectorMissingTime.put( position.getFav().getValue(), System.currentTimeMillis() );
                        }
                    }
                }
                else
                {
                    Long lastLogTime = artccDynamicSectorMissingTime.get( artcc );
                    
                    if(( lastLogTime == null ) || 
                            (( System.currentTimeMillis() - lastLogTime ) > MISSING_DYNAMIC_SECTOR_WARNING_REPEAT_MILLIS ))
                    {
                        log.warn( "Missing dynamic sector assignments for ARTCC {}. Example sector module: {}, FAV: {}, StaticSector: {}. " +
                                "Log warning period {} seconds.", 
                                artcc, position.getFavModule().getValue(), position.getFav().getValue(), 
                                position.getStaticSector().getValue(), ( MISSING_DYNAMIC_SECTOR_WARNING_REPEAT_MILLIS / 1000 ));
                        artccDynamicSectorMissingTime.put( artcc, System.currentTimeMillis() );
                    }                    
                }
            }                      
        }
    }
    
    private GeoPoint3D toGeoPoint( Position position )
    {
        return new GeoPoint3D(
                position.getLongitude(),
                position.getLatitude(),
                position.getAltitude() );
    }    
}
