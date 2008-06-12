/**
 * Copyright (c) 2000-2008 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.ipgeocoder.messaging;

import com.liferay.ipgeocoder.model.IPInfo;
import com.liferay.ipgeocoder.util.IPGeocoderUtil;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.util.JSONUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

/**
 * <a href="IPGeocoderMessageListener.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class IPGeocoderMessageListener implements MessageListener {

	public void receive(String message) {
		try {
			doReceive(message);
		}
		catch (Exception e) {
			_log.error("Unable to process message " + message, e);
		}
	}

	protected void doReceive(String message) throws Exception {
		JSONObject jsonObj = new JSONObject(message);

		String responseDestination = jsonObj.optString(
			"lfrResponseDestination");
		String responseId = jsonObj.optString("lfrResponseId");

		if (Validator.isNull(responseDestination) ||
			Validator.isNull(responseId)) {

			return;
		}

		String ipAddress = jsonObj.getString("ipAddress");

		IPInfo ipInfo = IPGeocoderUtil.getIPInfo(ipAddress);

		JSONObject jsonObject = new JSONObject();

		JSONUtil.put(jsonObject, "lfrResponseId", responseId);
		JSONUtil.put(
			jsonObject, "ipInfo", new JSONObject(JSONUtil.serialize(ipInfo)));

		MessageBusUtil.sendMessage(responseDestination, jsonObject.toString());
	}

	private static Log _log =
		LogFactory.getLog(IPGeocoderMessageListener.class);

}