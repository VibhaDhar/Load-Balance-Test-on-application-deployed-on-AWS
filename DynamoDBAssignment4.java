package db;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class DynamoDBAssignment4 {
	static AWSCredentials credentials = new BasicAWSCredentials(
			"", "4BjJGCU4VS6YXOUySgatchFG");

	static AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials);
	static DynamoDB dynamoDB = new DynamoDB(client);
	static {
		client.setRegion(Region.getRegion(Regions.US_WEST_2));
	}

	public DynamoDBAssignment4() {
		// createData();
	}

	public static List<Comments> scanOnAttributeValue(String tableName, String primaryKey,
			String attributeName[], String attributeValue[], String filename,
			String username, String comments) {

		ScanRequest request = new ScanRequest();
		request.setTableName(tableName);
		List<String> attributesToGet = new ArrayList<String>();
		attributesToGet.add(primaryKey);
		request.setAttributesToGet(attributesToGet);

		Map<String, Condition> scanFilter = new HashMap<String, Condition>();
		for (int i = 0; i < attributeName.length; i++) {
			Condition condition = new Condition();
			condition.setComparisonOperator("EQ");
			List<AttributeValue> attrValueList = new ArrayList<AttributeValue>();
			AttributeValue attrValue = new AttributeValue(attributeValue[i]);
			attrValueList.add(attrValue);
			condition.setAttributeValueList(attrValueList);
			scanFilter.put(attributeName[i], condition);
		}
		request.setScanFilter(scanFilter);
		ScanResult result = client.scan(request);
		List<Map<String, AttributeValue>> list = result.getItems();
		List<Comments> lpc = new ArrayList<Comments>();
		for (Map<String, AttributeValue> map : list) {
			Set<Entry<String, AttributeValue>> set = map.entrySet();
			Iterator<Entry<String, AttributeValue>> iter = set.iterator();

			while (iter.hasNext()) {
				Entry<String, AttributeValue> entry = iter.next();
				AttributeValue attrValue1 = entry.getValue();
				lpc.add(getResult(tableName, primaryKey, filename, username,
						comments, attrValue1.getS()));
			}
		}
		return lpc;
	}

	public static void createData() {
		CreateTableRequest createTableRequest = new CreateTableRequest()

				.withTableName("FILE_DETAILS")
				.withKeySchema(
						new KeySchemaElement().withAttributeName("FileName")
								.withKeyType(KeyType.HASH))

				.withAttributeDefinitions(
						new AttributeDefinition().withAttributeName("FileName")
								.withAttributeType("S"))
				.withProvisionedThroughput(
						new ProvisionedThroughput().withReadCapacityUnits(1L)
								.withWriteCapacityUnits(1L));
		Table result = null;
		try {
			result = dynamoDB.createTable(createTableRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}

		createTableRequest = new CreateTableRequest()

				.withTableName("COMMENTS")
				.withKeySchema(
						new KeySchemaElement().withAttributeName("Serial_Nos")
								.withKeyType(KeyType.HASH))

				.withAttributeDefinitions(
						new AttributeDefinition().withAttributeName(
								"Serial_Nos").withAttributeType("S"))
				.withProvisionedThroughput(
						new ProvisionedThroughput().withReadCapacityUnits(1L)
								.withWriteCapacityUnits(1L));
		result = null;
		try {
			result = dynamoDB.createTable(createTableRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertFile(String filename, String username, String url)
			throws Exception {

		TableWriteItems threadTableWriteItems = new TableWriteItems(
				"FILE_DETAILS");

		Item item = new Item().withPrimaryKey("FileName", filename)
				.withString("Username", username).withString("URL", url);
		threadTableWriteItems.addItemToPut(item);
		BatchWriteItemOutcome outcome = dynamoDB
				.batchWriteItem(threadTableWriteItems);

	}

	public static void insertComments(String filename, String username, String comments)
			throws Exception {

		TableWriteItems threadTableWriteItems = new TableWriteItems("COMMENTS");

		Item item = new Item()
				.withPrimaryKey("Serial_Nos", String.valueOf(Math.random()))
				.withString("Username", username)
				.withString("FileName", filename)
				.withString("Comments", comments);
		threadTableWriteItems.addItemToPut(item);
		BatchWriteItemOutcome outcome = dynamoDB
				.batchWriteItem(threadTableWriteItems);

	}

	public static Comments getResult(String tableName, String primaryKey,
			String filename, String username, String comments, String serialNos) {
		Table table = dynamoDB.getTable(tableName);

		ItemCollection<QueryOutcome> items = table.query(primaryKey, serialNos);

		Iterator<Item> iterator = items.iterator();
		Comments comm = new Comments();
		while (iterator.hasNext()) {
			Item currentItem = iterator.next();
			String filenames = currentItem.getString(filename);
			String usernames = currentItem.getString(username);
			String comment = currentItem.getString(comments);
			comm.setUsername(usernames);
			comm.setFileName(filenames);
			comm.setComments(comment);
		}

		return comm;
	}

	public List<FileDetails> getFileDetails(String tableName, String username,
			String filename, String URL) {

		ScanRequest sr = new ScanRequest().withTableName(tableName);

		ScanResult items = client.scan(sr);
		List<FileDetails> list = new ArrayList<FileDetails>();
		for (Map<String, AttributeValue> item : items.getItems()) {
			FileDetails fd = new FileDetails();
			fd.setUsername(item.get(username).getS());
			;
			fd.setFileName(item.get(filename).getS());
			;
			fd.setURL(item.get(URL).getS());
			list.add(fd);
		}
		return list;
	}
	
	public static void deleteFile(String fileName) {
		Table table = dynamoDB.getTable("FILE_DETAILS");
		DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
        .withPrimaryKey("FileName", fileName);
		 DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
		
	}
	
}
