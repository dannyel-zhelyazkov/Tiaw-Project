package dny.apps.tiaw.validation;

public class ValidationConstants {

	//User Constants
	
    public final static String USERNAME_ALREADY_EXISTS = "Username %s already exists!";

    public final static String USERNAME_LENGTH = "Username must be between 3 and 18 characters long!";

    public final static String PASSWORDS_DO_NOT_MATCH = "Passwords don't match!";

    public final static String EMAIL_ALREADY_EXISTS = "Email %s already exists";

    public final static String WRONG_PASSWORD = "Wrong password!";

    public final static String NAME_LENGTH = "Name must contain at least 3 characters!";
    
    // Card Constants
    
    public final static String CARD_NAME_ALREADY_EXISTS = "Card %s already exists!";
    
    public final static String CARD_NAME_LENGTH = "Name must be between 3 and 20 characters long!";

	public static final String POWER_MUST_BE_POSITIVE = "Power must be positive number!";
	
	public static final String POWER_MUST_BE_NOT_NULL = "Power must be not null";
	
	public static final String DEFENSE_MUST_BE_POSITIVE = "Defense must be positive number!";
	
	public static final String DEFENSE_MUST_BE_NOT_NULL = "Defense must be not null";
	
	public static final String IMAGE_MUST_BE_NOT_NULL = "Image must be not null!";
	
	public static final String RARITY_MUST_BE_NOT_NULL = "Rarity must be positive number!";

	// Deck Constants
	
	 public final static String DECK_NAME_LENGTH = "Name must be between 3 and 10 characters long!";
}
