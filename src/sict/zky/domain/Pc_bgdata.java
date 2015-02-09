package sict.zky.domain;

public class Pc_bgdata {
	private Integer id;
	private Integer userId;
	private Double bloodGlucose;
	
	private String uploadTime;
	private String userName;
	private String screenName;
	private Integer familyMember;
	private Integer upload;
	private Integer type;
	
	
	public Pc_bgdata(){}
	
	public Pc_bgdata(Integer id, Integer userId, Double bloodGlucose,
			String uploadTime, String userName, String screenName,
			Integer familyMember,Integer type) {
		this.id = id;
		this.userId = userId;
		this.bloodGlucose = bloodGlucose;
		
		this.uploadTime = uploadTime;
		this.userName = userName;
		this.screenName = screenName;
		this.type=type;
	}
	
//	public Pc_bgdata(Integer userId, Double bloodGlucose, 
//			String uploadTime, String userName, String screenName,
//			Integer familyMember,Integer type) {
//		
//		this.userId = userId;
//		this.bloodGlucose = bloodGlucose;
//		
//		this.uploadTime = uploadTime;
//		this.userName = userName;
//		this.screenName = screenName;
//		this.familyMember = familyMember;
//		this.type=type;
//	}

	public Pc_bgdata(Integer userId, Double bloodGlucose, 
			String uploadTime, String userName, String screenName,
			Integer familyMember,Integer upload,Integer type) {
		
		this.userId = userId;
		this.bloodGlucose = bloodGlucose;
		
		this.uploadTime = uploadTime;
		this.userName = userName;
		this.screenName = screenName;
		this.familyMember = familyMember;
		this.upload=upload;
		this.type=type;
	}

	

	public Pc_bgdata(Integer userId, String uploadTime, Double bloodGlucose,
			int familyMember) {
		this.userId = userId;
		this.bloodGlucose = bloodGlucose;
		
		this.uploadTime = uploadTime;
		this.familyMember = familyMember;
	}

	

	public Pc_bgdata(Integer id, Double bloodGlucose, String uploadTime) {
		super();
		this.id = id;
		this.bloodGlucose = bloodGlucose;
		this.uploadTime = uploadTime;
	}

	public Pc_bgdata(Integer id, Double bloodGlucose, String uploadTime,int type) {
		super();
		this.id = id;
		this.bloodGlucose = bloodGlucose;
		this.uploadTime = uploadTime;
		this.type=type;
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
	public Double getBloodGlucose() {
		return bloodGlucose;
	}
	public void setBloodGlucose(Double bloodGlucose) {
		this.bloodGlucose = bloodGlucose;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	
	
}
