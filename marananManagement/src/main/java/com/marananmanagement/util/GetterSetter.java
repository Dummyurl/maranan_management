package com.marananmanagement.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GetterSetter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String author;
	String auth_title;
	String vid_Id;
	String title;
	String descriptions;
	String channelTitle;
	String thumbnailHigh;
	String maxresThembUrl;
	String playListId;
	String total_videos;
	String time;
	String email;
	String signinwith;
	String id;
	String publish;
	String f_status;
	String l_status;
	String m_status;
	String f_sex_status;
	String m_sex_status;
	String alertMessages;
	int pagePosition;
	int count;

	String message;
	String phone;
	String alert_audio;
	String mode;
	String state;
	String time_hr;
	String time_min;
	String sun;
	String mon;
	String tues;
	String wed;
	String thur;
	String fri;
	String sat;
	String time_hr_two;
	String time_min_two;

	String date;
	String radio_programs;
	String status;
	String publish_status;
	String publish_notification;
	String image;
	String duration;
	public int imageResource;

	String city_suscriber;
	String family_suscriber;
	String name_suscriber;
	String mobile_one_suscriber;
	String mobile_two_suscriber;
	String mobile_three_suscriber;
	String mobile_four_suscriber;
	String line_one_suscriber;
	String line_two_suscriber;
	String country_code_suscriber;
	String pdf_pages;
	String pdf;
	String image_thumb;
	String nextPageToken;
	String contentDetails;
	String tag;
	String unique_video_id;
	String priority;
	
	String firstname;
	String lastname;
	public int imgSeq;
	
	String image_old;
	String image_status;
	
	public int imgAddRes;
	String days_select;

	public ArrayList<String> getListImage() {
		return listImage;
	}

	public void setListImage(ArrayList<String> listImage) {
		this.listImage = listImage;
	}

	ArrayList<String> listImage;

	public String getDays_select() {
		return days_select;
	}

	public void setDays_select(String days_select) {
		this.days_select = days_select;
	}

	public int getImgAddRes() {
		return imgAddRes;
	}

	public void setImgAddRes(int imgAddRes) {
		this.imgAddRes = imgAddRes;
	}

	public String getImage_status() {
		return image_status;
	}

	public void setImage_status(String image_status) {
		this.image_status = image_status;
	}

	public String getImage_old() {
		return image_old;
	}

	public void setImage_old(String image_old) {
		this.image_old = image_old;
	}

	public int getImgSeq() {
		return imgSeq;
	}

	public void setImgSeq(int imgSeq) {
		this.imgSeq = imgSeq;
	}

	public String getLastname() {
		return lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getTime_hr_two() {
		return time_hr_two;
	}

	public void setTime_hr_two(String time_hr_two) {
		this.time_hr_two = time_hr_two;
	}

	public String getTime_min_two() {
		return time_min_two;
	}

	public void setTime_min_two(String time_min_two) {
		this.time_min_two = time_min_two;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getUnique_video_id() {
		return unique_video_id;
	}

	public void setUnique_video_id(String unique_video_id) {
		this.unique_video_id = unique_video_id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	int color;
	int text_color;

	public int getText_color() {
		return text_color;
	}

	public void setText_color(int text_color) {
		this.text_color = text_color;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getContentDetails() {
		return contentDetails;
	}

	public void setContentDetails(String contentDetails) {
		this.contentDetails = contentDetails;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	public String getImage_thumb() {
		return image_thumb;
	}

	public void setImage_thumb(String image_thumb) {
		this.image_thumb = image_thumb;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getPdf_pages() {
		return pdf_pages;
	}

	public void setPdf_pages(String pdf_pages) {
		this.pdf_pages = pdf_pages;
	}

	public int imgCancelRes;
	public int imgCheckUncheckRes;

	public int getImgCancelRes() {
		return imgCancelRes;
	}

	public void setImgCancelRes(int imgCancelRes) {
		this.imgCancelRes = imgCancelRes;
	}

	public int getImgCheckUncheckRes() {
		return imgCheckUncheckRes;
	}

	public void setImgCheckUncheckRes(int imgCheckUncheckRes) {
		this.imgCheckUncheckRes = imgCheckUncheckRes;
	}

	public int getImgBroadCastRes() {
		return imgBroadCastRes;
	}

	public void setImgBroadCastRes(int imgBroadCastRes) {
		this.imgBroadCastRes = imgBroadCastRes;
	}

	public int imgBroadCastRes;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAlert_audio() {
		return alert_audio;
	}

	public void setAlert_audio(String alert_audio) {
		this.alert_audio = alert_audio;
	}

	public String getPublish_notification() {
		return publish_notification;
	}

	public void setPublish_notification(String publish_notification) {
		this.publish_notification = publish_notification;
	}

	public String getPublish_status() {
		return publish_status;
	}

	public void setPublish_status(String publish_status) {
		this.publish_status = publish_status;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry_code_suscriber() {
		return country_code_suscriber;
	}

	public void setCountry_code_suscriber(String country_code_suscriber) {
		this.country_code_suscriber = country_code_suscriber;
	}

	public String getCity_suscriber() {
		return city_suscriber;
	}

	public void setCity_suscriber(String city_suscriber) {
		this.city_suscriber = city_suscriber;
	}

	public String getFamily_suscriber() {
		return family_suscriber;
	}

	public void setFamily_suscriber(String family_suscriber) {
		this.family_suscriber = family_suscriber;
	}

	public String getName_suscriber() {
		return name_suscriber;
	}

	public void setName_suscriber(String name_suscriber) {
		this.name_suscriber = name_suscriber;
	}

	public String getMobile_one_suscriber() {
		return mobile_one_suscriber;
	}

	public void setMobile_one_suscriber(String mobile_one_suscriber) {
		this.mobile_one_suscriber = mobile_one_suscriber;
	}

	public String getMobile_two_suscriber() {
		return mobile_two_suscriber;
	}

	public void setMobile_two_suscriber(String mobile_two_suscriber) {
		this.mobile_two_suscriber = mobile_two_suscriber;
	}

	public String getMobile_three_suscriber() {
		return mobile_three_suscriber;
	}

	public void setMobile_three_suscriber(String mobile_three_suscriber) {
		this.mobile_three_suscriber = mobile_three_suscriber;
	}

	public String getMobile_four_suscriber() {
		return mobile_four_suscriber;
	}

	public void setMobile_four_suscriber(String mobile_four_suscriber) {
		this.mobile_four_suscriber = mobile_four_suscriber;
	}

	public String getLine_one_suscriber() {
		return line_one_suscriber;
	}

	public void setLine_one_suscriber(String line_one_suscriber) {
		this.line_one_suscriber = line_one_suscriber;
	}

	public String getLine_two_suscriber() {
		return line_two_suscriber;
	}

	public void setLine_two_suscriber(String line_two_suscriber) {
		this.line_two_suscriber = line_two_suscriber;
	}

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRadio_programs() {
		return radio_programs;
	}

	public void setRadio_programs(String radio_programs) {
		this.radio_programs = radio_programs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTime_hr() {
		return time_hr;
	}

	public void setTime_hr(String time_hr) {
		this.time_hr = time_hr;
	}

	public String getTime_min() {
		return time_min;
	}

	public void setTime_min(String time_min) {
		this.time_min = time_min;
	}

	public String getSun() {
		return sun;
	}

	public void setSun(String sun) {
		this.sun = sun;
	}

	public String getMon() {
		return mon;
	}

	public void setMon(String mon) {
		this.mon = mon;
	}

	public String getTues() {
		return tues;
	}

	public void setTues(String tues) {
		this.tues = tues;
	}

	public String getWed() {
		return wed;
	}

	public void setWed(String wed) {
		this.wed = wed;
	}

	public String getThur() {
		return thur;
	}

	public void setThur(String thur) {
		this.thur = thur;
	}

	public String getFri() {
		return fri;
	}

	public void setFri(String fri) {
		this.fri = fri;
	}

	public String getSat() {
		return sat;
	}

	public void setSat(String sat) {
		this.sat = sat;
	}

	public String getAlertMessages() {
		return alertMessages;
	}

	public void setAlertMessages(String alertMessages) {
		this.alertMessages = alertMessages;
	}

	public String getF_sex_status() {
		return f_sex_status;
	}

	public void setF_sex_status(String f_sex_status) {
		this.f_sex_status = f_sex_status;
	}

	public String getM_sex_status() {
		return m_sex_status;
	}

	public void setM_sex_status(String m_sex_status) {
		this.m_sex_status = m_sex_status;
	}

	public String getF_status() {
		return f_status;
	}

	public void setF_status(String f_status) {
		this.f_status = f_status;
	}

	public String getL_status() {
		return l_status;
	}

	public void setL_status(String l_status) {
		this.l_status = l_status;
	}

	public String getM_status() {
		return m_status;
	}

	public void setM_status(String m_status) {
		this.m_status = m_status;
	}

	public String getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSigninwith() {
		return signinwith;
	}

	public void setSigninwith(String signinwith) {
		this.signinwith = signinwith;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	ArrayList<HashMap<String, String>> list;
	ArrayList<HashMap<String, Integer>> list_count;

	public ArrayList<HashMap<String, Integer>> getList_count() {
		return list_count;
	}

	public void setList_count(ArrayList<HashMap<String, Integer>> list_count) {
		this.list_count = list_count;
	}

	public String getTotal_videos() {
		return total_videos;
	}

	public void setTotal_videos(String total_videos) {
		this.total_videos = total_videos;
	}

	public int getPagePosition() {
		return pagePosition;
	}

	public void setPagePosition(int pagePosition) {
		this.pagePosition = pagePosition;
	}

	public ArrayList<HashMap<String, String>> getList() {
		return list;
	}

	public void setList(ArrayList<HashMap<String, String>> list) {
		this.list = list;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPlayListId() {
		return playListId;
	}

	public void setPlayListId(String playListId) {
		this.playListId = playListId;
	}

	public String getThumbnailHigh() {
		return thumbnailHigh;
	}

	public void setThumbnailHigh(String thumbnailHigh) {
		this.thumbnailHigh = thumbnailHigh;
	}

	public String getChannelTitle() {
		return channelTitle;
	}

	public void setChannelTitle(String channelTitle) {
		this.channelTitle = channelTitle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuth_title() {
		return auth_title;
	}

	public void setAuth_title(String auth_title) {
		this.auth_title = auth_title;
	}

	public String getVid_Id() {
		return vid_Id;
	}

	public void setVid_Id(String vid_Id) {
		this.vid_Id = vid_Id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	// NEW GETTER SETTER

	public String getMaxresThembUrl() {
		return maxresThembUrl;
	}

	public void setMaxresThembUrl(String maxresThembUrl) {
		this.maxresThembUrl = maxresThembUrl;
	}

	// Getter Setter Values For Devote Screen
	String blessing;
	String nature;
	String name;
	String name_Optional;
	String there_Is;
	String sex;

	public String getBlessing() {
		return blessing;
	}

	public void setBlessing(String blessing) {
		this.blessing = blessing;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_Optional() {
		return name_Optional;
	}

	public void setName_Optional(String name_Optional) {
		this.name_Optional = name_Optional;
	}

	public String getThere_Is() {
		return there_Is;
	}

	public void setThere_Is(String there_Is) {
		this.there_Is = there_Is;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

}
