package api.payload;

import static io.restassured.RestAssured.given;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.restassured.path.json.JsonPath;
import static io.restassured.RestAssured.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class abc {

	// post-->http://10.41.5.156:8080/aeengine/rest/authenticate?username=john&password=Vyom@123
	// get requst
	// details-->http://10.41.5.156:8080/aeengine/rest/workflowinstances/2822
	// public String[] actualFilesArr;
	public String base_url = "http://localhost:8080/aeengine/rest";
	public String loginURL = base_url + "/authenticate";
	public String getWorkflowInstance = base_url + "/workflowinstances/45030";

	public void createUser() throws ParseException, Exception {
		SoftAssert sf = new SoftAssert();
		String sessionTokenForLogin = given().queryParam("username", "KalyaniTA").queryParam("password", "Pune@1234")
				.when().post(loginURL).then().extract().response().jsonPath().getString("sessionToken");

		System.out.println("Session token is: " + sessionTokenForLogin);

		String resp = given().header("X-Session-Token", sessionTokenForLogin).when().get(getWorkflowInstance).then()
				.extract().response().jsonPath().getString("workflowResponse");
		System.out.println(resp);

		JSONObject jsonobj = new JSONObject(resp);
		String actual_message = jsonobj.get("message").toString();
		System.out.println("Actual Message from API response: " + actual_message);
		JSONArray arrayl = (JSONArray) jsonobj.get("outputParameters");
		System.out.println(arrayl.length());
		JSONObject finaljson = new JSONObject();
		JSONArray finaljsonvalue = new JSONArray();
		Map<String, String> actualMap = new HashMap<String, String>();
		for (int i = 0; i < arrayl.length(); i++) {
			JSONObject singleObj = new JSONObject();
			System.out.println("Objects in array is: " + arrayl.get(i));
			JSONObject inner = (JSONObject) arrayl.get(i);
			String name = (String) inner.get("name");
			String value = inner.getString("value");
			System.out.println("name is :" + name);
			System.out.println("value is:" + value);

			singleObj.put("name", name);
			singleObj.put("value", value);
			finaljsonvalue.put(singleObj);
			// actualMap.put(name, value);
		}
		finaljson.put("outputParameters", finaljsonvalue);
		System.out.println("Final JSON is: " + finaljson.toString());

		try {
			// write JSON object to file
			FileWriter file = new FileWriter("C:\\Users\\kalyanig\\Downloads\\data.json");
			file.write(finaljson.toString());
			file.close();
			System.out.println("JSON file created successfully!");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}

		ObjectMapper mapper_expected = new ObjectMapper();
		ObjectMapper mapper_actual = new ObjectMapper();
		// JSON file to Java object
		POJO obj = mapper_expected.readValue(new File("C:\\Users\\kalyanig\\Downloads\\result.json"), POJO.class);
		POJO obj_actual = mapper_actual.readValue(new File("C:\\Users\\kalyanig\\Downloads\\data.json"), POJO.class);
		// number of tuples or objects in array in dialogElements

		int noOfOutputParams_actual = obj_actual.getOutputParameters().size();
		System.out.println("No of output params present in actual json file: " + noOfOutputParams_actual);

		int noOfOutputParams_expected = 0;
		if (obj.getOutputParameters() != null) {
			noOfOutputParams_expected = obj.getOutputParameters().size();
			String noOfOutputParams_expectedS = Integer.toString(noOfOutputParams_expected);
			System.out.println("No of output params present in expected json file: " + noOfOutputParams_expected);
			if (noOfOutputParams_expected > 0) {

				HashMap<String, String> expected_map = new HashMap<String, String>();
				HashMap<String, String> actual_map = new HashMap<String, String>();
				for (int i = 0; i < noOfOutputParams_expected; i++) {
					String element = obj.getOutputParameters().get(i).getName();
					String criteria = obj.getOutputParameters().get(i).getValue();
					System.out.println("Expected Parameter name is:-" + element);
					System.out.println("Expected Parameter Value is:-" + criteria);
					expected_map.put(element, criteria);
					String element_actual = obj_actual.getOutputParameters().get(i).getName();
					String criteria_actual = obj_actual.getOutputParameters().get(i).getValue();
					System.out.println("Actual Parameter name is:- " + element_actual);
					System.out.println("Actual Parameter Value is:- " + criteria_actual);
					// assertion part for output params
					actual_map.put(element_actual, criteria_actual);
				}
				for (Map.Entry<String, String> entry : expected_map.entrySet()) {
					System.out.println("Expected Key: " + entry.getKey() + ", Value: " + entry.getValue());
				}
				for (Map.Entry<String, String> entry : actual_map.entrySet()) {
					System.out.println("Actual Key: " + entry.getKey() + ", Value: " + entry.getValue());
				}

				// Match and compare the HashMaps
				for (String key : expected_map.keySet()) {
					if (actual_map.containsKey(key)) {
						String value1 = expected_map.get(key);
						String value2 = actual_map.get(key);

						if (value1.equals(value2)) {
							System.out.println("Values match for key " + key);
							sf.assertEquals(value2, value1);

						} else {
							System.out.println("Values do not match for key " + key);
							sf.assertEquals(value2, value1, "Values do not match for key " + key);
						}
					} else {
						System.out.println("Key " + key + " is not present in the second HashMap.");

						sf.assertTrue(false, "Key " + key + " is not present in the actual JSON result file");
					}

				}

				System.out.println("All validation successful for output parameters");
			}
		}
		if (obj.getOutputFiles() != null) {
			int noOfOutputFiles_expected = obj.getOutputFiles().size();
			String noOfOutputFiles_expectedS = Integer.toString(noOfOutputFiles_expected);
			System.out.println("No of output files present in expected json file: " + noOfOutputFiles_expectedS);
			if (noOfOutputFiles_expected > 0) {
				// This validation is for output files
				// int noOfOutputFiles_expected =obj.getOutputFiles().size();
				System.out.println("No of output files present in expected JSON is: " + noOfOutputFiles_expected);
				System.out.println("No of output files present in actual JSON is: "
						+ (noOfOutputParams_actual - noOfOutputParams_expected));
				String[] expectedFilesArr = new String[noOfOutputFiles_expected];
				String[] actualFilesArr = new String[noOfOutputParams_actual - noOfOutputParams_expected];
				for (int i = 0; i < noOfOutputFiles_expected; i++) {
					String fileName = obj.getOutputFiles().get(i).getFileName();
					System.out.println("Expected filename is:- " + fileName);
					expectedFilesArr[i] = fileName;
					System.out.println("Element at index " + i + ": " + expectedFilesArr[i]);

				}
				int j = 0;

				for (int i = noOfOutputParams_expected; i < noOfOutputParams_actual; i++) {
					String fileName = obj_actual.getOutputParameters().get(i).getName();
					System.out.println("Actual filename is:- " + fileName);

					actualFilesArr[j] = fileName;
					System.out.println("Element at index " + j + ": " + actualFilesArr[j]);
					j++;

				}
				System.out.println(Arrays.asList(actualFilesArr));
				Set<String> set_actualFileNames = new HashSet(Arrays.asList(actualFilesArr));
				System.out.println(Arrays.asList(expectedFilesArr));
				Set<String> set_expectedFileNames = new HashSet(Arrays.asList(expectedFilesArr));
				boolean result;
				// Check if the sizes are equal
				if (set_actualFileNames.size() != set_expectedFileNames.size()) {
					result = false;
				} else {
					// Check if both sets contain each other
					if (set_actualFileNames.containsAll(set_expectedFileNames)
							&& set_expectedFileNames.containsAll(set_actualFileNames)) {
						result = true;
					} else {
						result = false;
					}

				}
				sf.assertTrue(result, "Output files are not same in actual and expected json");
				System.out.println("Output files are same in actual and expected");
			}
		}
		sf.assertAll();

	}

	public static void main(String args[]) throws Exception {
		abc a = new abc();
		a.createUser();
	}

}
