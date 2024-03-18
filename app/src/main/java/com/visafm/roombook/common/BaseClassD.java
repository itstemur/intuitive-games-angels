package com.visafm.roombook.common;

import android.content.Context;

public interface BaseClassD
{
	void httpFailure(String response, String requestedFor) throws Exception;
	void httpResponses(String response, String requestedFor, Context context) throws Exception;
}
