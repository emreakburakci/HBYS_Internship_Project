package com.example.application.util;

import java.util.ResourceBundle;

public class ResourceBundleUtil {

    private  ResourceBundle rb ;

    public ResourceBundleUtil(String language){

        rb = ResourceBundle.getBundle(language);
    }



    public String getString(String key){

        
        return rb.getString(key);
    }


    
}
