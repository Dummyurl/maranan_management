package com.marananmanagement.util;

public class Config {

	// API'S
	// public static String ROOT_SERVER_CLIENT =
	// "http://173.254.28.169/~maranant";
	public static String ROOT_SERVER_CLIENT = "http://50.87.25.184/";
	public static String ROOT_DEMO = "http://shofarj.com/marananapp";
	public static String ROOT_DEMO_CLIENT = "http://demo.inextsolutions.com/maranan";
	public static String UPDATE_STATUS = "/dedication_status.php?";
	public static String ALL_DEDICATIONS = "/all_dedication.php";
	public static String SEND_ALERT = "/alerts.php?";
	public static String GET_ALERT = "/get_alerts.php";
	public static String DELETE_ALERT = "/delete_alerts.php";
	public static String DELETE_ALERT_IMAGE = "/delete_alert_image.php";
	public static String ADD_RADIO_PROGRAMS = "/add_radio_program.php?";
	public static String GET_RADIO_PROGRAMS = "/get_all_radio_program.php";
	public static String DELETE_PROGRAM = "/program_delete.php?";
	public static String UPDATE_STATUS_RADIO_PROGRAM = "/update_status_radio_program.php?";
	public static String GET_SMS_SUBSCRIBERS = "/get_sms_subscribers.php";
	public static String DELETE_SMS_SUBSCRIBERS = "/delete_sms_subscribers.php";
	public static String SEND_MESSAGES = "/message.php?";
	public static String GET_MESSAGES = "/get_sms.php";
	public static String DELETE_MESSAGES = "/delete_sms.php";
	public static String ADD_RADIO_ALERT = "/add_radio_alert.php?";
	public static String GET_RADIO_ALERT = "/fetch_radio_alert.php";
	public static String DELETE_RADIO_ALERT = "/delete_radio_alert.php?";
	public static String PUBLISH_BROADCAST_ALERT_PROGRAM = "/program_brodcast.php?";
	public static String ADD_SUBSCRIBERS_DETAILS = "/add_subscriber.php?";
	public static String UPLOAD_NEWSLETTERS = "/upload_newsletter.php?";
	public static String GET_NEWSLETTERS = "/getnewsletters.php?";
	public static String GET_NEWSLETTERS_BY_YEAR = "/newsletter_by_year.php?";
	public static String GET_UNIQUEDATE = "/uniquedate.php?";
	public static String DELETE_NEWSLETTER = "/delete_newsletter.php?";
	public static String UPDATE_NEWSLETTER_STATUS = "/update_newsletter_status.php?";
	public static String NEWSLETTER_BROADCAST = "/newsletter_broadcast.php?";
	public static String GET_NEWSLETTERS_STATUS = "get_newsletter_status_for_channel.php";
	public static String GET_ALL_CHANNELS = "get_all_channel.php";
	public static String CHANNEL_MANAGEMENT = "channel_management.php?";
	public static String GET_YOUTUBE_CHANNELS = "youtube_channel.php?";
	public static String GET_CHANNEL_PLAYLIST = "channel_playlist.php?";
	public static String KEY_PLAYLIST = "playlist_id=";
	public static String UPDATE_VIDEO_STATUS = "update_video_status.php?";
	public static String UPLOAD_VIDEO = "uploadvideo.php?";
	public static String VIDEO_BROADCAST = "video_broadcast.php?";
	public static String CHANNEL_PRIORITY = "channel_priority.php?";
	public static String GET_LIVE_BROADCAST = "live_broadcast.php?";
	public static String UPLOAD_LIVE_BROADCAST = "uploadlivebroadcast.php?";
	public static String LIVE_BROADCAST_STATUS = "update_livebroadcast_status.php?";
	public static String LIVE_BROADCAST_SINGLE_STATUS = "update_livebroadcast_single_status.php?";
	public static String LIVE_BROADCAST_PUBLISH = "live_broadcast_publish.php?";
	public static String GET_LIVE_BROADCAST_LIST = "get_livebroadcast_channel.php?";
	public static String DELETE_LIVE_BROADCAST = "delete_live_broadcast.php?";
	public static String GET_LIVE_BROADCAST_IMAGES = "live_broadcast_images.php?";
	public static String DELETE_LIVE_BROADCAST_IMAGE = "delete_livebroadcast_image.php?";
	public static String GET_YOUTUBE_ACCOUNT_USERS = "youtube_account_users.php";
	public static String PUT_ID = "id=";
	public static String PUT_NAME = "&name=";
	public static String VIDEO_SEQUENCE_STATUS = "sequence_status.php?";
	public static String UPDATE_ADVERTISEMENT_STATUS = "update_advertisement_status.php?";
	public static String GET_VIDEO_ADVERTISEMENT = "get_advertisement.php";
	public static String UPDATE_VIDEO_ADVERTISEMENT_STATUS = "update_video_advertisement_status.php?";
	
	// Youtube API's
	public static final String DEVELOPER_KEY = "AIzaSyAhV9mM16u8tcBlu5THHNsKuctLkinkHMU";
    public static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search?";
    public static final String PLAY_LIST_URL = "https://www.googleapis.com/youtube/v3/playlistItems?";
    public static final String PLAY_LIST_ID_URL = "https://www.googleapis.com/youtube/v3/playlists?";
    public static final String VIEW_COUNT_URL = "https://www.googleapis.com/youtube/v3/videos?";
    public static final String KEY = "key=";
    public static final String VIDEO_ID = "id=";
    public static final String SERVER_KEY = KEY+"AIzaSyCx9vEuKRcqS92nduiKgNi2-ucJwu77yZw";
    public static final String CHANNEL_KEY = "&channelId=";
    public static final String CHANNEL_ID = CHANNEL_KEY+"UC1EhBfEKFkpba74cRDBEXxA";
    public static final String PLAY_LIST_KEY = "&playlistId=";
    public static final String PART = "&part=snippet,id,contentDetails";
    public static final String PART_ADD = "&part=snippet,contentDetails,statistics,status";
    public static final String ORDER = "&order=date";
    public static final String START_INDEX_ONE = "&start-index=1";
    public static final String START_INDEX_51 = "&start-index=51";
    public static final String NEXT_PAGE_TOKEN = "&pageToken=";
    public static final String MAX_RESULT = "&maxResults=50";
    public static final String PLAYLIST_ID = PLAY_LIST_KEY+"PLrLp80RyVyk1ErFf5dVNgtXT3UD1iOr6U";
    public static final String YOUTUBE_URL_SEARCH = SEARCH_URL+ SERVER_KEY + CHANNEL_ID + PART + ORDER + START_INDEX_ONE + MAX_RESULT;
    public static final String YOUTUBE_URL_PLAYLIST = PLAY_LIST_URL + SERVER_KEY + PART + START_INDEX_ONE + MAX_RESULT + ORDER;
    public static final String YOUTUBE_URL_PLAYLIST_UPTO50 = PLAY_LIST_URL + SERVER_KEY + PART + MAX_RESULT + ORDER;

    // GETTING PLAY LIST OF YOU TUBE FROM VERSION 2.1
    public static final String GET_PLAY_LIST_V2 = "https://gdata.youtube.com/feeds/api/playlists/"+PLAYLIST_ID+"?v=2.1&alt=jsonc";
     
    // YouTube video id
    public static final String YOUTUBE_VIDEO_CODE = "9qGNSnkLbfk";
    
    // Getting play list id from this API  
    public static final String GET_PLAY_LIST_ID = PLAY_LIST_ID_URL + SERVER_KEY + ORDER + PART + CHANNEL_ID;
    public static final String GET_VIDEO_VIEW_COUNT = VIEW_COUNT_URL + SERVER_KEY + PART_ADD + VIDEO_ID;
    public static String ROOT_YOUTUBE = "https://www.googleapis.com/youtube/v3";
}
