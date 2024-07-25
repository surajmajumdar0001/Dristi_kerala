package com.pucar.drishti.config;


import org.springframework.stereotype.Component;


@Component
public class ServiceConstants {

    public static final String EXTERNAL_SERVICE_EXCEPTION = "External Service threw an Exception: ";
    public static final String SEARCHER_SERVICE_EXCEPTION = "Exception while fetching from searcher: ";
    public static final String USERNAME = "esigninterceptor";
    public static final String PASSWORD = "Beehyv@123";
    public static final String TENANTID = "kl";
    public static final String USERTYPE = "EMPLOYEE";
    public static final String SCOPE = "read";
    public static final String GRANTTYPE = "password";
    public static final String AUTHORIZATION = "Basic ZWdvdi11c2VyLWNsaWVudDo=";
    public static final String INVALID_TRANSACTION = "this transaction is not initiated by drishti";
    public static final String INVALID_TRANSACTION_EXCEPTION = "invalid transaction id for e-sign";
}
