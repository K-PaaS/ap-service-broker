package org.openpaas.servicebroker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 서비스 인스턴스 정보를 가지고 있는 데이터 모델 bean 클래스. 
 * Json 어노테이션을 사용해서 JSON 형태로 제공
 * 
 * @author 송창학
 * @date 2015.0629
 */

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceInstance {

	@JsonSerialize
	@JsonProperty("service_instance_id")
	private String serviceInstanceId;
	
	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;
	
	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;
	
	@JsonSerialize
	@JsonProperty("organization_guid")
	private String organizationGuid;
	
	@JsonSerialize
	@JsonProperty("space_guid")
	private String spaceGuid;
	
	@JsonSerialize
	@JsonProperty("dashboard_url")
	private String dashboardUrl;

	@JsonSerialize
	@JsonProperty("last_operation")
	private ServiceInstanceLastOperation lastOperation;

	@JsonIgnore
	private boolean async;

	public ServiceInstance() {}
	
	/**
	 * Create a ServiceInstance from a create request. If fields 
	 * are not present in the request they will remain null in the  
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(CreateServiceInstanceRequest request) {
		this.serviceDefinitionId = request.getServiceDefinitionId();
		this.planId = request.getPlanId();
		this.organizationGuid = request.getOrganizationGuid();
		this.spaceGuid = request.getSpaceGuid();
		this.serviceInstanceId = request.getServiceInstanceId();
		this.lastOperation = new ServiceInstanceLastOperation("Provisioning", OperationState.IN_PROGRESS);
	}
	
	/**
	 * Create a ServiceInstance from a delete request. If fields 
	 * are not present in the request they will remain null in the 
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(DeleteServiceInstanceRequest request) { 
		this.serviceInstanceId = request.getServiceInstanceId();
		this.planId = request.getPlanId();
		this.serviceDefinitionId = request.getServiceId();
		this.lastOperation = new ServiceInstanceLastOperation("Deprovisioning", OperationState.IN_PROGRESS);
	}
	
	/**
	 * Create a service instance from a delete request. If fields 
	 * are not present in the request they will remain null in the 
	 * ServiceInstance.
	 * @param request containing details of ServiceInstance
	 */
	public ServiceInstance(UpdateServiceInstanceRequest request) { 
		this.planId = request.getPlanId();
		this.serviceInstanceId = request.getServiceInstanceId();
		this.lastOperation = new ServiceInstanceLastOperation("Updating", OperationState.IN_PROGRESS);
	}
	
	public ServiceInstance withDashboardUrl(String dashboardUrl) { 
		this.dashboardUrl = dashboardUrl;
		return this;
	}
	
	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public boolean isAsync() {
		return this.async;
	}

	public ServiceInstance and() {
		return this;
	}

	public ServiceInstance withLastOperation(ServiceInstanceLastOperation lastOperation) {
		this.lastOperation = lastOperation;
		return this;
	}

	public ServiceInstance withAsync(boolean async) {
		this.async = async;
		return this;
	}

	public ServiceInstanceLastOperation getServiceInstanceLastOperation() {
		return this.lastOperation;
	}

	public String toString() {
		return "ServiceInstance{serviceInstanceId='" + this.serviceInstanceId + '\'' + ", serviceDefinitionId='" + this.serviceDefinitionId + '\'' + ", planId='" + this.planId + '\'' + ", organizationGuid='" + this.organizationGuid + '\'' + ", spaceGuid='" + this.spaceGuid + '\'' + ", dashboardUrl='" + this.dashboardUrl + '\'' + ", lastOperation=" + this.lastOperation + ", async=" + this.async + '}';
	}
}
