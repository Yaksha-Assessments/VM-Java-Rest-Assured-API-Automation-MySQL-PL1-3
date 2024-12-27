package testcases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rest.CustomResponse;

public class TestCodeValidator {

	// Method to validate if specific keywords are used in the method's source code
	public static boolean validateTestMethodFromFile(String filePath, String methodName, List<String> keywords)
			throws IOException {
		// Read the content of the test class file
		String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));

		// Extract the method body for the specified method using regex
		String methodRegex = "(public\\s+CustomResponse\\s+" + methodName + "\\s*\\(.*?\\)\\s*\\{)([\\s\\S]*?)}";
		Pattern methodPattern = Pattern.compile(methodRegex);
		Matcher methodMatcher = methodPattern.matcher(fileContent);

		if (methodMatcher.find()) {

			String methodBody = fetchBody(filePath, methodName);

			// Now we validate the method body for the required keywords
			boolean allKeywordsPresent = true;

			// Loop over the provided keywords and check if each one is present in the
			// method body
			for (String keyword : keywords) {
				Pattern keywordPattern = Pattern.compile("\\b" + keyword + "\\s*\\(");
				if (!keywordPattern.matcher(methodBody).find()) {
					System.out.println("'" + keyword + "()' is missing in the method.");
					allKeywordsPresent = false;
				}
			}
			System.out.println("===============");
			System.out.println(allKeywordsPresent);
			System.out.println("===============");
			return allKeywordsPresent;

		} else {
			System.out.println("Method " + methodName + " not found in the file.");
			return false;
		}
	}

	// This method takes the method name as an argument and returns its body as a
	// String.
	public static String fetchBody(String filePath, String methodName) {
		StringBuilder methodBody = new StringBuilder();
		boolean methodFound = false;
		boolean inMethodBody = false;
		int openBracesCount = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Check if the method is found by matching method signature
				if (line.contains("public CustomResponse " + methodName + "(")
						|| line.contains("public String " + methodName + "(")
						|| line.contains("public Response " + methodName + "(")) {
					methodFound = true;
				}

				// Once the method is found, start capturing lines
				if (methodFound) {
					if (line.contains("{")) {
						inMethodBody = true;
						openBracesCount++;
					}

					// Capture the method body
					if (inMethodBody) {
						methodBody.append(line).append("\n");
					}

					// Check for closing braces to identify the end of the method
					if (line.contains("}")) {
						openBracesCount--;
						if (openBracesCount == 0) {
							break; // End of method body
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return methodBody.toString();
	}

	public static boolean validateResponseFields(String methodName, CustomResponse customResponse)
			throws JsonMappingException, JsonProcessingException {
		boolean isValid = true;

		switch (methodName) {

		case "getAllDepartments":
			// Get the fields directly from the CustomResponse object
			List<Object> departmentIds = customResponse.getPatientIds();
			List<Object> departmentNames = customResponse.getPatientCodes();

			// Validate that DepartmentId and DepartmentName are not null
			for (int i = 0; i < departmentIds.size(); i++) {
				if (departmentIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: DepartmentId at index " + i);
				}
				if (departmentNames.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: DepartmentName at index " + i);
				}
			}

			// Check for uniqueness of DepartmentId
			Set<Object> uniqueDepartmentIds = new HashSet<>(departmentIds);
			if (uniqueDepartmentIds.size() != departmentIds.size()) {
				isValid = false;
				System.out.println("DepartmentId values are not unique.");
			}
			break;

		case "getAllItems":
			// Get the fields directly from the CustomResponse object
			List<Object> itemIds = customResponse.getPatientIds();
			List<Object> itemNames = customResponse.getPatientCodes();

			// Validate that ItemId and ItemName are not null
			for (int i = 0; i < itemIds.size(); i++) {
				if (itemIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: ItemId at index " + i);
				}
				if (itemNames.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: ItemName at index " + i);
				}
			}

			// Check for uniqueness of ItemId
			Set<Object> uniqueItemIds = new HashSet<>(itemIds);
			if (uniqueItemIds.size() != itemIds.size()) {
				isValid = false;
				System.out.println("ItemId values are not unique.");
			}
			break;

		case "getIncentiveSummaryReport":
			// Define the required fields for each incentive record
			List<String> expectedIncentiveFields = List.of("PrescriberName", "PrescriberId", "DocTotalAmount",
					"TDSAmount", "NetPayableAmount");

			// Get the JsonData from the response
			String jsonDataString = customResponse.getResponse().jsonPath().getString("Results.JsonData");

			// Deserialize the stringified JSON into a List of Maps
			List<Map<String, Object>> incentiveResults = new ObjectMapper().readValue(jsonDataString,
					new TypeReference<List<Map<String, Object>>>() {
					});

			// Check if JsonData is null or empty
			if (incentiveResults == null || incentiveResults.isEmpty()) {
				isValid = false;
				System.out.println("JsonData section is missing or empty in the response.");
				break;
			}

			// Validate that each incentive record contains the required fields
			for (int i = 0; i < incentiveResults.size(); i++) {
				Map<String, Object> incentive = incentiveResults.get(i);
				for (String field : expectedIncentiveFields) {
					if (!incentive.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in JsonData[" + i + "]: " + field);
					}
				}
			}

			// Validate the top-level status field
			String incentiveStatusField = customResponse.getResponse().jsonPath().getString("Status");
			if (incentiveStatusField == null || !incentiveStatusField.equals("OK")) {
				isValid = false;
				System.out.println("Status field is missing or invalid in the response.");
			}
			break;

		case "getIncentiveReffSummary":
			// Define expected fields for incentive referral summary
			List<String> expectedIncentiveFields1 = List.of("PrescriberName", "PrescriberId", "DocTotalAmount",
					"TDSAmount", "NetPayableAmount");

			// Extract the JsonData string from the response
			String jsonDataString1 = customResponse.getResponse().jsonPath().getString("Results.JsonData");

			// Check if JsonData is empty or null
			if (jsonDataString1 == null || jsonDataString1.isEmpty()) {
				isValid = false;
				System.out.println("JsonData section is missing or empty in the response.");
				break;
			}

			// Parse the JsonData string into a List of Maps
			List<Map<String, Object>> incentiveResults1 = new ObjectMapper().readValue(jsonDataString1,
					new TypeReference<List<Map<String, Object>>>() {
					});

			// Initialize lists to hold individual field values
			List<String> prescriberNames = new ArrayList<>();
			List<Integer> prescriberIds = new ArrayList<>();
			List<Double> docTotalAmounts = new ArrayList<>();
			List<Double> tdsAmounts = new ArrayList<>();
			List<Double> netPayableAmounts = new ArrayList<>();

			// Validate each incentive record entry
			for (int i = 0; i < incentiveResults1.size(); i++) {
				Map<String, Object> incentive = incentiveResults1.get(i);

				// Add the required fields to the corresponding lists
				prescriberNames.add((String) incentive.get("PrescriberName"));
				prescriberIds.add((Integer) incentive.get("PrescriberId"));
				docTotalAmounts.add((Double) incentive.get("DocTotalAmount"));
				tdsAmounts.add((Double) incentive.get("TDSAmount"));
				netPayableAmounts.add((Double) incentive.get("NetPayableAmount"));

				// Validate that the expected fields exist in the incentive record
				for (String field : expectedIncentiveFields1) {
					if (!incentive.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in JsonData[" + i + "]: " + field);
					}
				}
			}

			// Validate top-level status field
			String incentiveStatusField1 = customResponse.getResponse().jsonPath().getString("Status");
			if (incentiveStatusField1 == null || !incentiveStatusField1.equals("OK")) {
				isValid = false;
				System.out.println("Status field is missing or invalid in the response.");
			}

			// If all validations passed, print out the lists
			if (isValid) {
				System.out.println("Prescriber Names: " + prescriberNames);
				System.out.println("Prescriber Ids: " + prescriberIds);
				System.out.println("DocTotal Amounts: " + docTotalAmounts);
				System.out.println("TDS Amounts: " + tdsAmounts);
				System.out.println("Net Payable Amounts: " + netPayableAmounts);
			}

			break;

		case "getHospIncIncReport":
			// Declare the isValid variable at the start
			boolean isValid1 = true;

			// Validate the "Status" field is present and equals "OK"
			String status = customResponse.getStatus();
			if (status == null || !status.equals("OK")) {
				System.out.println("Status field is missing or invalid.");
				isValid1 = false;
			}

			// Validate the "Results" field is present and not empty
			List<Object> serviceDepartmentIds = customResponse.getServiceDepartmentIds();
			if (serviceDepartmentIds == null || serviceDepartmentIds.isEmpty()) {
				System.out.println("ServiceDepartmentIds field is missing or empty.");
				isValid1 = false;
			}

			List<Object> serviceDepartmentNames = customResponse.getServiceDepartmentNames();
			List<Object> netSales = customResponse.getNetSales();
			List<Object> referralCommissions = customResponse.getReferralCommissions();
			List<Object> grossIncomes = customResponse.getGrossIncomes();
			List<Object> otherIncentives = customResponse.getOtherIncentives();
			List<Object> hospitalNetIncomes = customResponse.getHospitalNetIncomes();

			// Validate that all fields are present and not empty for each record
			if (serviceDepartmentNames == null || serviceDepartmentNames.isEmpty() || netSales == null
					|| netSales.isEmpty() || referralCommissions == null || referralCommissions.isEmpty()
					|| grossIncomes == null || grossIncomes.isEmpty() || otherIncentives == null
					|| otherIncentives.isEmpty() || hospitalNetIncomes == null || hospitalNetIncomes.isEmpty()) {
				isValid1 = false;
				System.out.println("Required fields are missing or empty in the response.");
			} else {
				// Validate each record in the list
				for (int i = 0; i < serviceDepartmentIds.size(); i++) {
					// Check that each field is not null or empty for each record
					if (serviceDepartmentIds.get(i) == null || ((String) serviceDepartmentIds.get(i)).isEmpty()) {
						isValid1 = false;
						System.out.println("ServiceDepartmentId is missing or empty at index " + i);
					}
					if (serviceDepartmentNames.get(i) == null || ((String) serviceDepartmentNames.get(i)).isEmpty()) {
						isValid1 = false;
						System.out.println("ServiceDepartmentName is missing or empty at index " + i);
					}
					if (netSales.get(i) == null || ((String) netSales.get(i)).isEmpty()) {
						isValid1 = false;
						System.out.println("NetSales is missing or empty at index " + i);
					}
					if (referralCommissions.get(i) == null || ((String) referralCommissions.get(i)).isEmpty()) {
						isValid1 = false;
						System.out.println("ReferralCommission is missing or empty at index " + i);
					}
					if (grossIncomes.get(i) == null || ((String) grossIncomes.get(i)).isEmpty()) {
						isValid1 = false;
						System.out.println("GrossIncome is missing or empty at index " + i);
					}
					if (otherIncentives.get(i) == null || ((String) otherIncentives.get(i)).isEmpty()) {
						isValid1 = false;
						System.out.println("OtherIncentive is missing or empty at index " + i);
					}
					if (hospitalNetIncomes.get(i) == null || ((String) hospitalNetIncomes.get(i)).isEmpty()) {
						isValid1 = false;
						System.out.println("HospitalNetIncome is missing or empty at index " + i);
					}
				}
			}

			// After the validation, check if all fields were valid
			if (isValid1) {
				System.out.println("All fields are valid.");
			} else {
				System.out.println("Some fields are invalid.");
			}

			break;

		case "getEmpBillItem":
			// Validate that the response contains the necessary fields and structure
			System.out.println("1");
			if (customResponse == null || customResponse.getResponse() == null) {
				return false; // Response or its content is null
			}

			System.out.println("2");
			// Validate the "Results" field is not null
			Map<String, Object> result = customResponse.getMapResults(); // getResult() now returns a Map, not a List
			if (result == null || result.isEmpty()) {
				return false; // Results field is either null or empty
			}

			System.out.println("3");
			// Validate the fields within the "Results" map
			if (result.get("EmployeeIncentiveInfoId") == null || result.get("EmployeeId") == null
					|| result.get("FullName") == null || result.get("TDSPercent") == null
					|| result.get("EmpTDSPercent") == null || result.get("IsActive") == null
					|| result.get("EmployeeBillItemsMap") == null) {
				return false; // Any of the fields are null, validation fails
			}

			// Additional validation to check if "EmployeeBillItemsMap" is not null or empty
			List<Map<String, Object>> employeeBillItemsMap = (List<Map<String, Object>>) result
					.get("EmployeeBillItemsMap");
			if (employeeBillItemsMap == null || employeeBillItemsMap.isEmpty()) {
				// If EmployeeBillItemsMap is an empty list, it is allowed per the example
				// response
				System.out.println("EmployeeBillItemsMap is empty or null, which is allowed.");
			}

			// If all checks pass, return true
			return true;

		case "getInvntryFiscalYrs":
			isValid1 = validateGetInvntryFiscalYrsFields(customResponse);
			break;

		case "getActInventory":
			// Validate the extracted fields in the response
			List<Object> storeIds = customResponse.getItemIds();
			List<Object> names = customResponse.getItemNames();
			List<Object> storeDescriptions = customResponse.getGenericNames();

			if (storeIds != null && !storeIds.isEmpty() && names != null && !names.isEmpty()
					&& storeDescriptions != null && !storeDescriptions.isEmpty()) {
				// Check each store and validate required fields
				for (int i = 0; i < storeIds.size(); i++) {
					Integer storeId = (Integer) storeIds.get(i);
					String name = (String) names.get(i);
					String storeDescription = (String) storeDescriptions.get(i);

					// Check that StoreId, Name, and StoreDescription are not null
					if (storeId == null || name == null || storeDescription == null) {
						System.out.println("Validation failed for StoreId, Name, or StoreDescription being null.");
						return false;
					}

					// Optionally print out for debugging
					System.out.println("StoreId: " + storeId);
					System.out.println("Name: " + name);
					System.out.println("StoreDescription: " + storeDescription);
					System.out.println();
				}
				// All validations passed for the store list
				isValid1 = true;
			} else {
				System.out.println("Store details (StoreId, Name, StoreDescription) are missing or empty.");
				return false;
			}

			// Check if Status field is present and is "OK"
			String status1 = customResponse.getStatus();
			if (status1 == null || !status1.equals("OK")) {
				System.out.println("Status is not OK or is missing.");
				return false;
			}

			break;

		case "getInvSubCat":
			// Validate the "SubCategoryId" and "SubCategoryName" fields in the response
			List<Object> subCategoryIds = customResponse.getPatientIds();
			List<Object> subCategoryNames = customResponse.getPatientCodes();

			if (subCategoryIds != null && !subCategoryIds.isEmpty() && subCategoryNames != null
					&& !subCategoryNames.isEmpty()) {
				// Check each subcategory and validate required fields
				for (int i = 0; i < subCategoryIds.size(); i++) {
					Integer subCategoryId = (Integer) subCategoryIds.get(i);
					String subCategoryName = (String) subCategoryNames.get(i);

					// Check that SubCategoryId and SubCategoryName are not null
					if (subCategoryId == null || subCategoryName == null) {
						System.out.println("Validation failed for SubCategoryId or SubCategoryName being null.");
						return false;
					}

					// Optionally print out for debugging
					System.out.println("SubCategoryId: " + subCategoryId);
					System.out.println("SubCategoryName: " + subCategoryName);
					System.out.println();
				}
				// All validations passed for the subcategories list
				isValid1 = true;
			} else {
				System.out.println("SubCategoryId or SubCategoryName is missing or empty.");
				return false;
			}

			// Check if Status field is present and is "OK"
			String status11 = customResponse.getStatus();
			if (status11 == null || !status11.equals("OK")) {
				System.out.println("Status is not OK or is missing.");
				return false;
			}

			break;

		case "availableItems":
			// Validate that "Results" is not null and is a map
			Map<String, Object> results = customResponse.getMapResults();
			if (results == null || results.isEmpty()) {
				System.err.println("Error: Results field is null or empty.");
				isValid1 = false;
			} else {
				// Validate that all expected fields are present and not null
				if (!results.containsKey("ItemId") || results.get("ItemId") == null) {
					System.err.println("Error: ItemId is null or missing in Results.");
					isValid1 = false;
				}
				if (!results.containsKey("AvailableQuantity") || results.get("AvailableQuantity") == null) {
					System.err.println("Error: AvailableQuantity is null or missing in Results.");
					isValid1 = false;
				}
				if (!results.containsKey("StoreId") || results.get("StoreId") == null) {
					System.err.println("Error: StoreId is null or missing in Results.");
					isValid1 = false;
				}
			}
			break;

		case "requisitionItems":
			// Extract the "Requisition" field from the response
			Map<String, Object> requisitionDetails = customResponse.getMapResults();
			if (requisitionDetails == null) {
				System.out.println("Requisition field is missing or null.");
				isValid1 = false;
				break;
			}

			// Validate the "Requisition" field details
			String createdByName = (String) requisitionDetails.get("CreatedByName");
			Integer requisitionNo = (Integer) requisitionDetails.get("RequisitionNo");
			String requisitionStatus = (String) requisitionDetails.get("RequisitionStatus");

			if (createdByName == null) {
				System.out.println("CreatedByName is missing or null.");
				isValid1 = false;
			}
			if (requisitionNo == null) {
				System.out.println("RequisitionNo is missing or null.");
				isValid1 = false;
			}
			if (requisitionStatus == null) {
				System.out.println("RequisitionStatus is missing or null.");
				isValid1 = false;
			}

			// Extract and validate the "RequisitionItems" list
			List<Map<String, Object>> requisitionItemsList = (List<Map<String, Object>>) requisitionDetails
					.get("RequisitionItems");
			if (requisitionItemsList == null || requisitionItemsList.isEmpty()) {
				System.out.println("RequisitionItems list is missing or empty.");
				isValid1 = false;
			} else {
				// Validate each item in the "RequisitionItems" list
				for (Map<String, Object> requisitionItem : requisitionItemsList) {
					String itemName = (String) requisitionItem.get("ItemName");
					String itemCode = (String) requisitionItem.get("Code");
					Float pendingQuantity = (Float) requisitionItem.get("PendingQuantity");
					String itemStatus = (String) requisitionItem.get("RequisitionItemStatus");

					if (itemName == null) {
						System.out.println("ItemName is missing or null in requisition item.");
						isValid1 = false;
					}
					if (itemCode == null) {
						System.out.println("Code is missing or null in requisition item.");
						isValid1 = false;
					}
					if (pendingQuantity == null) {
						System.out.println("PendingQuantity is missing or null in requisition item.");
						isValid1 = false;
					}
					if (itemStatus == null) {
						System.out.println("RequisitionItemStatus is missing or null in requisition item.");
						isValid1 = false;
					}
				}
			}
			break;

		case "verifyRequisitionAndDispatchFields":
			// Extract the "Requisition" field from the response
			Map<String, Object> responseResults = customResponse.getMapResults();
			if (responseResults == null) {
				System.out.println("Error: 'Results' field is null.");
				isValid1 = false;
				break;
			}

			// Validate RequisitionId
			Integer requisitionId = (Integer) responseResults.get("RequisitionId");
			if (requisitionId == null) {
				System.out.println("Error: 'RequisitionId' is null.");
				isValid1 = false;
			} else {
				System.out.println("RequisitionId: " + requisitionId);
			}

			// Validate CreatedBy
			String createdBy = (String) responseResults.get("CreatedBy");
			if (createdBy == null || createdBy.isEmpty()) {
				System.out.println("Error: 'CreatedBy' is null or empty.");
				isValid1 = false;
			} else {
				System.out.println("CreatedBy: " + createdBy);
			}

			// Validate Status
			String reqStat = (String) responseResults.get("Status");
			if (reqStat == null || reqStat.isEmpty()) {
				System.out.println("Error: 'Status' is null or empty.");
				isValid1 = false;
			} else {
				System.out.println("Requisition Status: " + reqStat);
			}

			// Validate Dispatchers array
			List<Map<String, Object>> dispatchersList = (List<Map<String, Object>>) responseResults.get("Dispatchers");
			if (dispatchersList == null || dispatchersList.isEmpty()) {
				System.out.println("Error: 'Dispatchers' list is null or empty.");
				isValid1 = false;
			} else {
				for (Map<String, Object> dispatcher : dispatchersList) {
					// Validate DispatchId
					Integer dispatchId = (Integer) dispatcher.get("DispatchId");
					if (dispatchId == null) {
						System.out.println("Error: 'DispatchId' is null.");
						isValid1 = false;
					} else {
						System.out.println("DispatchId: " + dispatchId);
					}

					// Validate Name
					String dispatcherName = (String) dispatcher.get("Name");
					if (dispatcherName == null || dispatcherName.isEmpty()) {
						System.out.println("Error: 'Name' is null or empty.");
						isValid1 = false;
					} else {
						System.out.println("Dispatcher Name: " + dispatcherName);
					}
				}
			}
			break;

		case "getInvItem":
			// Validate the "Results" field structure in the response
			List<Object> itemIds1 = customResponse.getItemIds();
			List<Object> itemNames1 = customResponse.getItemNames();
			List<Object> availableQuantities = customResponse.getGenericNames();

			if (itemIds1 == null || itemIds1.isEmpty()) {
				System.out.println("Error: ItemIds are missing or empty.");
				isValid1 = false;
			}

			// Loop through each inventory item and validate required fields
			for (int i = 0; i < itemIds1.size(); i++) {
				Integer inventoryId = (Integer) itemIds1.get(i);
				String inventoryName = (String) itemNames1.get(i);
				Float availableStock = (Float) availableQuantities.get(i);

				// Validate each required field
				if (inventoryId == null) {
					System.out.println("ItemId is missing or null.");
					isValid1 = false;
				}
				if (inventoryName == null || inventoryName.isEmpty()) {
					System.out.println("ItemName is missing or empty.");
					isValid1 = false;
				}
				if (availableStock == null) {
					System.out.println("AvailableQuantity is missing or null.");
					isValid1 = false;
				} else if (availableStock <= 0) {
					System.out.println("AvailableQuantity should be greater than zero.");
					isValid1 = false;
				}

				// Print values for debugging purposes
				System.out.println("Validated Item: " + inventoryName + " [ItemId: " + inventoryId
						+ ", AvailableQuantity: " + availableStock + "]");
			}
			break;

		case "getMostSoldMed":
			// Validate the "itemNames" and "soldQuantities" fields
			List<Object> itemNames11 = customResponse.getPatientIds();
			List<Object> soldQuantities = customResponse.getPatientCodes();

			// Ensure "itemNames" and "soldQuantities" are not null or empty
			if (itemNames11 == null || itemNames11.isEmpty()) {
				System.out.println("Error: ItemNames field is missing or empty.");
				isValid1 = false;
			}

			if (soldQuantities == null || soldQuantities.isEmpty()) {
				System.out.println("Error: SoldQuantities field is missing or empty.");
				isValid1 = false;
			}

			// Validate each item and quantity
			for (int i = 0; i < itemNames11.size(); i++) {
				String itemName = (String) itemNames11.get(i);
				Float soldQuantity = (Float) soldQuantities.get(i);

				// Validate each required field
				if (itemName == null || itemName.isEmpty()) {
					System.out.println("Error: ItemName is missing or empty.");
					isValid1 = false;
				}

				if (soldQuantity == null) {
					System.out.println("Error: SoldQuantity is missing or null.");
					isValid1 = false;
				} else if (soldQuantity <= 0) {
					System.out.println("Error: SoldQuantity should be greater than zero.");
					isValid1 = false;
				}

				// Print values for debugging purposes
				System.out.println("Validated Medicine: " + itemName + " [SoldQuantity: " + soldQuantity + "]");
			}
			break;

		case "getSubDisp":
			// Validate the "storeNames" and "totalDispatchValues" fields in the response
			List<Object> storeNames = customResponse.getPatientIds();
			List<Object> totalDispatchValues = customResponse.getPatientCodes();

			// Ensure "storeNames" and "totalDispatchValues" are not null or empty
			if (storeNames == null || storeNames.isEmpty()) {
				System.out.println("Error: StoreNames field is missing or empty.");
				isValid1 = false;
			}

			if (totalDispatchValues == null || totalDispatchValues.isEmpty()) {
				System.out.println("Error: TotalDispatchValues field is missing or empty.");
				isValid1 = false;
			}

			// Validate each store and dispatch value
			for (int i = 0; i < storeNames.size(); i++) {
				String storeName = (String) storeNames.get(i);
				Float dispatchValue = (Float) totalDispatchValues.get(i);

				// Validate each required field
				if (storeName == null || storeName.isEmpty()) {
					System.out.println("Error: StoreName is missing or empty.");
					isValid1 = false;
				}

				if (dispatchValue == null) {
					System.out.println("Error: TotalDispatchValue is missing or null.");
					isValid1 = false;
				} else if (dispatchValue <= 0) {
					System.out.println("Error: TotalDispatchValue should be greater than zero.");
					isValid1 = false;
				}

				// Print values for debugging purposes
				System.out.println("Validated Store: " + storeName + " [DispatchValue: " + dispatchValue + "]");
			}
			break;

		case "getActSupp":
			// Extracting the results list from the response
			List<Object> supplierIds = customResponse.getPatientIds();
			List<Object> supplierNames = customResponse.getPatientCodes();

			if (supplierIds == null || supplierIds.isEmpty()) {
				System.out.println("Error: SupplierIds field is missing or empty.");
				isValid1 = false;
			}

			if (supplierNames == null || supplierNames.isEmpty()) {
				System.out.println("Error: SupplierNames field is missing or empty.");
				isValid1 = false;
			}

			// Loop through each supplier and validate required fields
			for (int i = 0; i < supplierIds.size(); i++) {
				Integer supplierId = (Integer) supplierIds.get(i);
				String supplierName = (String) supplierNames.get(i);

				// Validate SupplierId
				if (supplierId == null) {
					System.out.println("SupplierId is missing or null.");
					isValid1 = false;
				}

				// Validate SupplierName
				if (supplierName == null || supplierName.isEmpty()) {
					System.out.println("SupplierName is missing or empty.");
					isValid1 = false;
				}

				// Print values for debugging
				System.out.println("Validated Supplier: " + supplierName + " [SupplierId: " + supplierId + "]");
			}
			break;

		case "getMeasureUnits":
			// Extracting the lists of UOMIds and UOMNames from the response
			List<Object> uomIds = customResponse.getPatientIds();
			List<Object> uomNames = customResponse.getPatientCodes();

			// Validate UOMIds and UOMNames lists
			if (uomIds == null || uomIds.isEmpty()) {
				System.out.println("Error: UOMIds field is missing or empty.");
				isValid1 = false;
			}

			if (uomNames == null || uomNames.isEmpty()) {
				System.out.println("Error: UOMNames field is missing or empty.");
				isValid1 = false;
			}

			// Loop through each UOM and validate required fields
			for (int i = 0; i < uomIds.size(); i++) {
				Integer uomId = (Integer) uomIds.get(i);
				String uomName = (String) uomNames.get(i);

				// Validate UOMId
				if (uomId == null) {
					System.out.println("Error: UOMId is missing or null.");
					isValid1 = false;
				}

				// Validate UOMName
				if (uomName == null || uomName.isEmpty()) {
					System.out.println("Error: UOMName is missing or empty.");
					isValid1 = false;
				}

				// Print values for debugging purposes
				System.out.println("Validated UOM: " + uomName + " [UOMId: " + uomId + "]");
			}
			break;

		case "getSalesCat":
			// Extract SalesCategoryId and SalesCategoryName from the response
			List<Object> salesCategoryIds = customResponse.getPatientIds();
			List<Object> salesCategoryNames = customResponse.getPatientCodes();

			// Validate SalesCategoryId and SalesCategoryName lists
			if (salesCategoryIds == null || salesCategoryIds.isEmpty()) {
				System.out.println("Error: SalesCategoryIds field is missing or empty.");
				isValid1 = false;
			}

			if (salesCategoryNames == null || salesCategoryNames.isEmpty()) {
				System.out.println("Error: SalesCategoryNames field is missing or empty.");
				isValid1 = false;
			}

			// Loop through each SalesCategoryId and SalesCategoryName and validate required
			// fields
			for (int i = 0; i < salesCategoryIds.size(); i++) {
				Integer salesCategoryId = (Integer) salesCategoryIds.get(i);
				String salesCategoryName = (String) salesCategoryNames.get(i);

				// Validate SalesCategoryId
				if (salesCategoryId == null) {
					System.out.println("Error: SalesCategoryId is missing or null.");
					isValid1 = false;
				}

				// Validate SalesCategoryName
				if (salesCategoryName == null || salesCategoryName.isEmpty()) {
					System.out.println("Error: SalesCategoryName is missing or empty.");
					isValid1 = false;
				}

				// Print values for debugging purposes
				System.out.println("Validated Sales Category: " + salesCategoryName + " [SalesCategoryId: "
						+ salesCategoryId + "]");
			}
			break;

		default:
			System.out.println("Method " + methodName + " is not recognized for validation.");
			isValid1 = false;
		}
		return isValid;
	}

	private void checkFieldNotNull(List<String> list, int index, String fieldName, boolean[] isValid) {
		if (list == null || list.size() <= index || list.get(index) == null) {
			isValid[0] = false; // Modify isValid by passing it as an array (because primitives are passed by
								// value)
			System.out.println(fieldName + " is missing or null at index " + index);
		}
	}

	// Helper method to check that a field is not null
	private static void checkFieldNotNull(Map<String, Object> result, String fieldName) {
		if (result.get(fieldName) == null) {
			System.out.println(fieldName + " should not be null.");
		}
	}

	private static boolean validateGetAllDepartmentsFields(CustomResponse customResponse) {
		// Example of validation for "getAllDepartments"
		List<Map<String, Object>> results = customResponse.getListResults();
		if (results == null || results.isEmpty()) {
			return false;
		}

		for (Map<String, Object> department : results) {
			if (department.get("DepartmentId") == null || department.get("DepartmentName") == null) {
				return false;
			}
		}

		return true;
	}

	private static boolean validateGetInvntryFiscalYrsFields(CustomResponse customResponse) {
		// Validate the "Results" field in the response
		List<Map<String, Object>> results = customResponse.getListResults();
		if (results == null || results.isEmpty()) {
			return false; // Results field is either null or empty
		}

		// Loop through each fiscal year and validate required fields
		for (Map<String, Object> fiscalYear : results) {
			// Validate each field for "FiscalYearId", "FiscalYearName", "StartDate",
			// "EndDate", and "IsActive"
			if (fiscalYear.get("FiscalYearId") == null || fiscalYear.get("FiscalYearName") == null
					|| fiscalYear.get("StartDate") == null || fiscalYear.get("EndDate") == null
					|| fiscalYear.get("IsActive") == null) {
				return false; // Any of the fields are null, validation fails
			}
		}

		return true;
	}

}