package com.plasticviking.iappsync.services;

import com.plasticviking.iappsync.data.ShallowIAPPRecord;

import java.sql.Blob;

@FunctionalInterface
public interface IAPPImageConsumer {
	void consume(ShallowIAPPRecord record, Blob imageData);
}
