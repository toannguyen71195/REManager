package toannguyen.rem.entity;

import java.sql.Timestamp;

public class EstateEntity extends Entity {

	public static String ESTATE_LIST = "estates";
	public static String IS_VISITED = "isVisited";
	public static String TIME = "time";
	public static int STATUS_AVAILABLE = 1;

	UserEntity owner;
	AddressEntity address;
	EstateDetailEntity detail;
	boolean available;
	String type;
	Timestamp postTime;
	Double price;
	Double area;

	public EstateEntity(int id, String name, UserEntity owner, AddressEntity address, boolean status, String type,
			Timestamp postTime, Double price, Double area) {
		super(id, name);
		this.owner = owner;
		this.address = address;
		this.available = status;
		this.type = type;
		this.postTime = postTime;
		this.price = price;
		this.area = area;
	}

	public UserEntity getOwner() {
		return owner;
	}

	public void setOwner(UserEntity owner) {
		this.owner = owner;
	}

	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	public EstateDetailEntity getDetail() {
		return detail;
	}

	public void setDetail(EstateDetailEntity detail) {
		this.detail = detail;
	}

	public boolean isStatus() {
		return available;
	}

	public void setStatus(boolean status) {
		this.available = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Timestamp getPostTime() {
		return postTime;
	}

	public void setPostTime(Timestamp postTime) {
		this.postTime = postTime;
	}

}