package org.arc4eclipse.httpAccess.api.impl;

import junit.framework.TestCase;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.arc4eclipse.httpClient.api.IClientBuilder;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.api.impl.ClientBuilder;
import org.arc4eclipse.httpClient.api.impl.PreemptiveAuthInterceptor;
import org.arc4eclipse.httpClient.constants.HttpClientConstants;

public class ClientBuilderTest extends TestCase {

	public void testUtilsMethodsMakesWithHostAndPort() {
		checkHostAndPort(IHttpClient.Utils.builder(), HttpClientConstants.defaultHost, HttpClientConstants.defaultPort);
		checkHostAndPort(IHttpClient.Utils.builder("host", 111), "host", 111);
		checkUserNameAndPassword(IHttpClient.Utils.builder(), null, null);
		checkUserNameAndPassword(IHttpClient.Utils.builder("host", 111), null, null);
	}

	public void testWithCredentials() {
		checkUserNameAndPassword(IHttpClient.Utils.builder().withCredentials("user", "pass"), "user", "pass");
	}

	private void checkUserNameAndPassword(IClientBuilder builder, String userName, String password) {
		boolean expectingNull = userName == null && password == null;
		ClientBuilder clientBuilder = (ClientBuilder) builder;
		AuthScope authscope = clientBuilder.makeAuthScope();
		Credentials credentials = clientBuilder.client.getCredentialsProvider().getCredentials(authscope);
		if (expectingNull)
			assertNull(credentials);
		else {
			assertEquals(userName, credentials.getUserPrincipal().getName());
			assertEquals(password, credentials.getPassword());
		}
	}

	private void checkHostAndPort(IClientBuilder builder, String hostname, int port) {
		HttpHost host = ((ClientBuilder) builder).host;
		assertEquals(hostname, host.getHostName());
		assertEquals(port, host.getPort());
		DefaultHttpClient client = ((ClientBuilder) builder).client;
		assertNotNull(client);
		assertTrue(client.getRequestInterceptor(0) instanceof PreemptiveAuthInterceptor);
	}

}
