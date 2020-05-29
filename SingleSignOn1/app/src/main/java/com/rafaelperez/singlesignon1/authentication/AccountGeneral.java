package com.rafaelperez.singlesignon1.authentication;

public class AccountGeneral {
    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "com.ksquare.sso";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "sso account";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an sso account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "read write trust";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an sso account";

    //todo: Instantiate a retrofit service
    //public static final ServerAuthenticate sServerAuthenticate = new ParseComServerAuthenticate();
}
