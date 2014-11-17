package src;

import java.util.List;
import java.util.ArrayList;

public class User {
	private enum Gender {MALE, FEMALE};
	private String name;
	private String surname;
	private int age;
	private String occupation;
	private Gender gender;
	private List<String> interests;

	public User(String name, String surname, int age, String occupation, boolean male) {
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.occupation = occupation;
		gender = male ? Gender.MALE : Gender.FEMALE;
		interests = new ArrayList<String>();
	}

	public void addInterest(String interest) {
		interests.add(interest);
	}
}