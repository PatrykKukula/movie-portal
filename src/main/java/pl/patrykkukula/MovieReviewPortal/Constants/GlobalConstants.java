package pl.patrykkukula.MovieReviewPortal.Constants;

public class GlobalConstants {
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\\-+=?.><]).{8,}$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,5}$";
    public static final String ONLY_LETTER_REGEX = "^[a-zA-Z]*$";public static final String USERNAME_REGEX = "^[a-zA-Z0-9]{3,15}$";
}
