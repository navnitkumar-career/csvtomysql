package com.batch.model;

public class User {

	private Integer id;
	private String status;
	private String description;

	public User(Integer id, String status, String description) {
		super();
		this.id = id;

		this.status = status;
		this.description = description;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", status=" + status + ", description=" + description + "]";
	}

}
