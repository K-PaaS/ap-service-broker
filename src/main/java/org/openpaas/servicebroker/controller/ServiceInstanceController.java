package org.openpaas.servicebroker.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.exception.ServiceDefinitionDoesNotExistException;
import org.openpaas.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.openpaas.servicebroker.exception.ServiceInstanceExistsException;
import org.openpaas.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.CreateServiceInstanceResponse;
import org.openpaas.servicebroker.model.DeleteServiceInstanceRequest;
import org.openpaas.servicebroker.model.ErrorMessage;
import org.openpaas.servicebroker.model.OperationState;
import org.openpaas.servicebroker.model.ServiceDefinition;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.openpaas.servicebroker.model.ServiceInstanceLastOperation;
import org.openpaas.servicebroker.model.UpdateServiceInstanceRequest;
import org.openpaas.servicebroker.service.CatalogService;
import org.openpaas.servicebroker.service.ServiceInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 서비스 인스턴스 관련 Provision/Undate Instance/Unprovision API 를 호출 받는 컨트롤러이다.
 * 
 * @author 송창학
 * @date 2015.0629
 */

@Controller
public class ServiceInstanceController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances";
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceController.class);
	
	@Autowired
	private ServiceInstanceService service;
	@Autowired
	private CatalogService catalogService;
	
	@Autowired
 	public ServiceInstanceController(ServiceInstanceService service, CatalogService catalogService) {
 		this.service = service;
 		this.catalogService = catalogService;
 	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PUT)
	public ResponseEntity<CreateServiceInstanceResponse> createServiceInstance(
			@PathVariable("instanceId") String serviceInstanceId, 
			@Valid @RequestBody CreateServiceInstanceRequest request) throws
			ServiceDefinitionDoesNotExistException,
			ServiceInstanceExistsException,
			ServiceBrokerException {
		logger.debug("PUT: " + BASE_PATH + "/{instanceId}" 
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);
		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());
		logger.debug("svc..........................");
		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}
		logger.debug("ServiceDefinitionDoesNotExistException");
		
		ServiceInstance instance = service.createServiceInstance(
				request.withServiceDefinition(svc).and().withServiceInstanceId(serviceInstanceId));
		
		logger.debug("ServiceInstance Created: " + instance.getServiceInstanceId());
		logger.info("Service Instance" + instance.toString());
		return new ResponseEntity(new CreateServiceInstanceResponse(instance), instance.isAsync() ? HttpStatus.ACCEPTED : HttpStatus.OK);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException {
		logger.info( "DELETE: " + BASE_PATH + "/{instanceId}"
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
		ServiceInstance instance = service.deleteServiceInstance(
				new DeleteServiceInstanceRequest(instanceId, serviceId, planId));
		if (instance == null) {
			return new ResponseEntity<String>("{}", HttpStatus.GONE);
		}
		logger.debug("ServiceInstance Deleted: " + instance.getServiceInstanceId());
        return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServiceInstance(
			@PathVariable("instanceId") String instanceId,
			@Valid @RequestBody UpdateServiceInstanceRequest request) throws 
			ServiceInstanceUpdateNotSupportedException,
			ServiceInstanceDoesNotExistException, 
			ServiceBrokerException {
		logger.debug("UPDATE: " + BASE_PATH + "/{instanceId}"
				+ ", updateServiceInstanceBinding(), serviceInstanceId = "
				+ instanceId + ", instanceId = " + instanceId + ", planId = "
				+ request.getPlanId());
		ServiceInstance instance = service.updateServiceInstance(request.withInstanceId(instanceId));
		logger.debug("ServiceInstance updated: " + instance.getServiceInstanceId());
		return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	@RequestMapping(value = BASE_PATH + "/{instanceId}/last_operation", method = RequestMethod.GET)
	public ResponseEntity<?> getServiceInstanceLastOperation(@PathVariable("instanceId") String instanceId) {
		logger.info("GET: /v2/service_instances/{instanceId}/last_operation, getServiceInstance(), serviceInstanceId = " + instanceId);
		ServiceInstance instance = this.service.getOperationServiceInstance(instanceId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (null == instance) {
			return new ResponseEntity("{}", headers, HttpStatus.GONE);
		} else {
			ServiceInstanceLastOperation lastOperation = new ServiceInstanceLastOperation("test", OperationState.SUCCEEDED);
			logger.info("ServiceInstance: " + instance.getServiceInstanceId() + " is in succeed state. Details : " + instance.getDashboardUrl());
			return new ResponseEntity(lastOperation, headers, HttpStatus.OK);
		}
	}
	
	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceDefinitionDoesNotExistException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseBody
	public ResponseEntity<String> handleException(
			ServiceInstanceExistsException ex, 
			HttpServletResponse response) {
	    return new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceInstanceUpdateNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceUpdateNotSupportedException ex,
			HttpServletResponse response) {
		return getErrorResponse(ex.getMessage(),
				HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
}
