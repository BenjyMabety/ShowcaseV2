package tmg.za.shared;

public class StringUtils {

	public static double getNValue(String text) {
		if (text.isBlank()) {
			return 0;
		}
		return Double.valueOf(text);
	}

}
