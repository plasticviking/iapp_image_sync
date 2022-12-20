package com.plasticviking.iappsync.data;

import java.sql.Date;

public record ShallowIAPPRecord(Long imageID,
																Long siteID,
																Long samplePointID,
																Long treatmentID,
																String perspectiveCode,
																Long revisionCount,
																String comments,
																String referenceNo,
																Date imageDate) {
}


