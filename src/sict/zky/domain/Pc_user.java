package sict.zky.domain;

public class Pc_user {
	private Integer id;
	private String userName;
	private int userId;
	private int familyMember;
	private String familyRole;
	private int upload;

	public Pc_user(Integer id, Integer userId, String userName,
			int familyMember, String familyRole) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;

		this.familyMember = familyMember;
		this.familyRole = familyRole;
	}

	public Pc_user(Integer userId, String userName, int familyMember,
			String familyRole, int upload) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.familyMember = familyMember;
		this.familyRole = familyRole;
		this.upload = upload;
	}

	public Pc_user(int userId, String userName, int familyMember,
			String familyRole) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.familyMember = familyMember;
		this.familyRole = familyRole;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserID() {
		return userId;
	}

	public void setUserID(Integer userID) {
		this.userId = userID;
	}

	public int getFamilyMember() {
		return familyMember;
	}

	public void setFamilyMember(int familyMember) {
		this.familyMember = familyMember;
	}

	public String getFamilyRole() {
		return familyRole;
	}

	public void setFamilyRole(String familyRole) {
		this.familyRole = familyRole;
	}

	public int getUpload() {
		return upload;
	}

	public void setUpload(int upload) {
		this.upload = upload;
	}

}
