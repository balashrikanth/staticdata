package com.stonex.corp.payments.staticdata.config;

public class SystemFieldConfig {


    //Class Info
    public static final String BASECLASS="com.stonex.corp.payments.staticdata";

    //Mongo collection extension for unapproved
    public static final String UNAPPROVEDCOLLECTION="_unapproved";

    //Status Info
    public static final String ACTIONNEW="NEW";
    public static final String ACTIONEDIT="EDIT";
    public static final String ACTIONDELETE="DELETE";
    public static final String ACTIONAPPROVE="APPROVE";

    //Counters
    public static final int PICKLISTCOLS=10;//Set to Maximum value

    //System User
    public static final String SYSTEMUSER="SYSTEM";

    //Entity Prefix

    public static final String ENTITYPREFIX="static-";

    //Default Error
    public static final String DEFAULTERRORCODE="ENC9999";
    public static final String UNEXPECTEDERROR="ENC9998";
    public static final String MESSAGECODEWORD="%%MESSAGE%%";


}
