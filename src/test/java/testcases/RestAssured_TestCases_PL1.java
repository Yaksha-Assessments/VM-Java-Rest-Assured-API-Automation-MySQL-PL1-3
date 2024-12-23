package testcases;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import coreUtilities.utils.FileOperations;
import rest.ApiUtil;
import rest.CustomResponse;

public class RestAssured_TestCases_PL1 {

	FileOperations fileOperations = new FileOperations();

	private final String EXCEL_FILE_PATH = "src/main/resources/config.xlsx"; // Path to the Excel file
	private final String SHEET_NAME = "PostData"; // Sheet name in the Excel file
	private final String FILEPATH = "src/main/java/rest/ApiUtil.java";
	ApiUtil apiUtil;

	public static int appointmentId;

	@Test(priority = 1, groups = { "PL1" }, description = "1. Send a GET request to Get All Departments\n"
			+ "2. Validate that all the DepartmentId and DepartmentName are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getAllDepartmentsTest() throws Exception {
		apiUtil = new ApiUtil();

		// Send GET request
		CustomResponse customResponse = apiUtil.getAllDepartments("/AssetReports/GetAllDepartments", null);

		// Validate the implementation of getAllDepartments
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAllDepartments",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getAllDepartments must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getAllDepartments", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that each department entry has non-null fields
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertFalse(results.isEmpty(), "Results should not be empty.");
		results.forEach(department -> {
			Assert.assertNotNull(department.get("DepartmentId"), "DepartmentId should not be null.");
			Assert.assertNotNull(department.get("DepartmentName"), "DepartmentName should not be null.");
		});

		// Print response for debugging
		System.out.println("All Departments Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 2, groups = { "PL1" }, description = "1. Send a GET request to Get All Items\n"
			+ "2. Validate that the Item Id and Item name are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getAllItemsTest() throws IOException {
		apiUtil = new ApiUtil();

		// Send GET request and receive response
		CustomResponse customResponse = apiUtil.getAllItems("/AssetReports/GetAllItems", null);

		// Validate the implementation of getAllItems
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAllItems",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getAllItems must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getAllItems", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that each item entry has non-null fields
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertFalse(results.isEmpty(), "Results should not be empty.");
		results.forEach(item -> {
			Assert.assertNotNull(item.get("ItemId"), "ItemId should not be null.");
			Assert.assertNotNull(item.get("ItemName"), "ItemName should not be null.");
		});

		// Print response for debugging
		System.out.println("All Items Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 3, groups = { "PL1" }, description = "1. Send a GET request to Incentive Summary Report\n"
			+ "2. Validate that the Prescriber Id and Prescriber name are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getIncentiveSummary() throws Exception {
		// Initialize the ApiUtil object
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		apiUtil = new ApiUtil();

		String fromDate = searchResult.get("IncSummFromDate");
		String toDate = searchResult.get("IncSummToDate");
		String isRefferal = searchResult.get("IsRefferalOnly");

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil
				.getIncentiveSummaryReport("https://healthapp.yaksha.com/BillingReports/INCTV_DocterSummary?FromDate="
						+ fromDate + "&ToDate=" + toDate + "&IsRefferalOnly=" + isRefferal, null);

		// Validate the implementation of getIncentiveSummaryReport
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getIncentiveSummary",
				List.of("given", "then", "extract", "response"));
		System.out.println("---------------------------------------------" + isValidationSuccessful
				+ "------------------------------");

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the status field in the response
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate response fields using the shared method
		boolean isResponseValid = TestCodeValidator.validateResponseFields("getIncentiveSummaryReport", customResponse);
		Assert.assertTrue(isResponseValid, "Response validation failed.");

		// Print the entire API response for debugging
		System.out.println("Incentive Summary Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 4, groups = { "PL1" }, description = "1. Send a GET request to Incentive Referral Summary Report\n"
			+ "2. Validate that the Prescriber name, PrescriberId, DocTotalAmount, TDSAmount and NetPayableAmount are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getIncentiveReffSummary() throws Exception {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Fetch search parameters from the Excel file
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		String IncFromDate = searchResult.get("IncFromDate");
		String IncToDate = searchResult.get("IncToDate");
		String isRefferal = searchResult.get("DocSumIsRefferalOnly");

		// Send GET request to fetch incentive referral summary report
		CustomResponse customResponse = apiUtil
				.getIncReffSummReport("https://healthapp.yaksha.com/BillingReports/INCTV_DocterSummary?FromDate="
						+ IncFromDate + "&ToDate=" + IncToDate + "&IsRefferalOnly=" + isRefferal, null);

		// Validate the implementation of getIncReffSummReport method
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getIncReffSummReport",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getIncentiveReffSummary must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getIncentiveReffSummary", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Parse the results
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertFalse(results.isEmpty(), "Results should not be empty.");

		// Iterate through each record and validate fields
		results.forEach(result -> {
			String prescriberName = String.valueOf(result.get("PrescriberName"));
			String prescriberId = String.valueOf(result.get("PrescriberId"));
			String docTotalAmount = String.valueOf(result.get("DocTotalAmount"));
			String tdsAmount = String.valueOf(result.get("TDSAmount"));
			String netPayableAmount = String.valueOf(result.get("NetPayableAmount"));

			// Assert fields are not null
			Assert.assertNotNull(prescriberName, "The Prescriber Name is null.");
			Assert.assertNotNull(prescriberId, "The Prescriber ID is null.");
			Assert.assertNotNull(docTotalAmount, "The DocTotal Amount is null.");
			Assert.assertNotNull(tdsAmount, "The TDS Amount is null.");
			Assert.assertNotNull(netPayableAmount, "The Net Payable Amount is null.");

			// Print extracted fields for debugging
			System.out.println("PrescriberName: " + prescriberName);
			System.out.println("PrescriberId: " + prescriberId);
			System.out.println("DocTotalAmount: " + docTotalAmount);
			System.out.println("TDSAmount: " + tdsAmount);
			System.out.println("NetPayableAmount: " + netPayableAmount);
			System.out.println();
		});

		// Print full API response for debugging
		System.out.println("Full API Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 5, groups = { "PL1" }, description = "1. Send a GET request to Hospital Income Incentive Report\n"
			+ "2. Validate that the ServiceDepartmentName, ServiceDepartmentId, NetSales, ReferralCommission, GrossIncome, OtherIncentive, and HospitalNetIncome are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getHospitalIncomeIncReport() throws Exception {
		// Initialize the ApiUtil object
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		apiUtil = new ApiUtil();

		String IncFromDate = searchResult.get("IncFromDate");
		String IncToDate = searchResult.get("IncToDate");
		String ServiceDepartments = searchResult.get("ServiceDepartments");

		// Fetch the response from the API
		CustomResponse hospitalIncomeResponse = apiUtil
				.getHospIncIncReport("https://healthapp.yaksha.com/Reporting/HospitalIncomeIncentiveReport?FromDate="
						+ IncFromDate + "&ToDate=" + IncToDate + "&ServiceDepartments=" + ServiceDepartments, null);

		// Validate the implementation of getHospIncIncReport method using RestAssured
		// methods
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getHospIncIncReport",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getHospIncIncReport must be implemented using Rest Assured methods only.");

		// Validate response structure using validateResponseFields
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getHospIncIncReport", hospitalIncomeResponse),
				"Must have all required fields in the response.");

		// Validate the response status code
		Assert.assertEquals(hospitalIncomeResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the "Status" field in the response
		String status = hospitalIncomeResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Get the JsonData directly from Results (since it's an array now, not a
		// string)
		List<Map<String, Object>> results = hospitalIncomeResponse.getResponse().jsonPath().getList("Results");

		// Assert that results are not null
		Assert.assertNotNull(results, "Results field should not be null.");

		// Iterate through each record in the results
		for (Map<String, Object> result : results) {
			// Print the keys for debugging
			for (String key : result.keySet()) {
				System.out.println("Key: " + key);
			}

			// Extract and validate each field
			String serviceDepartmentId = String.valueOf(result.get("ServiceDepartmentId"));
			String serviceDepartmentName = String.valueOf(result.get("ServiceDepartmentName"));
			String netSales = String.valueOf(result.get("NetSales"));
			String referralCommission = String.valueOf(result.get("ReferralCommission"));
			String grossIncome = String.valueOf(result.get("GrossIncome"));
			String otherIncentive = String.valueOf(result.get("OtherIncentive"));
			String hospitalNetIncome = String.valueOf(result.get("HospitalNetIncome"));

			// Print extracted fields
			System.out.println("ServiceDepartmentId: " + serviceDepartmentId);
			System.out.println("ServiceDepartmentName: " + serviceDepartmentName);
			System.out.println("NetSales: " + netSales);
			System.out.println("ReferralCommission: " + referralCommission);
			System.out.println("GrossIncome: " + grossIncome);
			System.out.println("OtherIncentive: " + otherIncentive);
			System.out.println("HospitalNetIncome: " + hospitalNetIncome);
			System.out.println();

			// Assert fields are not null
			Assert.assertNotNull(serviceDepartmentId, "ServiceDepartmentId should not be null.");
			Assert.assertNotNull(serviceDepartmentName, "ServiceDepartmentName should not be null.");
			Assert.assertNotNull(netSales, "NetSales should not be null.");
			Assert.assertNotNull(referralCommission, "ReferralCommission should not be null.");
			Assert.assertNotNull(grossIncome, "GrossIncome should not be null.");
			Assert.assertNotNull(otherIncentive, "OtherIncentive should not be null.");
			Assert.assertNotNull(hospitalNetIncome, "HospitalNetIncome should not be null.");
		}

		// Print the entire API response
		System.out.println("Full API Response:");
		hospitalIncomeResponse.getResponse().prettyPrint();
	}

	@Test(priority = 6, groups = { "PL1" }, description = "1. Send a GET request to Incentive Employee Bill Items\n"
			+ "2. Validate that the EmployeeIncentiveInfoId, EmployeeId, FullName, TDSPercent, EmpTDSPercent, IsActive are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getIncenEmpBillItems() throws Exception {
		apiUtil = new ApiUtil();

		// Fetch the employeeId from the Excel file
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);
		String employeeId = searchResult.get("employeeId");

		// Send GET request to fetch employee bill items
		CustomResponse customResponse = apiUtil.getEmpBillItem("/Incentive/EmployeeBillItems?employeeId=" + employeeId,
				null);

		// Validate the implementation of getEmpBillItem method using Rest Assured
		// methods
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getEmpBillItem",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getIncenEmpBillItems must be implemented using Rest Assured methods only.");

		// Validate response structure using validateResponseFields
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getEmpBillItem", customResponse),
				"Must have all required fields in the response.");

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the "Status" field in the response
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Parse the "Results" field and assert it's not null or empty
		Map<String, Object> result = customResponse.getMapResults();
		Assert.assertNotNull(result, "Results field should not be null.");
		Assert.assertFalse(result.isEmpty(), "Results should not be empty.");

		// Extract and validate each field directly from the "result" map
		String employeeIncentiveInfoId = String.valueOf(result.get("EmployeeIncentiveInfoId"));
		String employeeIdFromResponse = String.valueOf(result.get("EmployeeId"));
		String fullName = String.valueOf(result.get("FullName"));
		String tdsPercent = String.valueOf(result.get("TDSPercent"));
		String empTdsPercent = String.valueOf(result.get("EmpTDSPercent"));
		String isActive = String.valueOf(result.get("IsActive"));
		List<Map<String, Object>> employeeBillItemsMap = (List<Map<String, Object>>) result.get("EmployeeBillItemsMap");

		// Assert fields are not null
		Assert.assertNotNull(employeeIncentiveInfoId, "EmployeeIncentiveInfoId should not be null.");
		Assert.assertNotNull(employeeIdFromResponse, "EmployeeId should not be null.");
		Assert.assertNotNull(fullName, "FullName should not be null.");
		Assert.assertNotNull(tdsPercent, "TDSPercent should not be null.");
		Assert.assertNotNull(empTdsPercent, "EmpTDSPercent should not be null.");
		Assert.assertNotNull(isActive, "IsActive should not be null.");
		Assert.assertNotNull(employeeBillItemsMap, "EmployeeBillItemsMap should not be null.");

		// Additional validation: check if EmployeeBillItemsMap is empty (which is
		// allowed, as per the response structure)
		if (employeeBillItemsMap != null && employeeBillItemsMap.isEmpty()) {
			System.out.println("EmployeeBillItemsMap is empty, which is allowed.");
		}

		// Print extracted fields for debugging
		System.out.println("EmployeeIncentiveInfoId: " + employeeIncentiveInfoId);
		System.out.println("EmployeeId: " + employeeIdFromResponse);
		System.out.println("FullName: " + fullName);
		System.out.println("TDSPercent: " + tdsPercent);
		System.out.println("EmpTDSPercent: " + empTdsPercent);
		System.out.println("IsActive: " + isActive);
		System.out.println("EmployeeBillItemsMap: " + employeeBillItemsMap);
		System.out.println();

		// Print the entire API response for debugging
		System.out.println("Employee Bill Items Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 7, groups = { "PL1" }, description = "1. Send a GET request to Inventory Fiscal Years\n"
			+ "2. Validate that the FiscalYearId, FiscalYearName are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getInventoryFiscalYears() throws IOException {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil.getInvntryFiscalYrs("/Inventory/InventoryFiscalYears", null);

		// Validate the implementation of getInvntryFiscalYrs
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getInvntryFiscalYrs",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getInvntryFiscalYrs must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getInvntryFiscalYrs", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate the "Results" field in the response
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertNotNull(results, "Results field should not be null.");
		Assert.assertFalse(results.isEmpty(), "Results list should not be empty.");

		// Loop through each fiscal year and extract fields
		for (Map<String, Object> fiscalYear : results) {
			Integer fiscalYearId = (Integer) fiscalYear.get("FiscalYearId");
			String fiscalYearName = (String) fiscalYear.get("FiscalYearName");
			String startDate = (String) fiscalYear.get("StartDate");
			String endDate = (String) fiscalYear.get("EndDate");
			Boolean isActive = (Boolean) fiscalYear.get("IsActive");

			// Assert fields are not null
			Assert.assertNotNull(fiscalYearId, "FiscalYearId should not be null.");
			Assert.assertNotNull(fiscalYearName, "FiscalYearName should not be null.");
			Assert.assertNotNull(startDate, "StartDate should not be null.");
			Assert.assertNotNull(endDate, "EndDate should not be null.");
			Assert.assertNotNull(isActive, "IsActive should not be null.");

			// Print extracted fields for debugging
			System.out.println("FiscalYearId: " + fiscalYearId);
			System.out.println("FiscalYearName: " + fiscalYearName);
			System.out.println("StartDate: " + startDate);
			System.out.println("EndDate: " + endDate);
			System.out.println("IsActive: " + isActive);
			System.out.println();
		}

		// Print the entire API response for debugging
		System.out.println("Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 8, groups = { "PL1" }, description = "1. Send a GET request to Activate Inventory/\n"
			+ "2. Validate that the StoreId, Name, and StoreDescription are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void activateInventory() throws IOException {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil.getActInventory("/ActivateInventory/", null);

		// Validate the implementation of getActInventory
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getActInventory",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getActInventory must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getActInventory", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that the "Results" field is not empty or null
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertNotNull(results, "Results field should not be null.");
		Assert.assertFalse(results.isEmpty(), "Results list should not be empty.");

		// Loop through each store and validate the necessary fields
		results.forEach(store -> {
			// Extract the fields
			Integer storeId = (Integer) store.get("StoreId");
			String name = (String) store.get("Name");
			String storeDescription = (String) store.get("StoreDescription");

			// Print the extracted fields for debugging
			System.out.println("StoreId: " + storeId);
			System.out.println("Name: " + name);
			System.out.println("StoreDescription: " + storeDescription);
			System.out.println();

			// Assert that the fields are not null
			Assert.assertNotNull(storeId, "StoreId should not be null.");
			Assert.assertNotNull(name, "Name should not be null.");
			Assert.assertNotNull(storeDescription, "StoreDescription should not be null.");
		});

		// Print the entire API response for debugging
		System.out.println("Activate Inventory Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 9, groups = { "PL1" }, description = "1. Send a GET request to Inventory Subcategory\n"
			+ "2. Validate that the SubCategoryName and SubCategoryId are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void inventorySubCategory() throws IOException {
		apiUtil = new ApiUtil();

		// Send GET request
		CustomResponse customResponse = apiUtil.getInvSubCat("/Inventory/SubCategories", null);

		// Validate the implementation of getInvSubCat
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getInvSubCat",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getInvSubCat must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getInvSubCat", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate the "Results" field in the response
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertNotNull(results, "Results field should not be null.");
		Assert.assertFalse(results.isEmpty(), "Results list should not be empty.");

		// Validate that each subcategory entry has non-null fields
		results.forEach(subcategory -> {
			Assert.assertNotNull(subcategory.get("SubCategoryId"), "SubCategoryId should not be null.");
			Assert.assertNotNull(subcategory.get("SubCategoryName"), "SubCategoryName should not be null.");
		});

		// Print response for debugging
		System.out.println("Inventory Subcategories Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 10, groups = { "PL1" }, description = "1. Send a GET request to available Items by Store Id\n"
			+ "2. Validate that the ItemId, AvailableQuantity and StoreId are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void availableItems() throws Exception {
		// Initialize the ApiUtil object
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		apiUtil = new ApiUtil();

		String reqItemId = searchResult.get("itemId");
		String reqStoreId = searchResult.get("storeId");

		// Send GET request
		CustomResponse customResponse = apiUtil.getAvlQtyByStoreId(
				"/Inventory/AvailableQuantityByItemIdAndStoreId?itemId=" + reqItemId + "&storeId=" + reqStoreId, null);

		// Validate the implementation of getAvlQtyByStoreId
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAvlQtyByStoreId",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"availableItems must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("availableItems", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate the "Results" field in the response
		Map<String, Object> results = customResponse.getMapResults(); // Use getMapResults instead of list
		Assert.assertNotNull(results, "Results field should not be null.");

		// Extract fields from the "Results" object
		Integer itemId = (Integer) results.get("ItemId");
		Float availableQuantity = (Float) results.get("AvailableQuantity");
		Integer storeId = (Integer) results.get("StoreId");

		// Print extracted fields
		System.out.println("ItemId: " + itemId);
		System.out.println("AvailableQuantity: " + availableQuantity);
		System.out.println("StoreId: " + storeId);

		// Assert fields are not null
		Assert.assertNotNull(itemId, "ItemId should not be null.");
		Assert.assertNotNull(availableQuantity, "AvailableQuantity should not be null.");
		Assert.assertNotNull(storeId, "StoreId should not be null.");

		// Print the entire API response
		System.out.println("Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 11, groups = {
			"PL1" }, description = "1. Send a GET request to get Inventory Requisition Items For View by requisition Id.\n"
					+ "2. Validate that the RequisitionNo, CreatedByName are not null.\n"
					+ "3. Verify the response status code is 200.")
	public void requisitionItems() throws Exception {
		// Initialize the ApiUtil object
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		apiUtil = new ApiUtil();

		String requisitionId = searchResult.get("requisitionId");

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil
				.getReqItemsById("/Inventory/RequisitionItemsForView?requisitionId=" + requisitionId, null);

		// Validate the implementation of getReqItemsById using Rest Assured methods
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getReqItemsById",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"requisitionItems must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("requisitionItems", customResponse),
				"Must have all required fields in the response.");

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the "Results" field in the response
		Map<String, Object> results = customResponse.getMapResults();
		Assert.assertNotNull(results, "Results field should not be null.");

		// Validate the "Requisition" field
		Map<String, Object> requisition = (Map<String, Object>) results.get("Requisition");
		Assert.assertNotNull(requisition, "Requisition field should not be null.");

		// Extract fields from the "Requisition" object
		String createdByName = (String) requisition.get("CreatedByName");
		Integer requisitionNo = (Integer) requisition.get("RequisitionNo");
		String requisitionStatus = (String) requisition.get("RequisitionStatus");

		// Print extracted requisition details
		System.out.println("CreatedByName: " + createdByName);
		System.out.println("RequisitionNo: " + requisitionNo);
		System.out.println("RequisitionStatus: " + requisitionStatus);

		// Assert requisition fields are not null
		Assert.assertNotNull(createdByName, "CreatedByName should not be null.");
		Assert.assertNotNull(requisitionNo, "RequisitionNo should not be null.");
		Assert.assertNotNull(requisitionStatus, "RequisitionStatus should not be null.");

		// Validate the "RequisitionItems" list
		List<Map<String, Object>> requisitionItems = (List<Map<String, Object>>) requisition.get("RequisitionItems");
		Assert.assertNotNull(requisitionItems, "RequisitionItems field should not be null.");
		Assert.assertFalse(requisitionItems.isEmpty(), "RequisitionItems list should not be empty.");

		// Loop through each requisition item and extract fields
		for (Map<String, Object> item : requisitionItems) {
			String itemName = (String) item.get("ItemName");
			String code = (String) item.get("Code");
			Float pendingQuantity = (Float) item.get("PendingQuantity");
			String requisitionItemStatus = (String) item.get("RequisitionItemStatus");

			// Print extracted requisition item details
			System.out.println("ItemName: " + itemName);
			System.out.println("Code: " + code);
			System.out.println("PendingQuantity: " + pendingQuantity);
			System.out.println("RequisitionItemStatus: " + requisitionItemStatus);

			// Assert requisition item fields are not null
			Assert.assertNotNull(itemName, "ItemName should not be null.");
			Assert.assertNotNull(code, "Code should not be null.");
			Assert.assertNotNull(pendingQuantity, "PendingQuantity should not be null.");
			Assert.assertNotNull(requisitionItemStatus, "RequisitionItemStatus should not be null.");
		}

		// Print the entire API response for debugging
		System.out.println("Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 12, groups = { "PL1" }, description = "1. Send a GET request to track by requisition Id.\n"
			+ "2. Validate that the RequisitionId, CreatedBy and Dispatchers are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void verifyRequisitionAndDispatchFields() throws Exception {
		// Initialize the ApiUtil object
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		apiUtil = new ApiUtil();

		String trackByRequisitionId = searchResult.get("trackByRequisitionId");

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil
				.trackReqItemById("/Inventory/TrackRequisition?requisitionId=" + trackByRequisitionId, null);

		// Validate the implementation of trackReqItemById
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "trackReqItemById",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"verifyRequisitionAndDispatchFields must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(
				TestCodeValidator.validateResponseFields("verifyRequisitionAndDispatchFields", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Extracting and validating the "Results" field from the response
		Map<String, Object> results = customResponse.getMapResults();
		Assert.assertNotNull(results, "Results field should not be null.");

		// Validate the "RequisitionId" field
		Integer requisitionId = (Integer) results.get("RequisitionId");
		Assert.assertNotNull(requisitionId, "RequisitionId field should not be null.");
		System.out.println("RequisitionId: " + requisitionId);

		// Validate the "CreatedBy" field
		String createdBy = (String) results.get("CreatedBy");
		Assert.assertNotNull(createdBy, "CreatedBy field should not be null.");
		System.out.println("CreatedBy: " + createdBy);

		// Validate the "Status" field
		String requisitionStatus = (String) results.get("Status");
		Assert.assertNotNull(requisitionStatus, "Status field should not be null.");
		System.out.println("Requisition Status: " + requisitionStatus);

		// Validate the "Dispatchers" field
		List<Map<String, Object>> dispatchers = (List<Map<String, Object>>) results.get("Dispatchers");
		Assert.assertNotNull(dispatchers, "Dispatchers field should not be null.");
		Assert.assertFalse(dispatchers.isEmpty(), "Dispatchers list should not be empty.");

		// Loop through each dispatcher and validate its fields
		for (Map<String, Object> dispatcher : dispatchers) {
			Integer dispatchId = (Integer) dispatcher.get("DispatchId");
			String name = (String) dispatcher.get("Name");

			// Validate DispatchId and Name
			Assert.assertNotNull(dispatchId, "DispatchId should not be null.");
			Assert.assertNotNull(name, "Name should not be null.");

			// Print dispatcher details
			System.out.println("DispatchId: " + dispatchId);
			System.out.println("Name: " + name);
		}

		// Print the entire API response for debugging
		System.out.println("Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 13, groups = { "PL1" }, description = "1. Send a GET request to get inventory items by store Id.\n"
			+ "2. Validate that the ItemId, ItemName, and AvailableQuantity are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void inventoryItemByStoreId() throws IOException {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil.getInvItem("/WardSupply/GetInventoryItemsByStoreId/7", null);

		// Validate the implementation of getInvItem
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getInvItem",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getInvItem must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getInvItem", customResponse),
				"Must have all required fields in the response.");

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate the "Results" field in the response
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertNotNull(results, "Results field should not be null.");
		Assert.assertFalse(results.isEmpty(), "Results list should not be empty.");

		// Loop through each item in the "Results" list and validate fields
		for (Map<String, Object> item : results) {
			Integer itemId = (Integer) item.get("ItemId");
			String itemName = (String) item.get("ItemName");
			Float availableQuantity = item.get("AvailableQuantity") == null ? null
					: Float.parseFloat(item.get("AvailableQuantity").toString());
			String code = (String) item.get("Code");
			String itemType = (String) item.get("ItemType");

			// Print extracted fields for debugging
			System.out.println("ItemId: " + itemId);
			System.out.println("ItemName: " + itemName);
			System.out.println("AvailableQuantity: " + availableQuantity);
			System.out.println("Code: " + code);
			System.out.println("ItemType: " + itemType);

			// Assert fields are not null
			Assert.assertNotNull(itemId, "ItemId should not be null.");
			Assert.assertNotNull(itemName, "ItemName should not be null.");
			Assert.assertNotNull(availableQuantity, "AvailableQuantity should not be null.");
			Assert.assertNotNull(code, "Code should not be null.");
			Assert.assertNotNull(itemType, "ItemType should not be null.");

			// Additional validations (if needed)
			// For example, checking if AvailableQuantity is greater than 0
			Assert.assertTrue(availableQuantity > 0, "AvailableQuantity should be greater than 0.");
		}

		// Print the entire API response for debugging
		System.out.println("Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 14, groups = {
			"PL1" }, description = "1. Send a GET request to get most sold medicine within date range.\n"
					+ "2. Validate that the ItemName and SoldQuantity are not null.\n"
					+ "3. Verify the response status code is 200.")
	public void mostSoldMedicine() throws Exception {
		// Initialize the ApiUtil object
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		apiUtil = new ApiUtil();

		String FromDate = searchResult.get("soldMedFromDate");
		String IncToDate = searchResult.get("IncToDate");

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil.getMostSoldMed(
				"https://healthapp.yaksha.com/PharmacyDashboard/GetPharmacyDashboardMostSoldMedicine?FromDate="
						+ FromDate + "&ToDate=" + IncToDate,
				null);

		// Validate the implementation of getMostSoldMed
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getMostSoldMed",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getMostSoldMed must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getMostSoldMed", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate the "Results" field in the response
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertNotNull(results, "Results field should not be null.");
		Assert.assertFalse(results.isEmpty(), "Results list should not be empty.");

		// Loop through each item in the "Results" list and validate fields
		for (Map<String, Object> item : results) {
			String itemName = (String) item.get("ItemName");
			Float soldQuantity = item.get("SoldQuantity") == null ? null
					: Float.parseFloat(item.get("SoldQuantity").toString());

			// Print extracted fields
			System.out.println("ItemName: " + itemName);
			System.out.println("SoldQuantity: " + soldQuantity);

			// Assert fields are not null
			Assert.assertNotNull(itemName, "ItemName should not be null.");
			Assert.assertNotNull(soldQuantity, "SoldQuantity should not be null.");

			// Additional validation: SoldQuantity should be greater than 0
			Assert.assertTrue(soldQuantity > 0, "SoldQuantity should be greater than 0.");
		}

		// Print the entire API response for debugging
		System.out.println("Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 15, groups = {
			"PL1" }, description = "1. Send a GET request to get substore wise dispatch value.\n"
					+ "2. Validate that the Name and TotalDispatchValue are not null.\n"
					+ "3. Verify the response status code is 200.")
	public void substoreWiseDisp() throws Exception {
		// Initialize the ApiUtil object
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);
		apiUtil = new ApiUtil();

		String FromDate = searchResult.get("soldMedFromDate");
		String IncToDate = searchResult.get("IncToDate");

		// Send GET request to the API
		CustomResponse customResponse = apiUtil.getSubDisp(
				"https://healthapp.yaksha.com/PharmacyDashboard/GetPharmacyDashboardSubstoreWiseDispatchValue?FromDate="
						+ FromDate + "&ToDate=" + IncToDate,
				null);

		// Validate the implementation of getSubDisp
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getSubDisp",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getSubDisp must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getSubDisp", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that the "Results" field is not null and not empty
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertNotNull(results, "Results field should not be null.");
		Assert.assertFalse(results.isEmpty(), "Results list should not be empty.");

		// Loop through each substore entry and validate fields
		for (Map<String, Object> substore : results) {
			String name = (String) substore.get("Name");
			Double totalDispatchValue = substore.get("TotalDispatchValue") == null ? null
					: Double.parseDouble(substore.get("TotalDispatchValue").toString());

			// Print the extracted fields for debugging purposes
			System.out.println("Substore: " + name);
			System.out.println("Total Dispatch Value: " + totalDispatchValue);

			// Assert that fields are not null
			Assert.assertNotNull(name, "Name should not be null.");
			Assert.assertNotNull(totalDispatchValue, "TotalDispatchValue should not be null.");

			// Validate that TotalDispatchValue is greater than 0
			Assert.assertTrue(totalDispatchValue > 0, "TotalDispatchValue should be greater than zero.");
		}

		// Print the entire API response for debugging
		System.out.println("Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 16, groups = { "PL1" }, description = "1. Send a GET request to get active suppliers.\n"
			+ "2. Validate that the SupplierId and SupplierName are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void activSupp() throws IOException {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil.getActSupp("/PharmacySettings/ActiveSuppliers", null);

		// Validate the implementation of getActSupp
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getActSupp",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getActSupp must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getActSupp", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate the "Results" field in the response
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertNotNull(results, "Results field should not be null.");
		Assert.assertFalse(results.isEmpty(), "Results list should not be empty.");

		// Loop through each item in the "Results" list and validate fields
		for (Map<String, Object> item : results) {
			// Extract fields from the item
			String supplierName = (String) item.get("SupplierName");
			Integer supplierId = (Integer) item.get("SupplierId");

			// Print extracted fields for debugging purposes
			System.out.println("SupplierName: " + supplierName);
			System.out.println("SupplierId: " + supplierId);

			// Assert SupplierName and SupplierId are not null
			Assert.assertNotNull(supplierName, "SupplierName should not be null.");
			Assert.assertNotNull(supplierId, "SupplierId should not be null.");
		}

		// Print the entire API response for debugging
		System.out.println("Active Suppliers Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 17, groups = { "PL1" }, description = "1. Send a GET request to get unit of measurements.\n"
			+ "2. Validate that the UOMId and UOMName are not null.\n" + "3. Verify the response status code is 200.")
	public void measureUnits() throws Exception {
		apiUtil = new ApiUtil();

		// Send GET request
		CustomResponse customResponse = apiUtil.getMeasureUnits("/PharmacySettings/UnitOfMeasurements", null);

		// Validate the implementation of getMeasureUnits
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getMeasureUnits",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getMeasureUnits must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getMeasureUnits", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that each unit of measurement entry has non-null fields
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertFalse(results.isEmpty(), "Results should not be empty.");
		results.forEach(unitOfMeasurement -> {
			Assert.assertNotNull(unitOfMeasurement.get("UOMId"), "UOMId should not be null.");
			Assert.assertNotNull(unitOfMeasurement.get("UOMName"), "UOMName should not be null.");
		});

		// Print response for debugging
		System.out.println("Units of Measurement Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 18, groups = {
			"PL1" }, description = "1. Send a GET request to get sales categories from pharmacy.\n"
					+ "2. Validate that the Name and SalesCategoryId are not null.\n"
					+ "3. Verify the response status code is 200.")
	public void salesCat() throws IOException {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Send GET request to retrieve sales categories
		CustomResponse customResponse = apiUtil.getSalesCat("/PharmacySettings/SalesCategories", null);

		// Validate the implementation of getSalesCat
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getSalesCat",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getSalesCat must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getSalesCat", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that the "Results" field is not empty or null
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertNotNull(results, "Results field should not be null.");
		Assert.assertFalse(results.isEmpty(), "Results list should not be empty.");

		// Loop through each item in the "Results" list and validate fields
		for (Map<String, Object> item : results) {
			String Name = (String) item.get("Name");
			Integer SalesCategoryId = (Integer) item.get("SalesCategoryId");

			// Print extracted fields for debugging
			System.out.println("Name: " + Name);
			System.out.println("SalesCategoryId: " + SalesCategoryId);

			// Assert fields are not null
			Assert.assertNotNull(Name, "Name should not be null.");
			Assert.assertNotNull(SalesCategoryId, "SalesCategoryId should not be null.");
		}

		// Print the entire API response for debugging
		System.out.println("Sales Categories Response:");
		customResponse.getResponse().prettyPrint();
	}
}
