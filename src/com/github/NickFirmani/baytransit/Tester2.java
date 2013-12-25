public class Tester2 {
    public static void main(String[] unused) {
        System.out.println(hashAlpha(""));
        System.out.println(hashAlpha("1"));
        System.out.println(hashAlpha("1R"));
        System.out.println(hashAlpha("21"));
        System.out.println(hashAlpha("51B"));
        System.out.println(hashAlpha("72"));
        System.out.println(hashAlpha("72M"));
        System.out.println(hashAlpha(""));
        System.out.println(hashAlpha("a"));
        System.out.println(hashAlpha("b"));
        System.out.println(hashAlpha("ba"));
        System.out.println(hashAlpha("bac"));
        System.out.println(hashAlpha("baz"));
        System.out.println(hashAlpha("bca"));
        System.out.println(hashAlpha("bza"));
        
        System.out.println(hashAlpha("bzzzzzzzz"));
        System.out.println(hashAlpha("c"));
        System.out.println(hashAlpha("daaaaa"));
        System.out.println(hashAlpha("e"));
        System.out.println(hashAlpha("gazzz"));
        System.out.println(hashAlpha("NX1"));
        System.out.println(hashAlpha("NXO"));
        System.out.println(hashAlpha("szzzzzzzz"));
        System.out.println(hashAlpha("tsdf"));
        System.out.println(hashAlpha("w"));
        System.out.println(hashAlpha("xzy"));
        System.out.println(hashAlpha("z"));
        System.out.println(hashAlpha("zzzzzzzzzzzzzz"));
    }
    public static int hashAlpha(String stringIn) {
		int returnInt = 0;
		stringIn = stringIn.toUpperCase();
		stringIn = stringIn.replaceAll("\\W" , "");
		if (stringIn.length() > 10) {
			stringIn = stringIn.substring(0, 11);
		} else {
            while (stringIn.length() <= 10) {
                stringIn = stringIn + "/";
            }
		}
        
		System.out.println("about to hash: " + stringIn); //fixme
        
        for (int k = 0; k < stringIn.length(); k += 1) {
			int charAt = (int) stringIn.charAt(k);
            charAt = charAt - 47;
            int offset;
            if (k == 0) {
                offset = charAt * 46 * 66;
            } else {
                offset = (int) Math.pow(4, k+1);
            }
            returnInt += offset + charAt * (k + 1);
		}
        return returnInt;
	}
}
