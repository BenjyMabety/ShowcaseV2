package tmg.za.shared;

public class StringUtils {

	public static double getNValue(String text) {
		if (text == null || text.trim().isEmpty()) {
			return 0;
		}
		return Double.valueOf(text);
	}

}
