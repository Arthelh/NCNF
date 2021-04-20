package com.ncnf;

public class Utils {
    /**
     * Global variables
     */
    public static final int PASSWORD_MINIMUM_LENGTH = 6;

    /**
     * Keys related to users
     */
    public static final String USERS_COLLECTION_KEY = "users/";
    public static final String FIRST_NAME_KEY = "first_name";
    public static final String LAST_NAME_KEY = "last_name";
    public static final String BIRTH_YEAR_KEY = "year_of_birth";
    public static final String EMAIL_KEY = "email";
    public static final String FRIENDS_KEY = "friends";
    public static final String OWNED_EVENTS_KEY = "owned_events";
    public static final String SAVED_EVENTS_KEY = "saved_events";
    public static final String NOTIFICATIONS_KEY = "notifications";
    public static final String NOTIFICATIONS_TOKEN_KEY = "notifications_token";
    public static final String PENDING_REQUESTS_KEY = "pending_requests";
    public static final String AWAITING_REQUESTS_KEY = "awaiting_requests";

    /**
     * Keys related to events
     */
    public static final String EVENTs_COLLECTION_KEY ="events/";
    public static final String UUID_KEY = "uuid";
    public static final String NAME_KEY = "name";
    public static final String DATE_KEY  = "date";
    public static final String LOCATION_KEY ="location";
    public static final String TYPE_KEY = "type";
    public static final String VISIBILITY_KEY = "visibility";
    public static final String ATTENDEES_KEY = "attendees";
    public static final String DESCRIPTION_KEY = "description";
    public static final String OWNER_KEY = "owner";
    public static final String MIN_AGE_KEY = "min_age";
    public static final String PRICE_KEY = "price";
    public static final String INVITED_KEY = "invited";
    public static final String TAGS_LIST_KEY = "tags";
    public static final String ADDRESS_KEY = "address";
    public static final String EVENT_EMAIL_KEY = "email";
    public static final String NEWS_KEY = "news";

    /**
     * Keys related to tests
     */
    public static final String EMPTY_FIELD_STRING = "One of the fields is empty.";
    public static final String PASSWORDS_DO_NOT_MATCH_STRING = "Passwords do not match.";
    public static final String BADLY_FORMATTED_EMAIL_STRING = "The email address is badly formatted.";
    public static final String INVALID_PASSWORD_STRING = "The password should contain at least 6 characters.";

    /**
     * Keys related to Log
     */
    public static final String DEBUG_TAG = "debugging";
    public static final String EMPTY_STRING =  "";
    public static final String CONNECTED_KEYWORD = "Connected";
    public static final String DISCONNECTED_KEYWORD = "Disconnected";
}
