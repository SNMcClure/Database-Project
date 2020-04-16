// Login page for petsitting service
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.Console;

import util.Validation; // Shared constants and predicate functions
import profile.Profile;

public class Login {
	/* ASCII art generated thanks to wonderful text->ASCII art tool from
	 * http://patorjk.com/software/taag/#p=display&h=3&v=3&f=Varsity&t=petsitting_%0Aservices.com
	 * ASCII dachsund is original art from Hayley Jane Wakenshaw*/
	final static public String banner =
		"___________________________________________________________________________\n" +
		"               _         _  _    _   _                                     \n" +
		"              / |_      (_)/ |_ / |_(_)                                    \n" +
		" _ .--.  .---`| |-.--.  __`| |-`| |-__  _ .--.  .--./)                     \n" +
		"[ '/'`\\ / /__\\| |( (`\\][  || |  | |[  |[ `.-. |/ /'`\\;                     \n" +
		" | \\__/ | \\__.| |,`'.'. | || |, | |,| | | | | |\\ \\._/_______               \n" +
		" | ;.__/ '.__.\\__[\\__) [______/ \\__[___[___||__.',__|_______|              \n" +
		"[__|                      (_)                 ( ( __))                     \n" +
		" .--. .---. _ .--. _   __ __  .---. .---. .--.    .---.  .--.  _ .--..--.  \n" +
		"( (`\\/ /__\\[ `/'`\\[ \\ [  [  |/ /'`\\/ /__\\( (`\\]  / /'`\\/ .'`\\ [ `.-. .-. | \n" +
		" `'.'| \\__.,| |    \\ \\/ / | || \\__.| \\__.,`'.'. _| \\__.| \\__. || | | | | | \n" +
		"[\\__) '.__.[___]    \\__/ [___'.___.''.__.[\\__) (_'.___.''.__.'[___||__||__]\n" +
		"___________________________________________________________________________\n" +
		"\n\n"+
		"                          __\n" +
    "   ,                    ,\" e`--o\n" +
    "  ((                   (  | __,'\n" +
    "   \\\\~----------------' \\_;/    Art by Hayley Jane Wakenshaw\n" +
		"hjw (                     /\n" +
		"    /) ._______________.  )\n" +
		"   (( (               (( (\n" +
		"    ``-'               ``-'\n";
	final public static String GREETING =
		"Welcome to petsitting_services.com!\n\n",
		PROMPT = "Enter 'l' to login, 'c' to create account, or 'q' to quit.";

	public static String curUsername;

	public static Statement statement;

	public static boolean login(Scanner input, Statement statement) {
		String username, password;
		System.out.print("Enter username: ");
		username = input.nextLine();
		System.out.println();
		Console cons;
		// Attempt to read in the password with obscured input for added
		// added security
		if ((cons = System.console()) != null) {
			System.out.println("***NOTE: For security sake, the console will not"
												 + " display input characters (input will appear "
												 + "blank)***");
			System.out.print("Enter password: ");
			password = String.valueOf(cons.readPassword());
    }
    else {
			System.out.print("Enter password: ");
			password = input.nextLine();
			System.out.println();
		}
		String query
			= "SELECT * FROM accounts WHERE username = '" + username
			+ "' AND password = crypt('" + password + "', password);";
		//ResultSet rs = statement.executeQuery (query);
		int matchesSize = Validation.numMatches(query, statement);
		// Get size of row and ensure it equals one
		if (matchesSize == 1) {
			curUsername = username;
			System.out.println("Welcome " + curUsername + "!\n");
			return true;
		}
		else if (matchesSize == 0) {
			System.out.println("Account not found\n");
			return false;
		}
		else {
			System.err.println("Internal error: duplicate data found for"
												 + " account");
			System.exit(-1);
		}
		return false;
	}

	public static void createAccount(Scanner input, Statement statement) {
		String username, password, fullname, email, city, state, query;
		boolean isSitter, isOwner;
		int matchesSize = 0;
		char c;

		System.out.print("Username: ");
		username = Validation.getUsername(statement);

		System.out.println("Please enter a password that meets the" +
											 " following criteria:\n"+Validation.PASSWORD_REQS);
		password = Validation.getPassword();

		System.out.print("Enter your full name: ");
		fullname = Validation.getFullname();

		System.out.print("Email: ");
		email = Validation.getEmail(statement);

		System.out.print("Please enter your city name: ");
		city = Validation.getCity();

		System.out.print("Enter two character state code (for example "
										 + "enter Florida as FL): ");
		state = Validation.getState();

		System.out.print("Are you a pet sitter (y/n)? ");
		isSitter = Validation.getIsPetSitter();

		System.out.print("Are you a pet owner (y/n)? ");
		isOwner = Validation.getIsPetOwner();

		String insertCMD = "INSERT INTO accounts (username, fullname, "
			+ "password, email, tsjoined, offersdone, "
			+ "issitter, isowner, city, state)" +
			"VALUES('" + username + "', '" + fullname +
			"', crypt('" + password + "', 'md5'), '" +
			email + "', NOW()," + " 0, '" + isSitter +
			"', '" + isOwner + "', '" + city + "', '" +
			state + "');";

		try {
			statement.executeUpdate(insertCMD);
		}
		catch (java.sql.SQLException e) {
			System.err.println(e);
			System.exit(-1);
		}
		curUsername = username;
	}

	public static void main (String args[]) {
      try {
         Class.forName("org.postgresql.Driver");
      }
      catch (ClassNotFoundException e) {
         System.err.println(e);
         System.exit(-1);
      }
      try {
				Scanner input = new Scanner(System.in);
				// open connection to database
				Connection connection
					= DriverManager.getConnection(//"jdbc:postgresql://dbhost:port/dbname", "user", "dbpass");
																				"jdbc:postgresql://127.0.0.1:5432/final_project", "emma", "pass");

				statement
					= connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
																			 ResultSet.CONCUR_UPDATABLE);
				System.out.println(banner + GREETING);
				while(true) {
					System.out.println(PROMPT);
					char response = input.nextLine().charAt(0);
					if (response == 'l' && login(input, statement))
						break;

					else if (response == 'c') {
						createAccount(input, statement);
						break;
					}

					else if (response == 'q') {
						connection.close();
						System.exit(0);
					}
				}

				// Figure out which page/view to switch the user to now that
				// their credentials have been created or validated in the db.
				System.out.println(Validation.OPTIONS);
				char response = input.nextLine().toLowerCase().charAt(0);
				while(true) {
					if (response == 'p')
						response = Profile.goToProfile(curUsername, statement);
					else if (response == 's')
						;//response = goToSearch(curUsername, statement);
					else if (response == 'c')
						;//response = goToCreateOffer(curUsername, statement);
					else if (response == 'q') {
						connection.close();
						System.exit(0);
					}
					else {
						System.out.println(Validation.OPTIONS);
						response = input.nextLine().charAt(0);
					}
				}
      }
      catch (java.sql.SQLException e) {
				System.err.println(e);
				System.exit(-1);
      }
	}
}
