import com.liferay.portal.ModelListenerException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portal.model.Country;
import com.liferay.portal.model.Region;
import com.liferay.portal.service.CountryServiceUtil;
import com.liferay.portal.service.RegionServiceUtil;
import com.liferay.portlet.social.model.SocialActivity;

import java.util.List;

public class ActivityListener extends BaseModelListener<SocialActivity> {

    private List<Country> ALL_COUNTRIES = null;
    private Country USA = null;

    static {
    }

    @Override
    public void onAfterCreate(SocialActivity activity)
        throws ModelListenerException {

        if (ALL_COUNTRIES == null) {
            try {
                ALL_COUNTRIES = CountryServiceUtil.getCountries();
                USA = CountryServiceUtil.getCountryByName("United States");
            } catch (Exception e) {
                throw new ModelListenerException(e);
            }
        }

        try {
            String address;

            /* Get the actual user's address */
//            for (Address addr : UserLocalServiceUtil.getUser(activity.getUserId()).getAddresses()) {
//                if (addr.isPrimary()) {
//                    address = (addr.getRegion() != null ? (addr.getRegion() + ",") : "") +
//                        addr.getCountry();
//                }
//            }

            // generate a fake address instead
            address = generateRandomAddress();

            JSONObject json = JSONFactoryUtil.createJSONObject();
            json.put("address", address);
            ActivityWebSocketEndpoint.sendMsg(json.toString());

        } catch (Exception e) {
            throw new ModelListenerException(e);
        }
    }

    private String generateRandomAddress() throws Exception {

        Country country;

        if (Math.random() < .15) {
            country = USA;
        } else {
            country = ALL_COUNTRIES.get((int) (Math.random() * (double)
                ALL_COUNTRIES.size()));
        }
        Region region = null;
        List<Region> regions = RegionServiceUtil.getRegions(country
            .getCountryId());
        if (regions != null && !regions.isEmpty()) {
            region = regions.get((int) (Math.random() * (double) regions.size
                ()));
        }

        return
            (region != null ? region.getName() + "," : "") +
                country.getName();
    }
}
