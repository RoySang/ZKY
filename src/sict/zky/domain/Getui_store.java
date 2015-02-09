package sict.zky.domain;

import android.R.integer;

public class Getui_store {
	private Integer id;
	private Integer userId;
	private String userName;
	private String content;
	private String time;
	private Integer type;
	private Integer mark;
	private String title;

	public Getui_store() {
		super();
	}

	public Getui_store(Integer userId, String content, String time, Integer mark,String title) {
		super();
		this.userId = userId;
		this.content = content;
		this.time = time;
		this.mark = mark;
		this.title = title;
	}
	

	public Getui_store(String content, String time, Integer mark,String title) {
		super();
		this.content = content;
		this.time = time;
		this.mark = mark;
		this.title = title;
	}

	public Getui_store(Integer userId, String userName, String content,
			String time, Integer mark,String title) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.content = content;
		this.time = time;
		this.mark = mark;
		this.title = title;
	}

	public Getui_store(Integer id, Integer userId, String userName,
			String content, String time, Integer type, Integer mark,String title) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.content = content;
		this.time = time;
		this.type = type;
		this.mark = mark;
		this.title = title;
	}

	public Integer getId() {
		return id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

}
