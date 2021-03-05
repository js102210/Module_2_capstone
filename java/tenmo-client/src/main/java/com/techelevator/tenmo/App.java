package com.techelevator.tenmo;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_VIEW_DETAILS_FOR_TRANSFER = "View a specific transfer";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_VIEW_DETAILS_FOR_TRANSFER, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private final TransferService transferService;
	private final AccountService accountService;

	private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private UserService userService;



	public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL),
				new TransferService(API_BASE_URL), new AccountService(API_BASE_URL), new UserService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, TransferService transferService, AccountService accountService, UserService userService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.transferService = transferService;
		this.accountService = accountService;
		this.userService = userService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_VIEW_DETAILS_FOR_TRANSFER.equals(choice)){
				viewTransferDetails();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}
	private void viewCurrentBalance() {
		BigDecimal balance = accountService.getTotalAcctBalance();
		console.printBalance(balance);
	}

	private void viewTransferHistory() {
		Transfer[] transferHistory = transferService.getAllTransfersForUser();
		console.printTransfers(transferHistory);
	}

	private void viewPendingRequests() {
	 Transfer[] pendingTransfers = transferService.showPendingTransfers();
	 console.printTransfers(pendingTransfers);

	}

	private void sendBucks() {
		console.printUsers(userService.getAllUsers());
		int selection = console.getUserInputInteger("Enter the ID of the user you would like to send Bucks to: ");
		BigDecimal amount = BigDecimal.valueOf(console.getUserInputInteger("Enter the number of Bucks to send them: "));
		boolean success = transferService.sendMoney((long) selection, amount);
		if (success){
			System.out.println("The transfer was made");
		} else {
			System.out.println("The transfer could not be made");
		}
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

	private void viewTransferDetails(){
		int selection = console.getUserInputInteger("Enter the ID of the transfer you would like to view: ");
		console.printDetailsForTransfer(transferService.getDetailsForTransfer(selection));
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
		    	//log current user and set their token as the active one for each service
				currentUser = authenticationService.login(credentials);
				accountService.AUTH_TOKEN = currentUser.getToken();
				transferService.AUTH_TOKEN = currentUser.getToken();
				userService.AUTH_TOKEN = currentUser.getToken();
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			} catch (RestClientResponseException e){
		    	System.out.println("You must be logged in or be more powerful to view this resource. Train harder or consult your local administrator. Good luck :)");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
