package com.visafm.common;

import android.content.Context;

public interface BaseClass
{
	void httpResponse(String response, String requestedFor) throws Exception;
	void httpFailure(String response, String requestedFor) throws Exception;
}
