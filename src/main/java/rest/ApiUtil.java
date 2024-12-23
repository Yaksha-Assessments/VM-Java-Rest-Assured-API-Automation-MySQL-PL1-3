package rest;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiUtil {

	private static final String BASE_URL = "https://healthapp.yaksha.com/api";

	/**
	 * @Test1 This method retrieves and verifies the list of departments.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of departments in the "Results" field, containing
	 *         details such as DepartmentId and DepartmentName.
	 */
	public CustomResponse getAllDepartments(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test2 This method retrieves and verifies the list of items.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of items in the "Results" field, containing
	 *         details such as ItemId and ItemName.
	 */
	public CustomResponse getAllItems(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test3 This method retrieves and verifies the incentive summary report.
	 * 
	 * @param URL  - The API endpoint to which the GET request is sent.
	 * @param body - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of incentive summary details in the "JsonData"
	 *         field, containing details such as PrescriberName, PrescriberId,
	 *         DocTotalAmount, TDSAmount, and NetPayableAmount.
	 */
	public CustomResponse getIncentiveSummaryReport(String URL, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		System.out.println(URL);
		Response response = request.get(URL).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		System.out.println(response.prettyPrint());
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("JsonData");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test4 This method retrieves and verifies the incentive referral summary
	 *        report.
	 * 
	 * @param URL  - The API endpoint to which the GET request is sent.
	 * @param body - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of incentive referral summary details in the
	 *         "JsonData" field, containing details such as PrescriberName,
	 *         PrescriberId, DocTotalAmount, TDSAmount, and NetPayableAmount.
	 */
	public CustomResponse getIncReffSummReport(String URL, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send the GET request and extract the response
		Response response = request.get(URL).then().extract().response();
		System.out.println(response.prettyPrint());

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Get the JsonData as a String, which contains a JSON array
		String jsonDataString = response.jsonPath().getString("Results.JsonData");

		// Initialize an empty list to hold the parsed results
		List<Map<String, Object>> results = null;

		// Parse the JsonData string into a List<Map<String, Object>> if it's not null
		if (jsonDataString != null && !jsonDataString.isEmpty()) {
			try {
				// Using ObjectMapper to parse the JSON string into a List of Maps
				ObjectMapper objectMapper = new ObjectMapper();
				results = objectMapper.readValue(jsonDataString, new TypeReference<List<Map<String, Object>>>() {
				});
			} catch (Exception e) {
				// Handle parsing error
				System.out.println("Error parsing JsonData: " + e.getMessage());
			}
		}

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test5 This method fetches the hospital income incentive report for a
	 *        specified date range and service department from the API. It validates
	 *        the response status code, checks the "Status" field, and extracts
	 *        fields such as ServiceDepartmentId, ServiceDepartmentName, NetSales,
	 *        ReferralCommission, GrossIncome, OtherIncentive, and HospitalNetIncome
	 *        from the "Results" array.
	 *
	 * @param endpoint - The API endpoint for fetching the hospital income incentive
	 *                 report.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and details such as ServiceDepartmentId,
	 *         ServiceDepartmentName, NetSales, ReferralCommission, GrossIncome,
	 *         OtherIncentive, and HospitalNetIncome in the "Results" field.
	 */
	public CustomResponse getHospIncIncReport(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(endpoint).then().extract().response();
		System.out.println(response.prettyPrint());

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test6 This method fetches employee bill items for a specific employee from
	 *        the API. It validates the response status, checks the "Status" field,
	 *        and extracts fields like EmployeeIncentiveInfoId, EmployeeId,
	 *        FullName, TDSPercent, EmpTDSPercent, IsActive, and
	 *        EmployeeBillItemsMap for validation.
	 *
	 * @param endpoint - The API endpoint for fetching employee bill items.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and details such as EmployeeIncentiveInfoId,
	 *         EmployeeId, FullName, TDSPercent, EmpTDSPercent, IsActive, and
	 *         EmployeeBillItemsMap in the "Results" field.
	 */
	public CustomResponse getEmpBillItem(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();
		System.out.println(response.prettyPrint());

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		Map<String, Object> results = response.jsonPath().getMap("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test7 This method fetches inventory fiscal years from the API and validates
	 *        various fields. It checks the fiscal year ID, name, start date, end
	 *        date, and active status for each fiscal year.
	 *
	 * @param endpoint - The API endpoint for retrieving inventory fiscal years.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and details such as FiscalYearId, FiscalYearName,
	 *         StartDate, EndDate, and IsActive in the "Results" field.
	 */
	public CustomResponse getInvntryFiscalYrs(String endpoint, Object body) {
		// Set up the request
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send the request and get the response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extract required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Fetch results as a List of Maps (representing the fiscal years)
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object with all the necessary details
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test8 This method activates inventory by retrieving store information from
	 *        the API. It fetches a list of stores and validates each store's ID,
	 *        name, and description.
	 *
	 * @param endpoint - The API endpoint for activating inventory.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of stores in the "Results" field.
	 */
	public CustomResponse getActInventory(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test9 This method retrieves and validates the subcategories of inventory
	 *        from the API. It fetches the list of subcategories and validates each
	 *        subcategory's ID and name.
	 *
	 * @param endpoint - The API endpoint that provides the subcategories
	 *                 information.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of subcategories in the "Results" field.
	 */
	public CustomResponse getInvSubCat(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test10 This method retrieves and validates the available quantity of an item
	 *         in a specific store based on the provided item ID and store ID.
	 *
	 * @param endpoint - The API endpoint that provides the available quantity of an
	 *                 item by item ID and store ID.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and details such as ItemID, StoreID, and
	 *         AvailableQuantity in the "Results" field.
	 */
	public CustomResponse getAvlQtyByStoreId(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		Map<String, Object> results = response.jsonPath().getMap("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test11 This method retrieves and validates the requisition and its
	 *         associated items based on a specific requisition ID.
	 *
	 * @param endpoint - The API endpoint that provides the requisition and its
	 *                 items by ID.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         requisition details, and a list of requisition items in the "Results"
	 *         field.
	 */
	public CustomResponse getReqItemsById(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		Map<String, Object> results = response.jsonPath().getMap("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test12 This method retrieves and validates the fields for a specific
	 *         requisition and its associated dispatchers.
	 * 
	 * @param endpoint - The API endpoint containing the requisition ID for fetching
	 *                 its details.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, requisition details such as "CreatedBy", "Status",
	 *         and a list of dispatchers in the "Results" field.
	 */
	public CustomResponse trackReqItemById(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		
		// Extract "Results" as a Map, since it's not a list
	    Map<String, Object> results = response.jsonPath().getMap("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test13 This method retrieves and validates the inventory items for a
	 *         specific store ID.
	 * 
	 * @param endpoint - The API endpoint containing the store ID for fetching
	 *                 inventory items.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of inventory items in the "Results" field.
	 */
	public CustomResponse getInvItem(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test14 This method retrieves and validates the most sold medicines from the
	 *         Pharmacy Dashboard.
	 * 
	 * @param endpoint - The API endpoint with query parameters specifying the date
	 *                 range for fetching data.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of most sold medicines in the "Results"
	 *         field.
	 */
	public CustomResponse getMostSoldMed(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test15 This method retrieves and validates the substore-wise dispatch values
	 *         from the Pharmacy Dashboard.
	 * 
	 * @param endpoint - The API endpoint with query parameters specifying the date
	 *                 range for fetching data.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of substore-wise dispatch values in the
	 *         "Results" field.
	 */
	public CustomResponse getSubDisp(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test16 This method retrieves and validates the list of Active Suppliers.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of active suppliers in the "Results"
	 *         field.
	 */
	public CustomResponse getActSupp(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test17 This method retrieves and validates the list of Units of Measurement.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of measurement units in the "Results"
	 *         field.
	 */
	public CustomResponse getMeasureUnits(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test18 This method retrieves and validates the list of Sales Categories.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of sales categories in the "Results"
	 *         field.
	 */
	public CustomResponse getSalesCat(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, results);
	}
}