package com.techelevator.view;


import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

    public ConsoleService() {

    }

    public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}

	//prints each user with their id for selection
	public void printUsers(User[] users){
		System.out.println("-----------------------------");
		System.out.println("ACTIVE USERS (SELECT BY ID)");
		System.out.println("-----------------------------");
		for (User u : users){
			System.out.print(u.getId() + ": " + u.getUsername() + "\n");
		}
	}

	public void printBalance(BigDecimal balance){
		System.out.println("-----------------------------");
		System.out.println("Your total balance is: $ " + balance);
		System.out.println("-----------------------------");
	}

	public void printTransfers(Transfer[] transfers){

		System.out.println("----------------------------------------------------\n" +
				"Transfers\n" +
				"ID          From/To                 Amount         Status\n" +
				"----------------------------------------------------");
		for (Transfer t : transfers){
			System.out.println(t.getTransferId()+ "         " + "From: " + t.getFromUserName() + " To: " + t.getToUserName() + " $ " + t.getAmtOfTransfer() + "         " + t.getTransferStatusName());
		}
	}

	public void printDetailsForTransfer(Transfer t){
		System.out.println("--------------------------------------------\n" +
				"Transfer Details\n" +
				"--------------------------------------------");
		System.out.println("Id: " + t.getTransferId());
		System.out.println("From: " + t.getFromUserName());
		System.out.println("To: " + t.getToUserName());
		System.out.println("Type: " + t.getTransferTypeName());
		System.out.println("Status: " + t.getTransferStatusName());
		System.out.println("Amount: " + t.getAmtOfTransfer());

	}

}
