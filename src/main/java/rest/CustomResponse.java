package rest;

import java.util.List;
import java.util.Map;

import io.restassured.response.Response;

public class CustomResponse {
	private Response response;
	private int statusCode;
	private String status;
	private Integer appointmentId;
	private List<Map<String, Object>> listResults;
	private String resultMessage;
	private Map<String, Object> mapResults;
	private List<Object> itemIds;
	private List<Object> itemNames;
	private List<Object> genericNames;
	private Object storeId;
	private Object category;
	private Object isActive;
	private Object patientId;
	private Object totalDue;
	private List<Object> patientIds;
	private List<Object> patientCodes;
	private List<Object> prescriberIds;
	private List<Object> prescriberNames;
	private List<Object> docTotalAmounts;
	private List<Object> tdsAmounts;
	private List<Object> netPayableAmounts;
	private List<Object> serviceDepartmentIds;
	private Object employeeIncentiveInfoId;
	private Object employeeId;
	private Object fullName;
	private Object tdsPercent;
	private Object empTdsPercent;
	private List<Map<String, Object>> employeeBillItemsMap;
	private Object requisitionNo;
	private Object createdByName;
	private Object requisitionStatus;
	private List<Map<String, Object>> requisitionItems;

	public Object getRequisitionNo() {
		return requisitionNo;
	}

	public void setRequisitionNo(Object requisitionNo) {
		this.requisitionNo = requisitionNo;
	}

	public Object getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(Object createdByName) {
		this.createdByName = createdByName;
	}

	public Object getRequisitionStatus() {
		return requisitionStatus;
	}

	public void setRequisitionStatus(Object requisitionStatus) {
		this.requisitionStatus = requisitionStatus;
	}

	public List<Map<String, Object>> getRequisitionItems() {
		return requisitionItems;
	}

	public void setRequisitionItems(List<Map<String, Object>> requisitionItems) {
		this.requisitionItems = requisitionItems;
	}

	public Object getFullName() {
		return fullName;
	}

	public void setFullName(Object fullName) {
		this.fullName = fullName;
	}

	public Object getEmployeeIncentiveInfoId() {
		return employeeIncentiveInfoId;
	}

	public void setEmployeeIncentiveInfoId(Object employeeIncentiveInfoId) {
		this.employeeIncentiveInfoId = employeeIncentiveInfoId;
	}

	public Object getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Object employeeId) {
		this.employeeId = employeeId;
	}

	public Object getTdsPercent() {
		return tdsPercent;
	}

	public void setTdsPercent(Object tdsPercent) {
		this.tdsPercent = tdsPercent;
	}

	public Object getEmpTdsPercent() {
		return empTdsPercent;
	}

	public void setEmpTdsPercent(Object empTdsPercent) {
		this.empTdsPercent = empTdsPercent;
	}

	public List<Map<String, Object>> getEmployeeBillItemsMap() {
		return employeeBillItemsMap;
	}

	public void setEmployeeBillItemsMap(List<Map<String, Object>> employeeBillItemsMap) {
		this.employeeBillItemsMap = employeeBillItemsMap;
	}

	public List<Object> getServiceDepartmentIds() {
		return serviceDepartmentIds;
	}

	public void setServiceDepartmentIds(List<Object> serviceDepartmentIds) {
		this.serviceDepartmentIds = serviceDepartmentIds;
	}

	public List<Object> getServiceDepartmentNames() {
		return serviceDepartmentNames;
	}

	public void setServiceDepartmentNames(List<Object> serviceDepartmentNames) {
		this.serviceDepartmentNames = serviceDepartmentNames;
	}

	public List<Object> getNetSales() {
		return netSales;
	}

	public void setNetSales(List<Object> netSales) {
		this.netSales = netSales;
	}

	public List<Object> getReferralCommissions() {
		return referralCommissions;
	}

	public void setReferralCommissions(List<Object> referralCommissions) {
		this.referralCommissions = referralCommissions;
	}

	public List<Object> getGrossIncomes() {
		return grossIncomes;
	}

	public void setGrossIncomes(List<Object> grossIncomes) {
		this.grossIncomes = grossIncomes;
	}

	public List<Object> getOtherIncentives() {
		return otherIncentives;
	}

	public void setOtherIncentives(List<Object> otherIncentives) {
		this.otherIncentives = otherIncentives;
	}

	public List<Object> getHospitalNetIncomes() {
		return hospitalNetIncomes;
	}

	public void setHospitalNetIncomes(List<Object> hospitalNetIncomes) {
		this.hospitalNetIncomes = hospitalNetIncomes;
	}

	private List<Object> serviceDepartmentNames;
	private List<Object> netSales;
	private List<Object> referralCommissions;
	private List<Object> grossIncomes;
	private List<Object> otherIncentives;
	private List<Object> hospitalNetIncomes;

	public CustomResponse(Response response, int statusCode, String status, Integer appointmentId) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.appointmentId = appointmentId;
	}

	public CustomResponse(Response response, int statusCode, String status, Map<String, Object> mapResults) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.mapResults = mapResults;
	}

	public CustomResponse(Response response, int statusCode, String status, String resultMessage) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.resultMessage = resultMessage;
	}

	public CustomResponse(Response response, int statusCode, String status, List<Map<String, Object>> listResults) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.listResults = listResults;
	}

	public CustomResponse(Response response, int statusCode, String status, List<Object> patientIds,
			List<Object> patientCodes) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.patientIds = patientIds;
		this.patientCodes = patientCodes;
	}

	public CustomResponse(Response response, int statusCode, String status, List<Object> itemIds,
			List<Object> itemNames, List<Object> genericNames) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.itemIds = itemIds;
		this.itemNames = itemNames;
		this.genericNames = genericNames;
	}

	public CustomResponse(Response response, int statusCode, String status, Object storeId, Object category,
			Object isActive) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.storeId = storeId;
		this.category = category;
		this.isActive = isActive;
	}

	public CustomResponse(Response response, int statusCode, String status, Object patientId, Object totalDue) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.patientId = patientId;
		this.totalDue = totalDue;
	}

	public CustomResponse(Response response, int statusCode, String status, List<Object> prescriberIds,
			List<Object> prescriberNames, List<Object> docTotalAmounts, List<Object> tdsAmounts,
			List<Object> netPayableAmounts) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.prescriberIds = prescriberIds;
		this.prescriberNames = prescriberNames;
		this.docTotalAmounts = docTotalAmounts;
		this.tdsAmounts = tdsAmounts;
		this.netPayableAmounts = netPayableAmounts;
	}

	public CustomResponse(Response response, int statusCode, String status, List<Object> serviceDepartmentIds,
			List<Object> serviceDepartmentNames, List<Object> netSales, List<Object> referralCommissions,
			List<Object> grossIncomes, List<Object> otherIncentives, List<Object> hospitalNetIncomes) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.serviceDepartmentIds = serviceDepartmentIds;
		this.serviceDepartmentNames = serviceDepartmentNames;
		this.netSales = netSales;
		this.referralCommissions = referralCommissions;
		this.grossIncomes = grossIncomes;
		this.otherIncentives = otherIncentives;
		this.hospitalNetIncomes = hospitalNetIncomes;
	}

	public CustomResponse(Response response, int statusCode, String status, Object employeeIncentiveInfoId,
			Object employeeId, Object fullName, Object tdsPercent, Object empTdsPercent, Object isActive,
			List<Map<String, Object>> employeeBillItemsMap) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.employeeIncentiveInfoId = employeeIncentiveInfoId;
		this.employeeId = employeeId;
		this.fullName = fullName;
		this.tdsPercent = tdsPercent;
		this.empTdsPercent = empTdsPercent;
		this.isActive = isActive;
		this.employeeBillItemsMap = employeeBillItemsMap;
	}

	public CustomResponse(Response response, int statusCode, String status, Object requisitionNo, Object createdByName,
			Object requisitionStatus, List<Map<String, Object>> requisitionItems) {
		this.response = response;
		this.statusCode = statusCode;
		this.status = status;
		this.requisitionNo = requisitionNo;
		this.createdByName = createdByName;
		this.requisitionStatus = requisitionStatus;
		this.requisitionItems = requisitionItems;
	}

	public Response getResponse() {
		return response;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatus() {
		return status;
	}

	public Integer getAppointmentId() {
		return appointmentId;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public List<Map<String, Object>> getListResults() {
		return listResults;
	}

	public void setListResults(List<Map<String, Object>> listResults) {
		this.listResults = listResults;
	}

	public Map<String, Object> getMapResults() {
		return mapResults;
	}

	public void setMapResults(Map<String, Object> mapResults) {
		this.mapResults = mapResults;
	}

	public List<Object> getItemIds() {
		return itemIds;
	}

	public void setItemIds(List<Object> itemIds) {
		this.itemIds = itemIds;
	}

	public List<Object> getItemNames() {
		return itemNames;
	}

	public void setItemNames(List<Object> itemNames) {
		this.itemNames = itemNames;
	}

	public List<Object> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<Object> genericNames) {
		this.genericNames = genericNames;
	}

	public Object getStoreId() {
		return storeId;
	}

	public void setStoreId(Object storeId) {
		this.storeId = storeId;
	}

	public Object getCategory() {
		return category;
	}

	public void setCategory(Object category) {
		this.category = category;
	}

	public Object getIsActive() {
		return isActive;
	}

	public void setIsActive(Object isActive) {
		this.isActive = isActive;
	}

	public Object getPatientId() {
		return patientId;
	}

	public void setPatientId(Object patientId) {
		this.patientId = patientId;
	}

	public Object getTotalDue() {
		return totalDue;
	}

	public void setTotalDue(Object totalDue) {
		this.totalDue = totalDue;
	}

	public List<Object> getPatientIds() {
		return patientIds;
	}

	public void setPatientIds(List<Object> patientIds) {
		this.patientIds = patientIds;
	}

	public List<Object> getPatientCodes() {
		return patientCodes;
	}

	public void setPatientCodes(List<Object> patientCodes) {
		this.patientCodes = patientCodes;
	}

	public List<Object> getPrescriberIds() {
		return prescriberIds;
	}

	public void setPrescriberIds(List<Object> prescriberIds) {
		this.prescriberIds = prescriberIds;
	}

	public List<Object> getPrescriberNames() {
		return prescriberNames;
	}

	public void setPrescriberNames(List<Object> prescriberNames) {
		this.prescriberNames = prescriberNames;
	}

	public List<Object> getDocTotalAmounts() {
		return docTotalAmounts;
	}

	public void setDocTotalAmounts(List<Object> docTotalAmounts) {
		this.docTotalAmounts = docTotalAmounts;
	}

	public List<Object> getTdsAmounts() {
		return tdsAmounts;
	}

	public void setTdsAmounts(List<Object> tdsAmounts) {
		this.tdsAmounts = tdsAmounts;
	}

	public List<Object> getNetPayableAmounts() {
		return netPayableAmounts;
	}

	public void setNetPayableAmounts(List<Object> netPayableAmounts) {
		this.netPayableAmounts = netPayableAmounts;
	}
}
