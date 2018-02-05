package org.openpaas.servicebroker.interceptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.servicebroker.exception.ServiceBrokerApiVersionException;
import org.openpaas.servicebroker.model.BrokerApiVersion;

public class BrokerApiVersionInterceptorTest {

	@Mock
	private HttpServletRequest request;
	
	@Mock 
	private HttpServletResponse response;
	
	@Mock
	private BrokerApiVersion brokerApiVersion;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void noBrokerApiVersionConfigured() throws IOException, ServletException, ServiceBrokerApiVersionException {
		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(null);
		assertTrue(interceptor.preHandle(request, response, null));
	}

	@Test
	public void anyVersionAccepted() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = BrokerApiVersion.API_VERSION_ANY;
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersions()).thenReturn(version);
		when(request.getHeader(header)).thenReturn("version");
		
		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersions();
	}

	@Test
	public void versionsMatch() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "version";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersions()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(version);

		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersions();
	}

	@Test
	public void versionsMatch_version_x() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String expectedVersion = "2.x";
		String apiVersion = "2.111";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersions()).thenReturn(expectedVersion);
		when(request.getHeader(header)).thenReturn(apiVersion);

		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(brokerApiVersion);
		assertTrue(interceptor.preHandle(request, response, null));
		verify(brokerApiVersion, atLeastOnce()).getApiVersions();
	}



	@Test(expected = ServiceBrokerApiVersionException.class)
	public void versionMismatch() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "version";
		String notVersion = "not_version";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersions()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(notVersion);
		
		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(brokerApiVersion);
		interceptor.preHandle(request, response, null);
		verify(brokerApiVersion).getBrokerApiVersionHeader();
		verify(brokerApiVersion).getApiVersions();
	}

	@Test(expected = ServiceBrokerApiVersionException.class)
	public void versionMismatch_version_x() throws IOException, ServletException, ServiceBrokerApiVersionException {
		String header = "header";
		String version = "2.x, 3.x";
		String notVersion = "4.5";
		when(brokerApiVersion.getBrokerApiVersionHeader()).thenReturn(header);
		when(brokerApiVersion.getApiVersions()).thenReturn(version);
		when(request.getHeader(header)).thenReturn(notVersion);

		BrokerApiVersionInterceptor interceptor = new BrokerApiVersionInterceptor(brokerApiVersion);
		interceptor.preHandle(request, response, null);
		verify(brokerApiVersion).getBrokerApiVersionHeader();
		verify(brokerApiVersion).getApiVersions();
	}
	
}
