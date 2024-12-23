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

	public CustomResponse(Object obj, Object obj1, Object obj2, Object obj3) {
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
}
