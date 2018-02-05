package org.openpaas.servicebroker.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openpaas.servicebroker.exception.ServiceBrokerApiVersionException;
import org.openpaas.servicebroker.model.BrokerApiVersion;
import org.slf4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 서비스 브로커 버전 체크 하는 interceptor 클래스 . 
 * spring 의 HandlerInterceptorAdapter 클래스를 상속함.
 * 
 * @author 송창학
 * @date 2015.0629
 */

public class BrokerApiVersionInterceptor extends HandlerInterceptorAdapter {

	private final BrokerApiVersion version;

	public BrokerApiVersionInterceptor() {
		this(null);
	}

	private static final Logger LOGGER = getLogger(BrokerApiVersionInterceptor.class);
	public BrokerApiVersionInterceptor(BrokerApiVersion version) {
		this.version = version;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws ServiceBrokerApiVersionException {
		if (version != null && !anyVersionAllowed()) {
			String apiVersion = request.getHeader(version.getBrokerApiVersionHeader());
			boolean contains = false;
			for (String brokerApiVersion : version.getApiVersions().split(", ")) {
				if(brokerApiVersion.contains(".") &&  apiVersion.contains(".")){
					if("x".equals(brokerApiVersion.split("[.]")[1]) && apiVersion.split("[.]")[0].equals(brokerApiVersion.split("[.]")[0])){
						contains = true;
						break;
					}
				}
				if (brokerApiVersion.equals(apiVersion)){
					contains = true;
					break;
				}
			}
			if (!contains) {
				throw new ServiceBrokerApiVersionException(version.getApiVersions(), apiVersion);
			}
			 
		}
		return true;
	}

	private boolean anyVersionAllowed() {
		return BrokerApiVersion.API_VERSION_ANY.equals(version.getApiVersions());
	}

}
