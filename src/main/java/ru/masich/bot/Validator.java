package ru.masich.bot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private Pattern pattern;
    private Matcher matcher;

//    private static final String EMAIL_PATTERN =
//            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
//                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String EMAIL_PATTERN="^[a-zA-Z0-9]{1,}"+
            "((\\.|\\_|-{0,1})[a-zA-Z0-9]{1,})*"+
            "@"+
            "[a-zA-Z0-9]{1,}"+
            "((\\.|\\_|-{0,1})[a-zA-Z0-9]{1,})*"+
            "\\.[a-zA-Z]{2,}$";
    public Validator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public boolean emailValidate(final String hex) {
        matcher = pattern.matcher(hex);

        return matcher.matches();
    }
}
