
public class Person {
	private String name;
	private String yomi;
	private String bday;
	private String place;
	private String blood;
	private String imgURL;

	public Person(String name, String yomi, String bday, String place,
			String blood, String imgURL) {
		super();
		this.name = name;
		this.yomi = yomi;
		this.bday = bday;
		this.place = place;
		this.blood = blood;
		this.imgURL = imgURL;
	}
	
	public String getName() {
		return name;
	}

	public String getYomi() {
		return yomi;
	}

	public String getBday() {
		return bday;
	}

	public String getPlace() {
		return place;
	}

	public String getBlood() {
		return blood;
	}

	public String getImgURL() {
		return imgURL;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", yomi=" + yomi + ", bday=" + bday
				+ ", place=" + place + ", blood=" + blood + ", imgURL="
				+ imgURL + "]";
	}
}
