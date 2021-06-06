package com.ncnf.utilities;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.models.Organization;
import com.ncnf.utilities.map.MapUtilities;
import com.ncnf.utilities.settings.Settings;

import org.junit.Test;

import static com.ncnf.utilities.StringCodes.FRAGMENT_VIEW_TAG;
import static com.ncnf.utilities.StringCodes.generatePerViewID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class UtilitiesTest {

    @Test
    public void position_range_test(){
        LatLng epfl_position = new LatLng(46.5191f, 6.5668f);
        LatLng chuv_position = new LatLng(46.5249f, 6.6424f); //5.8km from EPFL
        LatLng vevey_position = new LatLng(46.4628f, 6.8419f); //22.05km from EPFL

        Settings.setCurrentMaxDistance(5);
        assertThat(MapUtilities.position_in_range(epfl_position, chuv_position), is(false));
        assertThat(MapUtilities.position_in_range(epfl_position, vevey_position), is(false));

        Settings.setCurrentMaxDistance(6);
        assertThat(MapUtilities.position_in_range(epfl_position, chuv_position), is(true));
        assertThat(MapUtilities.position_in_range(epfl_position, vevey_position), is(false));

        Settings.setCurrentMaxDistance(22);
        assertThat(MapUtilities.position_in_range(epfl_position, chuv_position), is(true));
        assertThat(MapUtilities.position_in_range(epfl_position, vevey_position), is(false));

        Settings.setCurrentMaxDistance(23);
        assertThat(MapUtilities.position_in_range(epfl_position, chuv_position), is(true));
        assertThat(MapUtilities.position_in_range(epfl_position, vevey_position), is(true));
    }

    @Test
    public void generatePerViewIdWorks(){
            Organization organization = new Organization("name", new GeoPoint(0, 0), "address", "email", "0000000000", "originalId");
            String orgString = organization.toString();
        assertEquals(generatePerViewID(organization), FRAGMENT_VIEW_TAG + orgString);
    }
}
