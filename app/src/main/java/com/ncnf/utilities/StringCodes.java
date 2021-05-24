package com.ncnf.utilities;

import com.ncnf.models.Organization;

public class StringCodes {

    public static final int PICK_IMAGE = 100;

    public static String generatePerViewID(Organization o){
        return FRAGMENT_VIEW_TAG + o.toString();
    }
    public static final String FRAGMENT_BUNDLE_KEY_ORG_ID = "org_ID";
    public static final String FRAGMENT_ORGANIZATION_CREATION_TAG= "fragment_organization_creation";
    public static final String FRAGMENT_VIEW_TAG = "fragment_organization_view";
    public static final String FRAGMENT_ORGANIZATION_TAG = "fragment_organization_list";
    public static final String FRAGMENT_USER_PROFILE = "fragment_user_profile";


    /**
     * Global variables
     */
    public static final int PASSWORD_MINIMUM_LENGTH = 6;
    public static final String NEXT_ACTIVITY_EXTRA_KEY = "next_activity";
    public final static String POPUP_TITLE = "Connection Error";
    public final static String POPUP_POSITIVE_BUTTON = "Retry";

    /**
     * Keys related to users
     */
    public static final String USER_ORGANIZATIONS = "organizations";
    public static final String USERS_COLLECTION_KEY = "users/";
    public static final String USERNAME_KEY = "username";
    public static final String FULL_NAME_KEY = "first_name";
    public static final String BIRTH_DATE_KEY = "year_of_birth";
    public static final String EMAIL_KEY = "email";
    public static final String FRIENDS_KEY = "friends";
    public static final String SAVED_EVENTS_KEY = "saved_events";
    public static final String NOTIFICATIONS_KEY = "notifications";
    public static final String NOTIFICATIONS_TOKEN_KEY = "notifications_token";
    public static final String PENDING_REQUESTS_KEY = "pending_requests";
    public static final String AWAITING_REQUESTS_KEY = "awaiting_requests";
    public static final String USER_LOCATION_KEY = "user_location";
    public static final String USER_IMAGE_PATH = "/users";

    /**
     * Keys related to events
     */
    public static final String EVENTS_COLLECTION_KEY ="events/";
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
    public static final String NEWS_KEY = "news";
    public static final String GEOHASH_KEY = "geohash";
    public static final String LAT_KEY = "lat";
    public static final String LNG_KEY = "lng";

    /**
     * Keys related to groups
     */
    public static final String GROUPS_COLLECTION_KEY = "groups/";
    public static final String OWNED_GROUPS_KEY = "owned_groups";
    public static final String PARTICIPATING_GROUPS_KEY = "participating_groups";
    /**
     * Keys related to organizations
     */
    public static final String ORGANIZATIONS_COLLECTION_KEY = "organizations/";
    public static final String ORGANIZATION_NAME = "name";
    public static final String ORGANIZATION_EMAIL = "email";
    public static final String ORGANIZATION_ADMIN_TOKEN = "token";
    public static final String ORGANIZED_EVENTS_KEY = "organized_events";
    public static final String PHONE_NB_KEY = "phone_number";
    public static final String ADMIN_KEY = "admins";

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
    public static final String DEBUG_TAG = "DEBUGGING";
    public static final String EMPTY_STRING =  "";
    public static final String CONNECTED_KEYWORD = "Connected";
    public static final String DISCONNECTED_KEYWORD = "Disconnected";
}
