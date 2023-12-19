package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.model.table.AndEventsInformation;

import java.util.List;

public interface EventParser {
    AndEventsInformation emptyEventsInformationFactory();

    void praserInformationInTables(VRM vrm, List<TableRow> tableRows, AndEventsInformation ei);
}
