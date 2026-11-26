package tmg.za.shared;

/**
 * String Utils class
 */
public class StringUtils {

	/**
	 * @param text
	 * @return double
	 */
	public static double getNValue(String text) {
		if (text == null || text.trim().isEmpty()) {
			return 0;
		}
		return Double.valueOf(text);
	}

}
