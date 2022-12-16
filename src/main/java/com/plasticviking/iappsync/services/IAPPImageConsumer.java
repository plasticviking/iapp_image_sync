package com.plasticviking.iappsync.services;

import com.plasticviking.iappsync.data.ShallowIAPPRecord;

@FunctionalInterface
public interface IAPPImageConsumer {
	void consume(ShallowIAPPRecord record);
}
