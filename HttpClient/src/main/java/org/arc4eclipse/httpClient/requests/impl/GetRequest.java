package org.arc4eclipse.httpClient.requests.impl;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.impl.Response;
import org.arc4eclipse.utilities.exceptions.WrappedException;

public class GetRequest extends AbstractRequestBuilder {

	public GetRequest(HttpHost host, HttpClient client, String url) {
		super(host, client, url);
	}

	
	public <Context1, Context2> void execute(Context1 context1, Context2 context2, IResponseCallback<Context1, Context2> callback) {
		try {
			String protocolHostAndUrl = protocolHostAndUrl();
			HttpGet get = new HttpGet(protocolHostAndUrl);
			HttpResponse httpResponse = client.execute(get);
			Response response = new Response(httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(httpResponse.getEntity()));
			callback.process(context1, context2, response);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}