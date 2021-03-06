package controllers.businessLogic.hirewand;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import api.http.httpResponse.hirewand.HireWandResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import play.Logger;

/**
 * Created by User on 07-12-2016.
 */
public class HireWandService {

    static HireWandService _this = null;
    static String emailaddress = null, userpassword = null, authkey = null, callbackurl = null;
    // queue that is used to store all responses that need to be re-processed
    static private List<HireWandResponse> responseQueue = new ArrayList<>();

    public JSONParser parser = new JSONParser(); // parses hirewand api response
    private HttpClient httpclient = new HttpClient();
    private PostMethod httppost = null;

    private String UPLOADURL = "http://www.hirewand.com/api/upload/",
            LOGINURL = "http://www.hirewand.com/api/signin/",
            PROFILESURL = "http://www.hirewand.com/api/getprofiles/",
            PROFILEURL = "http://www.hirewand.com/api/getprofile/";

    static public HireWandService get(){
        if(_this==null){
            _this = new HireWandService();
        }
        return _this;
    }

    public void login(String email, String password) throws HWHTTPException {
        PostMethod httppost = new PostMethod(LOGINURL);
        try{
            NameValuePair[] data = {new NameValuePair("email", email),new NameValuePair("password", password)}; // login credentials
            httppost.setRequestBody(data);
            httpclient.executeMethod(httppost);
            int status = httppost.getStatusCode();
            InputStream responseStream = httppost.getResponseBodyAsStream();
            String responseString = readResponseStream(responseStream);
            JSONObject resObj = convertToJSON(responseString);
            if(status==200){
                emailaddress = email;
                userpassword = password;
                authkey = (String) resObj.get("authkey"); // authkey for the session, this key remains valid for 8 hrs of inactivity
                System.out.println("Login successful");
            }
            else{
                System.out.println("Login failed");
                throw new HWHTTPException((String) resObj.get("msg"),401);
            }
        }
        catch(java.net.ConnectException e){
            throw new HWHTTPException("Failed to initialize. Connection refused",503);
        }
        catch(java.net.UnknownHostException e){
            throw new HWHTTPException("Failed to initialize. No internet connection",0);
        }
        catch(IOException e){
            throw new HWHTTPException("Invalid response from server",500);
        }
        catch (ParseException e) {
            throw new HWHTTPException("Invalid response from server",500);
        }
    }

    @SuppressWarnings("unchecked")
    public String call(String function, HashMap params) throws InvalidRequestException, HWHTTPException{
        JSONObject jsonobj = new JSONObject();
        List<NameValuePair> paramlist = new LinkedList<NameValuePair>();
        InputStreamRequestEntity requestEntityStream = null;

        try{
            if(authkey==null){
                throw new HWHTTPException("User not logged in",401);
            }
            else{
                String printtype;
                switch(function){
                    case "upload" :
                        if(params.get("filename")==null) throw new InvalidRequestException("Parameter missing: filename", 701);
                        else if(params.get("resume")==null) throw new InvalidRequestException("Parameter missing: resume", 701);
                        else if(params.get("resume") instanceof InputStream == false) throw new InvalidRequestException("Invalid parameter, `resume` must be a inputstream", 702);
                        else{
                            paramlist.add(new NameValuePair("filename", (String) params.get("filename")));
                            if(params.get("callback")!=null) {
                                Logger.info("Callback URL in supplied params = "+(String) params.get("callback"));
                                paramlist.add(new NameValuePair("callback", (String) params.get("callback")));
                            }
                            else if(callbackurl!=null) {
                                //Logger.info("Preset Callback URL = "+callbackurl);
                                paramlist.add(new NameValuePair("callback", callbackurl));
                            }
                            printtype = (String)params.get("prettytype");
                            if(printtype==null) printtype = "simple";
                            paramlist.add(new NameValuePair("prettytype",printtype));
                            requestEntityStream = (new InputStreamRequestEntity((InputStream) params.get("resume")));
                            httppost = new PostMethod(UPLOADURL);
                        }
                        break;

                    case "profiles":
                        paramlist.add(new NameValuePair("from", "0"));
                        paramlist.add(new NameValuePair("resSize",params.get("size").toString()));
                        paramlist.add(new NameValuePair("since",params.get("since").toString()));
                        printtype = (String)params.get("prettytype");
                        if(printtype==null) printtype = "simple";
                        paramlist.add(new NameValuePair("prettytype",printtype));
                        httppost = new PostMethod(PROFILESURL);
                        break;

                    case "profile":
                        if(params.get("personid")==null) throw new InvalidRequestException("Parameter missing: personid", 701);
                        paramlist.add(new NameValuePair("personid", (String) params.get("personid")));
                        printtype = (String)params.get("prettytype");
                        if(printtype==null) printtype = "simple";
                        paramlist.add(new NameValuePair("prettytype",printtype));
                        httppost = new PostMethod(PROFILEURL);
                        break;
                }

                if(requestEntityStream!=null) httppost.setRequestEntity((RequestEntity) requestEntityStream);
                paramlist.add(new NameValuePair("sessionless", "true"));
                paramlist.add(new NameValuePair("authkey", authkey));
                int i = 0;
                NameValuePair[] queryStringParameters = new NameValuePair[paramlist.size()];
                for(NameValuePair entry : paramlist){
                    queryStringParameters[i++] = entry;
                }
                httppost.setQueryString(queryStringParameters);
                Logger.info("httppost.getQueryString()="+httppost.getQueryString());
                httpclient.executeMethod(httppost);
                int status = httppost.getStatusCode();
                if(status==401){
                    login(emailaddress,userpassword); // re-logging, authkey expired
                    httpclient.executeMethod(httppost);
                }
                return httppost.getResponseBodyAsString();
            }
        }
        catch(java.net.UnknownHostException e){
            throw new HWHTTPException("Failed to initialize. No internet connection",0);
        }
        catch (HttpException e) {
            throw new HWHTTPException("Failed to initialize. Please try later",503);
        }
        catch (IOException e) {
            throw new HWHTTPException("Invalid response from server",500);
        }
        finally{
            if(httppost!=null) httppost.releaseConnection();
        }
    }

    List call_list(String function, HashMap params) throws InvalidRequestException, HWHTTPException{
        ArrayList responselist = new ArrayList();
        JSONObject jsonobj = new JSONObject();
        List<NameValuePair> paramlist = new LinkedList<NameValuePair>();
        InputStreamRequestEntity requestEntityStream = null;
        try{
            switch(function){
                case "profiles":
                    //get latest profiles after a particular time
					/*
					 * parameters required
					 * 
					 * 1. since - time in milliseconds after which profile has to be retrieved
					 * 2. size - 100 or lesser 
					 * 
					 * returns List of profiles
					 * 
					 * use `UpdateDateMS` from the last profile received for the next call
					 * 
					 * */
                    ArrayList profiles = new ArrayList();
                    if(params.get("since") == null) throw new InvalidRequestException("Parameter missing: since", 701);
                    if(params.get("size") == null) throw new InvalidRequestException("Parameter missing: size", 701);
                    if(params.get("size") instanceof Integer == false) throw new InvalidRequestException("Invalid parameter, `size` must be a number", 702);
                    if(params.get("since") instanceof Long == false) throw new InvalidRequestException("Invalid parameter, `since` must be of type Long", 702);
                    if((Integer) params.get("size")>100 ||(Integer) params.get("size")<1 ) throw new InvalidRequestException("Invalid request size, please specify a range between 1 to 100", 702);
                    else{
                        String responseStr = call("profiles",params);
                        //parse response from hirewand
                        JSONObject profilejson = convertToJSON(responseStr);
                        //pull profiles from the response
                        JSONArray profilearray = (JSONArray) profilejson.get("result");

                        for (Object singleProfile : profilearray) {
                            profiles.add((JSONObject)singleProfile);
                        }
                    }
                    return profiles;
            }
        } catch (ParseException e) {
            throw new HWHTTPException("Invalid response from server",500);
        }
        finally{
            if(httppost!=null) httppost.releaseConnection();
        }

        return responselist;
    }

    boolean isNumber(String str){
        boolean flag = true;
        for(Character c : str.toCharArray()){
            if(Character.isDigit(c)==false){
                flag = false;
                break;
            }
        }
        return flag;
    }

    public void setCallback(String callbackurl){
        this.callbackurl = callbackurl;
    }

    public String readResponseStream(InputStream responsestream) throws IOException{
        StringBuffer response = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(responsestream,"UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null){
            response.append(line).append(System.getProperty("line.separator"));
        }
        reader.close();
        return response.toString();
    }

    public JSONObject convertToJSON(String jsonstr) throws ParseException{
        return (JSONObject) parser.parse(jsonstr.trim());
    }

    public void printCallback(JSONObject requestJson) throws ParseException{
        for (Object key : requestJson.keySet()) {
            String keyStr = (String)key;
            Object keyvalue = requestJson.get(keyStr);

            //Print key and value
            System.out.println("key: "+ keyStr + " value: " + keyvalue);

            //for nested objects iteration if required
            if (keyvalue instanceof JSONObject)
                printCallback((JSONObject)keyvalue);
        }
		
		
		/* SAMPLE JSON REQUEST ON CALLBACK
		 * {
		 *  	profile: structured form of the resume uploaded, 
		 *  	duplicate: true/false, // True, if the same resume was already present in the system 
		 *  	type: "newprofile",
		 *  	personid: personid, // unique hirewand id for resume (also returned in the response of successfull upload)
		 *  	accountid: accountid, // account id of the user
		 *  	message: "Profile created successfully"
		 * }		 
		 */

    }

    /*
     * print particular fields in profile
     * 
     * arguments 
     * 1. profilejson - Jsonarray of profile 
     * 2. requiredfields - list of attributes to be printed 
     * 
     * */
    public void printProfile(List profileobject,List requiredfields){
        for (Object singleProfile : profileobject) {
            JSONObject prof = (JSONObject)singleProfile;
            for(Object singlefield : requiredfields){
                singlefield = (String)singlefield;
                //print the values
                System.out.println(singlefield+" "+prof.get(singlefield));
            }
        }
    }

    public static Boolean pushResponseQueue(String personId, HireWandResponse.Profile profile, Boolean duplicate){
        if(personId!= null && profile!= null){

            //if an entry already exists, update it
            Boolean exists = Boolean.FALSE;
            for(HireWandResponse each:responseQueue){
                if(Objects.equals(each.getPersonid(), personId)){
                    each.setProfile(profile);
                    each.setDuplicate(duplicate);
                    exists = Boolean.TRUE;
                    break;
                }
            }

            if(!exists){
                // else create a new entry
                HireWandResponse hireWandResponse = new HireWandResponse();
                hireWandResponse.setPersonid(personId);
                hireWandResponse.setProfile(profile);
                hireWandResponse.setDuplicate(duplicate);
                responseQueue.add(hireWandResponse);
            }

            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static HireWandResponse popResponseQueue(String personId){
        ListIterator<HireWandResponse> iter = responseQueue.listIterator();
        HireWandResponse temp = new HireWandResponse();
        while(iter.hasNext()){
            temp = iter.next();
            if(Objects.equals(temp.getPersonid(), personId)){
                iter.remove();
                return temp;
            }
        }
        return null;
    }

}
