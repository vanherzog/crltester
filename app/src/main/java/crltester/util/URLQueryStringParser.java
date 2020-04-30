package crltester.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to parse URL query strings.
 */
public class URLQueryStringParser {
	private static final String ENCODING = "UTF-8";
	private static final String PARAM_SEP = "&";
	private static final String VALUE_SEP = "=";

	/**
	 * @param urlQueryString	URL query string.
	 * @return					Map of keys to string value lists.
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, List<String>> getUrlParameters(String urlQueryString)
	        throws UnsupportedEncodingException {
		if (urlQueryString == null) {
			return null;
		}
	    Map<String, List<String>> params = new HashMap<String, List<String>>();
        for (String param : urlQueryString.split(PARAM_SEP)) {
            String pair[] = param.split(VALUE_SEP, 2);
            String key = URLDecoder.decode(pair[0], ENCODING);
            String value = "";
            if (pair.length > 1) {
                value = URLDecoder.decode(pair[1], ENCODING);
            }
            List<String> values = params.get(key);
            if (values == null) {
                values = new ArrayList<String>();
                params.put(key, values);
            }
            values.add(value);
        }
	    return params;
	}
}
