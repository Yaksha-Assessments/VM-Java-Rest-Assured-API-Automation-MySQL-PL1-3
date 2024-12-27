package testcases;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		List<Object> departmentIds = customResponse.getPatientIds();
		List<Object> departmentNames = customResponse.getPatientCodes();

		Set<Object> uniqueDepartmentIds = new HashSet<>();
		Assert.assertFalse(departmentIds.isEmpty(), "DepartmentIds should not be empty.");
		for (int i = 0; i < departmentIds.size(); i++) {
			Assert.assertNotNull(departmentIds.get(i), "DepartmentId should not be null.");
			Assert.assertNotNull(departmentNames.get(i), "DepartmentName should not be null.");

			uniqueDepartmentIds.add(departmentIds.get(i));
		}

		// Validate uniqueness of DepartmentId
		Assert.assertEquals(uniqueDepartmentIds.size(), departmentIds.size(), "DepartmentId values should be unique.");

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
		List<Object> itemIds = customResponse.getPatientIds();
		List<Object> itemNames = customResponse.getPatientCodes();

		Set<Object> uniqueItemIds = new HashSet<>();
		Assert.assertFalse(itemIds.isEmpty(), "ItemIds should not be empty.");
		for (int i = 0; i < itemIds.size(); i++) {
			Assert.assertNotNull(itemIds.get(i), "ItemId should not be null.");
			Assert.assertNotNull(itemNames.get(i), "ItemName should not be null.");

			uniqueItemIds.add(itemIds.get(i));
		}

		// Validate uniqueness of ItemId
		Assert.assertEquals(uniqueItemIds.size(), itemIds.size(), "ItemId values should be unique.");

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

		// Validate the individual fields
		Assert.assertFalse(customResponse.getPrescriberNames().isEmpty(), "Prescriber Names should not be empty.");
		Assert.assertFalse(customResponse.getPrescriberIds().isEmpty(), "Prescriber Ids should not be empty.");
		Assert.assertFalse(customResponse.getDocTotalAmounts().isEmpty(), "DocTotalAmounts should not be empty.");
		Assert.assertFalse(customResponse.getTdsAmounts().isEmpty(), "TDS Amounts should not be empty.");
		Assert.assertFalse(customResponse.getNetPayableAmounts().isEmpty(), "Net Payable Amounts should not be empty.");

		// Iterate through each record and validate fields
		for (int i = 0; i < customResponse.getPrescriberNames().size(); i++) {
			Object prescriberName = customResponse.getPrescriberNames().get(i);
			Object prescriberId = customResponse.getPrescriberIds().get(i);
			Object docTotalAmount = customResponse.getDocTotalAmounts().get(i);
			Object tdsAmount = customResponse.getTdsAmounts().get(i);
			Object netPayableAmount = customResponse.getNetPayableAmounts().get(i);

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
		}

		// Print full API response for debugging
		System.out.println("Full API Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 5, groups = { "PL1" }, description = "1. Send a GET request to Hospital Income Incentive Report\n"
			+ "2. Validate that the ServiceDepartmentName, ServiceDepartmentId, NetSales, ReferralCommission, GrossIncome, OtherIncentive, and HospitalNetIncome are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getHospitalIncomeIncReport() throws Exception {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Fetch search parameters from the Excel file
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		String IncFromDate = searchResult.get("IncFromDate");
		String IncToDate = searchResult.get("IncToDate");
		String ServiceDepartments = searchResult.get("ServiceDepartments");

		// Send GET request to fetch hospital income incentive report
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

		// Extract individual fields from the response
		List<Object> serviceDepartmentIds = hospitalIncomeResponse.getServiceDepartmentIds();
		List<Object> serviceDepartmentNames = hospitalIncomeResponse.getServiceDepartmentNames();
		List<Object> netSales = hospitalIncomeResponse.getNetSales();
		List<Object> referralCommissions = hospitalIncomeResponse.getReferralCommissions();
		List<Object> grossIncomes = hospitalIncomeResponse.getGrossIncomes();
		List<Object> otherIncentives = hospitalIncomeResponse.getOtherIncentives();
		List<Object> hospitalNetIncomes = hospitalIncomeResponse.getHospitalNetIncomes();

		// Assert that results are not null
		Assert.assertNotNull(serviceDepartmentIds, "ServiceDepartmentIds field should not be null.");
		Assert.assertNotNull(serviceDepartmentNames, "ServiceDepartmentNames field should not be null.");
		Assert.assertNotNull(netSales, "NetSales field should not be null.");
		Assert.assertNotNull(referralCommissions, "ReferralCommissions field should not be null.");
		Assert.assertNotNull(grossIncomes, "GrossIncomes field should not be null.");
		Assert.assertNotNull(otherIncentives, "OtherIncentives field should not be null.");
		Assert.assertNotNull(hospitalNetIncomes, "HospitalNetIncomes field should not be null.");

		// Assert that lists are not empty
		Assert.assertFalse(serviceDepartmentIds.isEmpty(), "ServiceDepartmentIds list should not be empty.");
		Assert.assertFalse(serviceDepartmentNames.isEmpty(), "ServiceDepartmentNames list should not be empty.");
		Assert.assertFalse(netSales.isEmpty(), "NetSales list should not be empty.");
		Assert.assertFalse(referralCommissions.isEmpty(), "ReferralCommissions list should not be empty.");
		Assert.assertFalse(grossIncomes.isEmpty(), "GrossIncomes list should not be empty.");
		Assert.assertFalse(otherIncentives.isEmpty(), "OtherIncentives list should not be empty.");
		Assert.assertFalse(hospitalNetIncomes.isEmpty(), "HospitalNetIncomes list should not be empty.");

		// Iterate through each record and validate fields
		for (int i = 0; i < serviceDepartmentIds.size(); i++) {
			// Extract values from each list
			Object serviceDepartmentId = serviceDepartmentIds.get(i);
			Object serviceDepartmentName = serviceDepartmentNames.get(i);
			Object netSale = netSales.get(i);
			Object referralCommission = referralCommissions.get(i);
			Object grossIncome = grossIncomes.get(i);
			Object otherIncentive = otherIncentives.get(i);
			Object hospitalNetIncome = hospitalNetIncomes.get(i);

			// Assert fields are not null
			Assert.assertNotNull(serviceDepartmentId, "The ServiceDepartmentId is null.");
			Assert.assertNotNull(serviceDepartmentName, "The ServiceDepartmentName is null.");
			Assert.assertNotNull(netSale, "The NetSales is null.");
			Assert.assertNotNull(referralCommission, "The ReferralCommission is null.");
			Assert.assertNotNull(grossIncome, "The GrossIncome is null.");
			Assert.assertNotNull(otherIncentive, "The OtherIncentive is null.");
			Assert.assertNotNull(hospitalNetIncome, "The HospitalNetIncome is null.");

			// Print extracted fields for debugging
			System.out.println("ServiceDepartmentId: " + serviceDepartmentId);
			System.out.println("ServiceDepartmentName: " + serviceDepartmentName);
			System.out.println("NetSales: " + netSale);
			System.out.println("ReferralCommission: " + referralCommission);
			System.out.println("GrossIncome: " + grossIncome);
			System.out.println("OtherIncentive: " + otherIncentive);
			System.out.println("HospitalNetIncome: " + hospitalNetIncome);
			System.out.println();
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
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getEmpBillItem",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getIncenEmpBillItems must be implemented using Rest Assured methods only.");

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the status field in the response
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Extract and validate each field directly from the "result" map
		Object employeeIncentiveInfoId = customResponse.getEmployeeIncentiveInfoId();
		Object employeeIdFromResponse = customResponse.getEmployeeId();
		Object fullName = customResponse.getFullName();
		Object tdsPercent = customResponse.getTdsPercent();
		Object empTdsPercent = customResponse.getEmpTdsPercent();
		Object isActive = customResponse.getIsActive();
		List<Map<String, Object>> employeeBillItemsMap = customResponse.getEmployeeBillItemsMap();

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

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate each fiscal year field directly from the extracted lists
		List<Object> fiscalYearIds = customResponse.getPrescriberIds();
		List<Object> fiscalYearNames = customResponse.getPrescriberNames();
		List<Object> startDates = customResponse.getDocTotalAmounts();
		List<Object> endDates = customResponse.getTdsAmounts();
		List<Object> isActiveList = customResponse.getNetPayableAmounts();

		// Validate that none of the fields are null
		for (int i = 0; i < fiscalYearIds.size(); i++) {
			Assert.assertNotNull(fiscalYearIds.get(i), "FiscalYearId at index " + i + " should not be null.");
			Assert.assertNotNull(fiscalYearNames.get(i), "FiscalYearName at index " + i + " should not be null.");
			Assert.assertNotNull(startDates.get(i), "StartDate at index " + i + " should not be null.");
			Assert.assertNotNull(endDates.get(i), "EndDate at index " + i + " should not be null.");
			Assert.assertNotNull(isActiveList.get(i), "IsActive at index " + i + " should not be null.");
		}

		// Print the extracted fields for debugging
		System.out.println("FiscalYearId: " + fiscalYearIds);
		System.out.println("FiscalYearName: " + fiscalYearNames);
		System.out.println("StartDate: " + startDates);
		System.out.println("EndDate: " + endDates);
		System.out.println("IsActive: " + isActiveList);
	}

	@Test(priority = 8, groups = { "PL1" }, description = "1. Send a GET request to Activate Inventory\n"
			+ "2. Validate that the StoreId, Name, and StoreDescription are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void activateInventory() throws IOException {
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

		// Loop through each store and validate the necessary fields
		List<Object> storeIds = customResponse.getItemIds();
		List<Object> names = customResponse.getItemNames();
		List<Object> storeDescriptions = customResponse.getGenericNames();

		// Validate that none of the fields are null
		for (int i = 0; i < storeIds.size(); i++) {
			Assert.assertNotNull(storeIds.get(i), "StoreId at index " + i + " should not be null.");
			Assert.assertNotNull(names.get(i), "Name at index " + i + " should not be null.");
			Assert.assertNotNull(storeDescriptions.get(i), "StoreDescription at index " + i + " should not be null.");
		}

		// Print the extracted fields for debugging
		System.out.println("StoreIds: " + storeIds);
		System.out.println("Names: " + names);
		System.out.println("StoreDescriptions: " + storeDescriptions);
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

		// Validate the top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate the "SubCategoryId" and "SubCategoryName" fields
		List<Object> subCategoryIds = customResponse.getPatientIds();
		List<Object> subCategoryNames = customResponse.getPatientCodes();

		// Validate that none of the fields are null
		for (int i = 0; i < subCategoryIds.size(); i++) {
			Assert.assertNotNull(subCategoryIds.get(i), "SubCategoryId at index " + i + " should not be null.");
			Assert.assertNotNull(subCategoryNames.get(i), "SubCategoryName at index " + i + " should not be null.");
		}

		// Print the extracted fields for debugging
		System.out.println("SubCategoryIds: " + subCategoryIds);
		System.out.println("SubCategoryNames: " + subCategoryNames);
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

		// Validate the extracted fields: ItemId, AvailableQuantity, StoreId
		Object itemId = customResponse.getStoreId();
		Object availableQuantity = customResponse.getCategory();
		Object storeId = customResponse.getIsActive();

		// Assert that none of the fields are null
		Assert.assertNotNull(itemId, "ItemId should not be null.");
		Assert.assertNotNull(availableQuantity, "AvailableQuantity should not be null.");
		Assert.assertNotNull(storeId, "StoreId should not be null.");

		// Print the extracted fields for debugging
		System.out.println("ItemId: " + itemId);
		System.out.println("AvailableQuantity: " + availableQuantity);
		System.out.println("StoreId: " + storeId);

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

		// Send GET request
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

		// Validate the "Requisition" fields
		Object requisitionNo = customResponse.getRequisitionNo();
		Object createdByName = customResponse.getCreatedByName();
		Object requisitionStatus = customResponse.getRequisitionStatus();

		Assert.assertNotNull(requisitionNo, "RequisitionNo should not be null.");
		Assert.assertNotNull(createdByName, "CreatedByName should not be null.");
		Assert.assertNotNull(requisitionStatus, "RequisitionStatus should not be null.");

		// Validate the "RequisitionItems" field
		List<Map<String, Object>> requisitionItems = customResponse.getRequisitionItems();
		Assert.assertNotNull(requisitionItems, "RequisitionItems should not be null.");
		Assert.assertFalse(requisitionItems.isEmpty(), "RequisitionItems list should not be empty.");

		// Loop through each requisition item and validate the fields
		for (Map<String, Object> item : requisitionItems) {
			String itemName = (String) item.get("ItemName");
			String code = (String) item.get("Code");
			Float pendingQuantity = (Float) item.get("PendingQuantity");
			String requisitionItemStatus = (String) item.get("RequisitionItemStatus");

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

		// Validate the implementation of trackReqItemById using Rest Assured methods
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

		// Validate the "RequisitionId" field
		Object requisitionId = customResponse.getRequisitionNo();
		Assert.assertNotNull(requisitionId, "RequisitionId field should not be null.");
		System.out.println("RequisitionId: " + requisitionId);

		// Validate the "CreatedBy" field
		Object createdBy = customResponse.getCreatedByName();
		Assert.assertNotNull(createdBy, "CreatedBy field should not be null.");
		System.out.println("CreatedBy: " + createdBy);

		// Validate the "Status" field
		Object requisitionStatus = customResponse.getRequisitionStatus();
		Assert.assertNotNull(requisitionStatus, "Status field should not be null.");
		System.out.println("Requisition Status: " + requisitionStatus);

		// Validate the "Dispatchers" field
		List<Map<String, Object>> dispatchers = customResponse.getRequisitionItems();
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

		// Validate that itemIds, itemNames, and availableQuantities are not null
		List<Object> itemIds = customResponse.getItemIds();
		List<Object> itemNames = customResponse.getItemNames();
		List<Object> availableQuantities = customResponse.getGenericNames();

		Assert.assertNotNull(itemIds, "ItemIds field should not be null.");
		Assert.assertNotNull(itemNames, "ItemNames field should not be null.");
		Assert.assertNotNull(availableQuantities, "AvailableQuantities field should not be null.");

		// Validate that the lists are not empty
		Assert.assertFalse(itemIds.isEmpty(), "ItemIds list should not be empty.");
		Assert.assertFalse(itemNames.isEmpty(), "ItemNames list should not be empty.");
		Assert.assertFalse(availableQuantities.isEmpty(), "AvailableQuantities list should not be empty.");

		// Loop through each item and validate fields
		for (int i = 0; i < itemIds.size(); i++) {
			Integer itemId = (Integer) itemIds.get(i);
			String itemName = (String) itemNames.get(i);
			Float availableQuantity = (Float) availableQuantities.get(i);

			// Print extracted fields for debugging
			System.out.println("ItemId: " + itemId);
			System.out.println("ItemName: " + itemName);
			System.out.println("AvailableQuantity: " + availableQuantity);

			// Assert fields are not null
			Assert.assertNotNull(itemId, "ItemId at index " + i + " should not be null.");
			Assert.assertNotNull(itemName, "ItemName at index " + i + " should not be null.");
			Assert.assertNotNull(availableQuantity, "AvailableQuantity at index " + i + " should not be null.");

			// Additional validations (if needed)
			// For example, checking if AvailableQuantity is greater than 0
			Assert.assertTrue(availableQuantity > 0, "AvailableQuantity at index " + i + " should be greater than 0.");
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

		// Validate the "itemNames" and "soldQuantities" fields in the response
		List<Object> itemNames = customResponse.getPatientIds();
		List<Object> soldQuantities = customResponse.getPatientCodes();

		Assert.assertNotNull(itemNames, "ItemNames field should not be null.");
		Assert.assertNotNull(soldQuantities, "SoldQuantities field should not be null.");

		// Validate that the lists are not empty
		Assert.assertFalse(itemNames.isEmpty(), "ItemNames list should not be empty.");
		Assert.assertFalse(soldQuantities.isEmpty(), "SoldQuantities list should not be empty.");

		// Loop through each item and validate fields
		for (int i = 0; i < itemNames.size(); i++) {
			String itemName = (String) itemNames.get(i);
			Float soldQuantity = (Float) soldQuantities.get(i);

			// Print extracted fields for debugging
			System.out.println("ItemName: " + itemName);
			System.out.println("SoldQuantity: " + soldQuantity);

			// Assert fields are not null
			Assert.assertNotNull(itemName, "ItemName at index " + i + " should not be null.");
			Assert.assertNotNull(soldQuantity, "SoldQuantity at index " + i + " should not be null.");

			// Additional validation: SoldQuantity should be greater than 0
			Assert.assertTrue(soldQuantity > 0, "SoldQuantity at index " + i + " should be greater than 0.");
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

		// Validate the "storeNames" and "totalDispatchValues" fields in the response
		List<Object> storeNames = customResponse.getPatientIds();
		List<Object> totalDispatchValues = customResponse.getPatientCodes();

		Assert.assertNotNull(storeNames, "StoreNames field should not be null.");
		Assert.assertNotNull(totalDispatchValues, "TotalDispatchValues field should not be null.");

		// Validate that the lists are not empty
		Assert.assertFalse(storeNames.isEmpty(), "StoreNames list should not be empty.");
		Assert.assertFalse(totalDispatchValues.isEmpty(), "TotalDispatchValues list should not be empty.");

		// Loop through each store and validate fields
		for (int i = 0; i < storeNames.size(); i++) {
			String storeName = (String) storeNames.get(i);

			// Print extracted fields for debugging
			System.out.println("StoreName: " + storeName);

			// Assert fields are not null
			Assert.assertNotNull(storeName, "StoreName at index " + i + " should not be null.");
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

		// Extract supplier IDs and names from the response
		List<Object> supplierIds = customResponse.getPatientIds();
		List<Object> supplierNames = customResponse.getPatientCodes();

		Assert.assertNotNull(supplierIds, "SupplierIds field should not be null.");
		Assert.assertNotNull(supplierNames, "SupplierNames field should not be null.");
		Assert.assertFalse(supplierIds.isEmpty(), "SupplierIds list should not be empty.");
		Assert.assertFalse(supplierNames.isEmpty(), "SupplierNames list should not be empty.");

		// Loop through each item and validate fields
		for (int i = 0; i < supplierIds.size(); i++) {
			Integer supplierId = (Integer) supplierIds.get(i);
			String supplierName = (String) supplierNames.get(i);

			// Print extracted fields for debugging
			System.out.println("SupplierId: " + supplierId);
			System.out.println("SupplierName: " + supplierName);

			// Assert fields are not null
			Assert.assertNotNull(supplierId, "SupplierId should not be null.");
			Assert.assertNotNull(supplierName, "SupplierName should not be null.");
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

		// Extract UOMIds and UOMNames from the response
		List<Object> uomIds = customResponse.getPatientIds();
		List<Object> uomNames = customResponse.getPatientCodes();

		Assert.assertNotNull(uomIds, "UOMIds field should not be null.");
		Assert.assertNotNull(uomNames, "UOMNames field should not be null.");
		Assert.assertFalse(uomIds.isEmpty(), "UOMIds list should not be empty.");
		Assert.assertFalse(uomNames.isEmpty(), "UOMNames list should not be empty.");

		// Loop through each item and validate fields
		for (int i = 0; i < uomIds.size(); i++) {
			Integer uomId = (Integer) uomIds.get(i);
			String uomName = (String) uomNames.get(i);

			// Print extracted fields for debugging
			System.out.println("UOMId: " + uomId);
			System.out.println("UOMName: " + uomName);

			// Assert fields are not null
			Assert.assertNotNull(uomId, "UOMId should not be null.");
			Assert.assertNotNull(uomName, "UOMName should not be null.");
		}

		// Print the entire API response for debugging
		System.out.println("Units of Measurement Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 18, groups = {
			"PL1" }, description = "1. Send a GET request to get sales categories from pharmacy.\n"
					+ "2. Validate that the Name and SalesCategoryId are not null.\n"
					+ "3. Verify the response status code is 200.")
	public void salesCat() throws IOException {
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
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Extract SalesCategoryIds and SalesCategoryNames from the response
		List<Object> salesCategoryIds = customResponse.getPatientIds();
		List<Object> salesCategoryNames = customResponse.getPatientCodes();

		Assert.assertNotNull(salesCategoryIds, "SalesCategoryIds field should not be null.");
		Assert.assertNotNull(salesCategoryNames, "SalesCategoryNames field should not be null.");
		Assert.assertFalse(salesCategoryIds.isEmpty(), "SalesCategoryIds list should not be empty.");
		Assert.assertFalse(salesCategoryNames.isEmpty(), "SalesCategoryNames list should not be empty.");

		// Loop through each item and validate fields
		for (int i = 0; i < salesCategoryIds.size(); i++) {
			Integer salesCategoryId = (Integer) salesCategoryIds.get(i);
			String salesCategoryName = (String) salesCategoryNames.get(i);

			// Print extracted fields for debugging
			System.out.println("SalesCategoryId: " + salesCategoryId);
			System.out.println("SalesCategoryName: " + salesCategoryName);

			// Assert fields are not null
			Assert.assertNotNull(salesCategoryId, "SalesCategoryId should not be null.");
			Assert.assertNotNull(salesCategoryName, "SalesCategoryName should not be null.");
		}

		// Print the entire API response for debugging
		System.out.println("Sales Categories Response:");
		customResponse.getResponse().prettyPrint();
	}

}
