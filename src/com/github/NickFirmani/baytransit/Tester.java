public class Tester {
    public static void main(String[] unused) {
        System.out.println(hashAlpha(""));
        System.out.println(hashAlpha("a"));
        System.out.println(hashAlpha("b"));
        System.out.println(hashAlpha("ba"));
        System.out.println(hashAlpha("bac"));
        System.out.println(hashAlpha("bzzzzzzzz"));
        System.out.println(hashAlpha("c"));
        System.out.println(hashAlpha("daaaaa"));
        System.out.println(hashAlpha("e"));
        System.out.println(hashAlpha("gaaadxzzz"));
        System.out.println(hashAlpha("asdfasdf"));
        System.out.println(hashAlpha("asdfbsd"));
        System.out.println(hashAlpha("asdfgwesd"));
        System.out.println(hashAlpha("asdfefgsadf"));
    }
    public static int hashAlpha(String stringIn) {
		int returnInt = 0;
		stringIn = stringIn.toUpperCase();
		stringIn = stringIn.replaceAll("\\W" , "");
		if (stringIn.length() > 5) {
			stringIn = stringIn.substring(0, 6);
		} else {
            while (stringIn.length() <= 5) {
                stringIn = stringIn + "@";
            }
		}
		System.out.println("about to hash: " + stringIn); //fixme
        
        for (int k = 0; k < stringIn.length(); k += 1) {
			int charAt = (int) stringIn.charAt(k);
            charAt = charAt - 63;
            int offset;
            if (k == 0) {
                offset = charAt * 27 * 21;
            } else {
                offset = 0;
            }
            returnInt += offset + charAt * (k + 1);
		}
        return returnInt;
	}
}
