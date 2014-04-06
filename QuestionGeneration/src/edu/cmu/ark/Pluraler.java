package edu.cmu.ark;

public class Pluraler {
	public static String makePlural (String singularWord){
        String pluralWord = "";
        String strippedWord = singularWord.substring(0, singularWord.length()-1);
        char lastLetter = singularWord.charAt(singularWord.length()-1);
        switch (lastLetter){
            case 's':
            case 'x':
            case 'z':
                pluralWord = singularWord + "es";
                break;
            case 'h': // checking for if the word ends with "ch" or "sh"
                if ((singularWord.charAt(singularWord.length()-2)== 'c') || (singularWord.charAt(singularWord.length()-2)== 's')) {
                    pluralWord = singularWord + "es";
                    break;
                } 
            case 'y':
                if (isEnglishConsonant(singularWord.charAt(singularWord.length()-2))) {
                    pluralWord = strippedWord + "ies";
                    break;
                }
            default: pluralWord = singularWord + "s";
            break;
        }
        return pluralWord;
    }
    private static boolean isEnglishConsonant(char ch) {
        switch (Character.toLowerCase(ch)) {
            case 'a': case 'e': case 'i': case 'o': case 'u': 
                return false;
            default: 
                return true;
        }
    }
}
