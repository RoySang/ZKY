package sict.zky.domain;

import java.sql.Date;

public class Pc_data {
	private Integer id;
	private Integer userId;
	private Integer systolicPressure;
	private Integer diastolicPressure;
	private Integer pulse;
	private String uploadTime;
	private String userName;
	private String screenName;
	private Integer familyMember;
	private Integer upload;

	public Pc_data() {
	}

	public Pc_data(Integer id, Integer userId, Integer systolicPressure,
			Integer diastolicPressure, Integer pulse, String uploadTime,
			String userName, String screenName, Integer familyMember) {

		this.id = id;
		this.userId = userId;
		this.systolicPressure = systolicPressure;
		this.diastolicPressure = diastolicPressure;
		this.pulse = pulse;
		this.uploadTime = uploadTime;
		this.userName = userName;
		this.screenName = screenName;
		this.familyMember = familyMember;
	}

	public Pc_data(Integer userId, Integer systolicPressure,
			Integer diastolicPressure, Integer pulse, String uploadTime,
			String userName, String screenName, Integer familyMember) {

		this.userId = userId;
		this.systolicPressure = systolicPressure;
		this.diastolicPressure = diastolicPressure;
		this.pulse = pulse;
		this.uploadTime = uploadTime;
		this.userName = userName;
		this.screenName = screenName;
		this.familyMember = familyMember;
	}

	public Pc_data(Integer userId, Integer systolicPressure,
			Integer diastolicPressure, Integer pulse, String uploadTime,
			String userName, String screenName, Integer familyMember,
			Integer upload) {

		this.userId = userId;
		this.systolicPressure = systolicPressure;
		this.diastolicPressure = diastolicPressure;
		this.pulse = pulse;
		this.uploadTime = uploadTime;
		this.userName = userName;
		this.screenName = screenName;
		this.familyMember = familyMember;
		this.upload = upload;
	}

	public Pc_data(Integer id, String uploadTime, int systolicPressure,
			int diastolicPressure, int pulse, int familyMember) {
		this.id = id;
		this.systolicPressure = systolicPressure;
		this.diastolicPressure = diastolicPressure;
		this.pulse = pulse;
		this.uploadTime = uploadTime;
		this.familyMember = familyMember;
	}

	public Pc_data(Integer id, String uploadTime, int systolicPressure,
			int diastolicPressure, int pulse) {
		this.id = id;
		this.systolicPressure = systolicPressure;
		this.diastolicPressure = diastolicPressure;
		this.pulse = pulse;
		this.uploadTime = uploadTime;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getSystolicPressure() {
		return systolicPressure;
	}

	public void setSystolicPressure(Integer systolicPressure) {
		this.systolicPressure = systolicPressure;
	}

	public Integer getDiastolicPressure() {
		return diastolicPressure;
	}

	public void setDiastolicPressure(Integer diastolicPressure) {
		this.diastolicPressure = diastolicPressure;
	}

	public Integer getPulse() {
		return pulse;
	}

	public void setPulse(Integer pulse) {
		this.pulse = pulse;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public Integer getFamilyMember() {
		return familyMember;
	}

	public void setFamilyMember(Integer familyMember) {
		this.familyMember = familyMember;
	}

	public Integer getUpload() {
		return upload;
	}

	public void setUpload(Integer upload) {
		this.upload = upload;
	}

}
