package com.nuaa.art.vrmcheck.service.obj;

import com.nuaa.art.vrm.model.TableRow;
import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrmcheck.model.obj.EventsInformation;

import java.util.List;

public interface EventPraser {
    EventsInformation emptyEventsInformationFactory();

    void praserInformationInTables(VariableRealationModel vrm, List<TableRow> tableRows, EventsInformation ei);
}
