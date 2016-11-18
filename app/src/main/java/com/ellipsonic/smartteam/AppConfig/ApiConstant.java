package com.ellipsonic.smartteam.AppConfig;

/**
 * Created by USER on 10/15/2016.
 */
public class ApiConstant {
    public static String SERVERADDRESS = "http://104.236.73.97";
    public static String USEREXISTANCE = SERVERADDRESS+"/api/v1/persons/";
    public static String ALLUSER = SERVERADDRESS+"/api/v1/persons";
    public static String SAVEUSERPROFILE = ALLUSER+"/"+StaticConstants.user_id;
}
