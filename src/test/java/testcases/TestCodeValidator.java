package testcases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
			// Define expected fields for departments
			List<String> expectedDepartmentFields = List.of("DepartmentId", "DepartmentName");

			// Extract the list of departments from the response
			List<Map<String, Object>> departmentResults = customResponse.getResponse().jsonPath().getList("Results");
			if (departmentResults == null || departmentResults.isEmpty()) {
				isValid = false;
				System.out.println("Results section is missing or empty in the response.");
				break;
			}

			// Validate each department entry
			for (int i = 0; i < departmentResults.size(); i++) {
				Map<String, Object> department = departmentResults.get(i);
				for (String field : expectedDepartmentFields) {
					if (!department.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in Results[" + i + "]: " + field);
					}
				}
			}

			// Validate top-level status field
			String departmentStatusField = customResponse.getResponse().jsonPath().getString("Status");
			if (departmentStatusField == null || !departmentStatusField.equals("OK")) {
				isValid = false;
				System.out.println("Status field is missing or invalid in the response.");
			}
			break;

		case "getAllItems":
			// Define the required fields for each item in Results
			List<String> expectedItemFields = List.of("ItemId", "ItemName");

			List<Map<String, Object>> itemResults = customResponse.getResponse().jsonPath().getList("Results");
			if (itemResults == null || itemResults.isEmpty()) {
				isValid = false;
				System.out.println("Results section is missing or empty in the response.");
				break;
			}

			// Validate that each item contains the required fields
			for (int i = 0; i < itemResults.size(); i++) {
				Map<String, Object> item = itemResults.get(i);
				for (String field : expectedItemFields) {
					if (!item.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in Results[" + i + "]: " + field);
					}
				}
			}

			// Validate the top-level status field
			String itemStatusField = customResponse.getResponse().jsonPath().getString("Status");
			if (itemStatusField == null || !itemStatusField.equals("OK")) {
				isValid = false;
				System.out.println("Status field is missing or invalid in the response.");
			}
			break;

		case "getIncentiveSummaryReport":
			// Define the required fields for each incentive record
			List<String> expectedIncentiveFields = List.of("PrescriberName", "PrescriberId", "DocTotalAmount",
					"TDSAmount", "NetPayableAmount");

			// Get the stringified JSON from the response's JsonData field
			String jsonDataString = customResponse.getResponse().jsonPath().getString("Results.JsonData");

			// Deserialize the stringified JSON into a List of Maps
			List<Map<String, Object>> incentiveResults = new ObjectMapper().readValue(jsonDataString,
					new TypeReference<List<Map<String, Object>>>() {
					});

			// Iterate through the deserialized List of Maps
			for (Map<String, Object> map : incentiveResults) {
				for (String key : map.keySet()) {
					System.out.println(key + ": " + map.get(key));
				}
			}

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
			List<String> expectedIncentiveFieldss = List.of("PrescriberName", "PrescriberId", "DocTotalAmount",
					"TDSAmount", "NetPayableAmount");

			// Extract the JsonData string from the response
			String jsonDataStrings = customResponse.getResponse().jsonPath().getString("Results.JsonData");

			// Check if JsonData is empty or null
			if (jsonDataStrings == null || jsonDataStrings.isEmpty()) {
				isValid = false;
				System.out.println("JsonData section is missing or empty in the response.");
				break;
			}

			// Parse the JsonData string into a List of Maps
			List<Map<String, Object>> incentiveResultss = new ObjectMapper().readValue(jsonDataStrings,
					new TypeReference<List<Map<String, Object>>>() {
					});

			// Validate each incentive record entry
			for (int i = 0; i < incentiveResultss.size(); i++) {
				Map<String, Object> incentive = incentiveResultss.get(i);
				for (String field : expectedIncentiveFieldss) {
					if (!incentive.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in JsonData[" + i + "]: " + field);
					}
				}
			}

			// Validate top-level status field
			String incentiveStatusFields = customResponse.getResponse().jsonPath().getString("Status");
			if (incentiveStatusFields == null || !incentiveStatusFields.equals("OK")) {
				isValid = false;
				System.out.println("Status field is missing or invalid in the response.");
			}
			break;

		case "getHospIncIncReport":
			// Validate the "Status" field is present and equals "OK"
			String status = customResponse.getStatus();
			if (status == null || !status.equals("OK")) {
				System.out.println("Status field is missing or invalid.");
				isValid = false;
			}

			// Validate the "Results" field is present and not empty
			List<Map<String, Object>> results = customResponse.getListResults();
			if (results == null || results.isEmpty()) {
				System.out.println("Results field is missing or empty.");
				isValid = false;
			} else {
				// Validate each record in the results list
				for (Map<String, Object> result : results) {
					// Check that each required field is not null
					checkFieldNotNull(result, "ServiceDepartmentId");
					checkFieldNotNull(result, "ServiceDepartmentName");
					checkFieldNotNull(result, "NetSales");
					checkFieldNotNull(result, "ReferralCommission");
					checkFieldNotNull(result, "GrossIncome");
					checkFieldNotNull(result, "OtherIncentive");
					checkFieldNotNull(result, "HospitalNetIncome");
				}
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
			isValid = validateGetInvntryFiscalYrsFields(customResponse);
			break;

		case "getActInventory":
			// Validate the "Results" field in the response
			List<Map<String, Object>> res = customResponse.getListResults();
			if (res != null && !res.isEmpty()) {
				// Check each store object in the Results
				for (Map<String, Object> store : res) {
					// Validate that required fields are present and not null
					if (store.containsKey("StoreId") && store.containsKey("Name")
							&& store.containsKey("StoreDescription")) {
						Integer storeId = (Integer) store.get("StoreId");
						String name = (String) store.get("Name");
						String storeDescription = (String) store.get("StoreDescription");

						// Check that StoreId, Name, and StoreDescription are not null
						if (storeId == null || name == null || storeDescription == null) {
							System.out.println("Validation failed for StoreId, Name, or StoreDescription being null.");
							return false;
						}
					} else {
						System.out.println("Store object is missing required fields.");
						return false;
					}
				}
				// All validations passed for the Results list
				isValid = true;
			} else {
				System.out.println("Results field is null or empty.");
				return false;
			}

			// Check if Status field is present and is "OK"
			String statuss = customResponse.getStatus();
			if (statuss == null || !statuss.equals("OK")) {
				System.out.println("Status is not OK or is missing.");
				return false;
			}

			break;

		case "getInvSubCat":
			// Check if the status is OK
			String stat = customResponse.getStatus();
			if (!"OK".equalsIgnoreCase(stat)) {
				System.err.println("Error: Status is not OK for getInvSubCat.");
				return false;
			}

			// Check if the Results field is present and is a list
			List<Map<String, Object>> re = customResponse.getListResults();
			if (re == null || re.isEmpty()) {
				System.err.println("Error: Results field is missing or empty for getInvSubCat.");
				return false;
			}

			// Validate that each result contains "SubCategoryId" and "SubCategoryName"
			for (Map<String, Object> subCategory : re) {
				if (!subCategory.containsKey("SubCategoryId") || subCategory.get("SubCategoryId") == null) {
					System.err.println("Error: Missing or null SubCategoryId in result.");
					return false;
				}
				if (!subCategory.containsKey("SubCategoryName") || subCategory.get("SubCategoryName") == null) {
					System.err.println("Error: Missing or null SubCategoryName in result.");
					return false;
				}
			}

			// If all validations pass, return true
			isValid = true;
			break;

		case "availableItems":
			// Validate the response structure for availableItems endpoint

			// Validate that "Results" is not null and is a Map
			if (customResponse.getMapResults() == null || customResponse.getMapResults().isEmpty()) {
				System.out.println("Error: Results field is null or empty.");
				isValid = false;
			} else {
				Map<String, Object> r = customResponse.getMapResults();

				// Validate that all expected fields are present and not null
				if (!r.containsKey("ItemId") || r.get("ItemId") == null) {
					System.out.println("Error: ItemId is null or missing in Results.");
					isValid = false;
				}
				if (!r.containsKey("AvailableQuantity") || r.get("AvailableQuantity") == null) {
					System.out.println("Error: AvailableQuantity is null or missing in Results.");
					isValid = false;
				}
				if (!r.containsKey("StoreId") || r.get("StoreId") == null) {
					System.out.println("Error: StoreId is null or missing in Results.");
					isValid = false;
				}
			}
			break;

		case "requisitionItems":
			// Extract the "Results" field from the response
			Map<String, Object> responseData = customResponse.getMapResults();
			if (responseData == null) {
				System.out.println("Results field is missing or null in the response.");
				isValid = false;
				break;
			}

			// Extract the "Requisition" field from the "Results" field
			Map<String, Object> requisitionDetails = (Map<String, Object>) responseData.get("Requisition");
			if (requisitionDetails == null) {
				System.out.println("Requisition field is missing or null.");
				isValid = false;
				break;
			}

			// Validate "Requisition" field details
			String createdByName = (String) requisitionDetails.get("CreatedByName");
			Integer requisitionNo = (Integer) requisitionDetails.get("RequisitionNo");
			String requisitionStatus = (String) requisitionDetails.get("RequisitionStatus");

			if (createdByName == null) {
				System.out.println("CreatedByName is missing or null.");
				isValid = false;
			}
			if (requisitionNo == null) {
				System.out.println("RequisitionNo is missing or null.");
				isValid = false;
			}
			if (requisitionStatus == null) {
				System.out.println("RequisitionStatus is missing or null.");
				isValid = false;
			}

			// Extract the "RequisitionItems" list from the "Requisition" field
			List<Map<String, Object>> requisitionItemsList = (List<Map<String, Object>>) requisitionDetails
					.get("RequisitionItems");
			if (requisitionItemsList == null || requisitionItemsList.isEmpty()) {
				System.out.println("RequisitionItems list is missing or empty.");
				isValid = false;
			} else {
				// Validate each item in the "RequisitionItems" list
				for (Map<String, Object> requisitionItem : requisitionItemsList) {
					String itemName = (String) requisitionItem.get("ItemName");
					String itemCode = (String) requisitionItem.get("Code");
					Float pendingQuantity = (Float) requisitionItem.get("PendingQuantity");
					String itemStatus = (String) requisitionItem.get("RequisitionItemStatus");

					if (itemName == null) {
						System.out.println("ItemName is missing or null in requisition item.");
						isValid = false;
					}
					if (itemCode == null) {
						System.out.println("Code is missing or null in requisition item.");
						isValid = false;
					}
					if (pendingQuantity == null) {
						System.out.println("PendingQuantity is missing or null in requisition item.");
						isValid = false;
					}
					if (itemStatus == null) {
						System.out.println("RequisitionItemStatus is missing or null in requisition item.");
						isValid = false;
					}
				}
			}
			break;

		case "verifyRequisitionAndDispatchFields":
			// Extract the "Results" field from the response
			Map<String, Object> responseResults = customResponse.getMapResults();
			if (responseResults == null) {
				System.out.println("Error: 'Results' field is null.");
				isValid = false;
			} else {
				// Validate RequisitionId
				Integer requisitionId = (Integer) responseResults.get("RequisitionId");
				if (requisitionId == null) {
					System.out.println("Error: 'RequisitionId' is null.");
					isValid = false;
				} else {
					System.out.println("RequisitionId: " + requisitionId);
				}

				// Validate CreatedBy
				String createdBy = (String) responseResults.get("CreatedBy");
				if (createdBy == null || createdBy.isEmpty()) {
					System.out.println("Error: 'CreatedBy' is null or empty.");
					isValid = false;
				} else {
					System.out.println("CreatedBy: " + createdBy);
				}

				// Validate Status
				String reqStat = (String) responseResults.get("Status");
				if (reqStat == null || reqStat.isEmpty()) {
					System.out.println("Error: 'Status' is null or empty.");
					isValid = false;
				} else {
					System.out.println("Requisition Status: " + reqStat);
				}

				// Validate Dispatchers array
				List<Map<String, Object>> dispatchersList = (List<Map<String, Object>>) responseResults
						.get("Dispatchers");
				if (dispatchersList == null || dispatchersList.isEmpty()) {
					System.out.println("Error: 'Dispatchers' list is null or empty.");
					isValid = false;
				} else {
					for (Map<String, Object> dispatcher : dispatchersList) {
						// Validate DispatchId
						Integer dispatchId = (Integer) dispatcher.get("DispatchId");
						if (dispatchId == null) {
							System.out.println("Error: 'DispatchId' is null.");
							isValid = false;
						} else {
							System.out.println("DispatchId: " + dispatchId);
						}

						// Validate Name
						String dispatcherName = (String) dispatcher.get("Name");
						if (dispatcherName == null || dispatcherName.isEmpty()) {
							System.out.println("Error: 'Name' is null or empty.");
							isValid = false;
						} else {
							System.out.println("Dispatcher Name: " + dispatcherName);
						}
					}
				}
			}
			break;

		case "getInvItem":
			// Validate the "Results" field structure in the response
			List<Map<String, Object>> itemList = customResponse.getListResults();
			if (itemList == null || itemList.isEmpty()) {
				System.out.println("Results field is missing or empty.");
				isValid = false;
			}

			// Loop through each inventory item and validate required fields
			for (Map<String, Object> inventoryItem : itemList) {
				Integer inventoryId = (Integer) inventoryItem.get("ItemId");
				String inventoryName = (String) inventoryItem.get("ItemName");
				Float availableStock = inventoryItem.get("AvailableQuantity") == null ? null
						: Float.parseFloat(inventoryItem.get("AvailableQuantity").toString());
				String stockCode = (String) inventoryItem.get("Code");
				String itemCategory = (String) inventoryItem.get("ItemType");

				// Validate each required field
				if (inventoryId == null) {
					System.out.println("ItemId is missing or null.");
					isValid = false;
				}
				if (inventoryName == null || inventoryName.isEmpty()) {
					System.out.println("ItemName is missing or empty.");
					isValid = false;
				}
				if (availableStock == null) {
					System.out.println("AvailableQuantity is missing or null.");
					isValid = false;
				} else if (availableStock <= 0) {
					System.out.println("AvailableQuantity should be greater than zero.");
					isValid = false;
				}
				if (stockCode == null || stockCode.isEmpty()) {
					System.out.println("Code is missing or empty.");
					isValid = false;
				}
				if (itemCategory == null || itemCategory.isEmpty()) {
					System.out.println("ItemType is missing or empty.");
					isValid = false;
				}

				// Print values for debugging purposes
				System.out.println("Validated Item: " + inventoryName + " [ItemId: " + inventoryId
						+ ", AvailableQuantity: " + availableStock + "]");
			}
			break;

		case "getMostSoldMed":
			// Validate the "Results" field structure in the response
			List<Map<String, Object>> soldMedicineList = customResponse.getListResults();
			if (soldMedicineList == null || soldMedicineList.isEmpty()) {
				System.out.println("Results field is missing or empty.");
				isValid = false;
			}

			// Loop through each sold medicine item and validate required fields
			for (Map<String, Object> medicineItem : soldMedicineList) {
				String medicineName = (String) medicineItem.get("ItemName");
				Float quantitySold = medicineItem.get("SoldQuantity") == null ? null
						: Float.parseFloat(medicineItem.get("SoldQuantity").toString());

				// Validate each required field
				if (medicineName == null || medicineName.isEmpty()) {
					System.out.println("ItemName is missing or empty.");
					isValid = false;
				}
				if (quantitySold == null) {
					System.out.println("SoldQuantity is missing or null.");
					isValid = false;
				} else if (quantitySold <= 0) {
					System.out.println("SoldQuantity should be greater than zero.");
					isValid = false;
				}

				// Print values for debugging purposes
				System.out.println("Validated Medicine: " + medicineName + " [SoldQuantity: " + quantitySold + "]");
			}
			break;

		case "getSubDisp":
			// Validate the "Results" field structure in the response
			List<Map<String, Object>> locationList = customResponse.getListResults();
			if (locationList == null || locationList.isEmpty()) {
				System.out.println("Results field is missing or empty.");
				isValid = false;
			}

			// Loop through each location entry and validate required fields
			for (Map<String, Object> location : locationList) {
				String storeName = (String) location.get("Name");
				Double dispatchValue = location.get("TotalDispatchValue") == null ? null
						: Double.parseDouble(location.get("TotalDispatchValue").toString());

				// Validate each required field
				if (storeName == null || storeName.isEmpty()) {
					System.out.println("Store Name is missing or empty.");
					isValid = false;
				}
				if (dispatchValue == null) {
					System.out.println("TotalDispatchValue is missing or null.");
					isValid = false;
				} else if (dispatchValue <= 0) {
					System.out.println("TotalDispatchValue should be greater than zero.");
					isValid = false;
				}

				// Print values for debugging purposes
				System.out.println("Validated Store: " + storeName + " [DispatchValue: " + dispatchValue + "]");
			}
			break;

		case "getActSupp":
			// Extracting the results list from the response
			List<Map<String, Object>> supplierList = customResponse.getListResults();

			if (supplierList == null || supplierList.isEmpty()) {
				System.out.println("Results field is missing or empty.");
				isValid = false;
			}

			// Looping through each supplier and validating required fields
			for (Map<String, Object> supplierDetails : supplierList) {
				Integer supplierIdentification = (Integer) supplierDetails.get("SupplierId");
				String supplierFullName = (String) supplierDetails.get("SupplierName");

				// Validate SupplierId
				if (supplierIdentification == null) {
					System.out.println("SupplierId is missing or null.");
					isValid = false;
				}

				// Validate SupplierName
				if (supplierFullName == null || supplierFullName.isEmpty()) {
					System.out.println("SupplierName is missing or empty.");
					isValid = false;
				}

				// Print values for debugging
				System.out.println(
						"Validated Supplier: " + supplierFullName + " [SupplierId: " + supplierIdentification + "]");
			}
			break;

		case "getMeasureUnits":
			// Validate response for getMeasureUnits API

			// Validate the presence of the 'Status' field and its value
			String statusField = customResponse.getStatus();
			if (statusField == null || !statusField.equals("OK")) {
				System.out.println("Error: 'Status' field is either null or not OK.");
				return false;
			}

			// Validate that 'Results' is a list and it's not empty
			List<Map<String, Object>> resultList = customResponse.getListResults();
			if (resultList == null || resultList.isEmpty()) {
				System.out.println("Error: 'Results' field is either null or empty.");
				return false;
			}

			// Validate the 'UOMId' and 'UOMName' for each entry in 'Results'
			for (Map<String, Object> unitOfMeasurement : resultList) {
				// Validate 'UOMId' field
				if (!unitOfMeasurement.containsKey("UOMId") || unitOfMeasurement.get("UOMId") == null) {
					System.out.println("Error: 'UOMId' is missing or null for a unit of measurement.");
					return false;
				}
				if (!(unitOfMeasurement.get("UOMId") instanceof Integer)) {
					System.out.println("Error: 'UOMId' is not an integer for a unit of measurement.");
					return false;
				}

				// Validate 'UOMName' field
				if (!unitOfMeasurement.containsKey("UOMName") || unitOfMeasurement.get("UOMName") == null) {
					System.out.println("Error: 'UOMName' is missing or null for a unit of measurement.");
					return false;
				}
				if (!(unitOfMeasurement.get("UOMName") instanceof String)) {
					System.out.println("Error: 'UOMName' is not a string for a unit of measurement.");
					return false;
				}
			}

			// If all validations pass, return true
			return true;

		case "getSalesCat":
			// Validate that the "Status" field is present and has a value of "OK"
			String statu = customResponse.getStatus();
			if (statu == null || !statu.equals("OK")) {
				System.err.println("Status should be 'OK', but found: " + statu);
				return false;
			}

			// Validate that the response code is 200
			if (customResponse.getStatusCode() != 200) {
				System.err.println("Expected status code 200, but found: " + customResponse.getStatusCode());
				return false;
			}

			// Validate that the "Results" field is not null and not empty
			List<Map<String, Object>> r = customResponse.getListResults();
			if (r == null || r.isEmpty()) {
				System.err.println("Results field should not be null or empty.");
				return false;
			}

			// Validate that each item in the results list has the necessary fields
			for (Map<String, Object> resultItem : r) {
				String categoryName = (String) resultItem.get("Name");
				Integer categoryId = (Integer) resultItem.get("SalesCategoryId");

				if (categoryName == null) {
					System.err.println("Name should not be null in one of the results.");
					return false;
				}

				if (categoryId == null) {
					System.err.println("SalesCategoryId should not be null in one of the results.");
					return false;
				}
			}
			// All validations passed
			return true;

		default:
			System.out.println("Method " + methodName + " is not recognized for validation.");
			isValid = false;
		}
		return isValid;
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
			return false;
		}

		// Loop through each fiscal year and validate required fields
		for (Map<String, Object> fiscalYear : results) {
			// Validate each field for "FiscalYearId", "FiscalYearName", "StartDate",
			// "EndDate", and "IsActive"
			if (fiscalYear.get("FiscalYearId") == null || fiscalYear.get("FiscalYearName") == null
					|| fiscalYear.get("StartDate") == null || fiscalYear.get("EndDate") == null
					|| fiscalYear.get("IsActive") == null) {
				return false;
			}
		}

		return true;
	}

}