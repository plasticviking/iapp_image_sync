package com.plasticviking.iappsync.data;

import java.util.Optional;

public record ShallowIAPPRecord(long imageID,
																Optional<Long> siteID,
																Optional<Long> samplePointID,
																Optional<Long> treatmentID) {
}


