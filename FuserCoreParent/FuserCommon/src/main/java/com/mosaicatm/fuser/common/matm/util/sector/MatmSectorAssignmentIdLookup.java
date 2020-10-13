package com.mosaicatm.fuser.common.matm.util.sector;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class MatmSectorAssignmentIdLookup
implements MatmIdLookup<MatmSectorAssignment, String>
{
    @Override
    public String getIdentifier(MatmSectorAssignment data)
    {
        if (data != null)
            return data.getSectorName();
        return null;
    }
}
