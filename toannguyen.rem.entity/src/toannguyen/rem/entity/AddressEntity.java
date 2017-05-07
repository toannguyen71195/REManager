package toannguyen.rem.entity;

public class AddressEntity extends Entity {
	String city;
	String district;
	String ward;
	String street;
	String address;

	public AddressEntity(int id, String city, String district, String ward, String street, String address) {
		super(id);
		this.city = city;
		this.district = district;
		this.ward = ward;
		this.street = street;
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
