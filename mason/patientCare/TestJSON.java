package patientCare;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

public class TestJSON {
    public static void main(String args[]){
       JSONObject jo = new JSONObject("{ \"abc\" : \"def\" }");
       System.out.println(jo);
       JSONExampleArray2();
       JSONExampleStringer();
    }
    
    private static JSONArray listNumberArray(int max){
    	JSONArray res = new JSONArray();
    	for (int i=0; i<max;i++) {
    		//The value of the labels must be an String in order to make it work
    		res.put(String.valueOf(i));
    	}
    	return res;
    }
    
    private static void JSONExampleArray2() {

    	//We can also create an Array without a String by creating an empty array and adding elements to it

    	JSONArray array = new JSONArray();

    	//Adding elements with .put()

    	array.put("value");
    	array.put(5);
    	array.put(-23.45e67);
    	array.put(true);

    	//We convert it to JSONObject providing a label arrray like last time

    	JSONArray list = listNumberArray(array.length());
    	JSONObject object = array.toJSONObject(list);
    	System.out.println("Final JSONOBject: " + object);
    }
    
    private static void JSONExampleStringer() {

    	//We initializate the JSONStringer

    	JSONStringer jsonStringer = new JSONStringer();

    	//Now we start the process of adding elements with .object()

    	jsonStringer.object();

    	//We can now add elements as keys and values with .values () and .key()

    	jsonStringer.key("trueValue").value(true);
    	jsonStringer.key("falseValue").value(false);
    	jsonStringer.key("nullValue").value(null);
    	jsonStringer.key("stringValue").value("hello world!");
    	jsonStringer.key("complexStringValue").value("h\be\tllo w\u1234orld!");
    	jsonStringer.key("intValue").value(42);
    	jsonStringer.key("doubleValue").value(-23.45e67);

    	//We end this prcedure with .ednObject

    	jsonStringer.endObject();

    	//Once we have a JSONStringer, we convert it to JSONObject generating a String and using JSONObject's contructor.

    	String str = jsonStringer.toString();
    	JSONObject jsonObject = new JSONObject(str);

    	System.out.println("Final JSONOBject: " + jsonObject);
    }
}

