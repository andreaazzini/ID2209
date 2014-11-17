package src;

public class Artifact {
	private String name;
	private String creator;
	private String date;
	private String place;
	private String genre;

	public Artifact(String name, String creator, String date,
					String place, String genre) {
		this.name = name;
		this.creator = creator;
		this.date = date;
		this.place = place;
		this.genre = genre;
	}

	public String getName() {
		return name;
	}

	public String getCreator() {
		return creator;
	}

	public String getDate() {
		return date;
	}

	public String getPlace() {
		return place;
	}

	public String getGenre() {
		return genre;
	}
}