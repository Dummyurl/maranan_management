package com.marananmanagement.httpholder;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.marananmanagement.AddSubscribersActivity;
import com.marananmanagement.AlertEditPage;
import com.marananmanagement.AlertList;
import com.marananmanagement.MainActivity;
import com.marananmanagement.R;
import com.marananmanagement.SmsActivity;
import com.marananmanagement.SmsEditPage;
import com.marananmanagement.SmsSuscribersActivity;
import com.marananmanagement.VideoAdvertisement;
import com.marananmanagement.adapter.SmsAdapter;
import com.marananmanagement.database.MarananDB;
import com.marananmanagement.util.Config;
import com.marananmanagement.util.ConnectionDetector;
import com.marananmanagement.util.CustomMultiPartEntity;
import com.marananmanagement.util.CustomMultiPartEntity.ProgressListener;
import com.marananmanagement.util.GetterSetter;
import com.marananmanagement.util.ServiceHandler;
import com.marananmanagement.util.Utilities;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class AsyncRequest {
	Context context;
	static ConnectionDetector cd;
	static Boolean isInternetPresent = false;
	private static long totalSize = 0;
	private static int count;


	/*
	 * Get All Dedications for Administrator who control all the data over
	 * server
	 */
	public static void getAllDedications(final Context ctx, final String name, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {

			new AsyncTask<Void, Void, ArrayList<GetterSetter>>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected ArrayList<GetterSetter> doInBackground(Void... params) {
					ArrayList<GetterSetter> listDedications = new ArrayList<GetterSetter>();
					ServiceHandler sd = new ServiceHandler();
					String responce = sd.makeServiceCall(
							Config.ROOT_SERVER_CLIENT + Config.ALL_DEDICATIONS,
							ServiceHandler.GET);
					if (responce != null) {
						try {
							JSONObject json = new JSONObject(responce);
							JSONArray jArry = json.getJSONArray("details");
							for (int i = 0; i < jArry.length(); i++) {
								JSONObject jsonObj = jArry.getJSONObject(i);
								GetterSetter getset = new GetterSetter();
								getset.setId(jsonObj.getString("id"));
								getset.setNature(jsonObj.getString("nature"));
								getset.setName(jsonObj.getString("name")
										.replace("*", "'"));
								getset.setSex(jsonObj.getString("sex"));
								getset.setName_Optional(jsonObj.getString(
										"name_optional").replace("*", "'"));
								getset.setThere_Is(jsonObj
										.getString("there_is")
										.replace("*", "'"));
								getset.setBlessing(jsonObj
										.getString("status_blessing"));
								getset.setEmail(jsonObj.getString("email"));
								getset.setSigninwith(jsonObj
										.getString("sign_with"));
								getset.setTime(jsonObj.getString("time"));
								getset.setPublish(jsonObj.getString("publish"));
								getset.setF_status(jsonObj
										.getString("name_status"));
								getset.setL_status(jsonObj
										.getString("nameopt_status"));
								getset.setM_status(jsonObj
										.getString("thereis_status"));
								getset.setF_sex_status(jsonObj
										.getString("f_sex_status"));
								getset.setM_sex_status(jsonObj
										.getString("m_sex_status"));
								listDedications.add(getset);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						((Activity) ctx).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Utilities.showToast(ctx,
										"Server Not Responding");

							}
						});
					}

					return listDedications;
				}

				@Override
				protected void onPostExecute(ArrayList<GetterSetter> result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result != null && result.size() > 0) {
						MainActivity.getInstance().setAdapter(result);
					}
				}

			}.execute();
		}

	}

	/* Get Unique Dates */
	public static void getUniqueDates(final Context ctx) {
		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {

			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);

		} else {
			new AsyncTask<Void, Void, ArrayList<String>>() {

				@Override
				protected ArrayList<String> doInBackground(Void... params) {
					ArrayList<String> listdates = new ArrayList<String>();
					ServiceHandler sd = new ServiceHandler();
					String responce = sd.makeServiceCall(
							Config.ROOT_SERVER_CLIENT + Config.GET_UNIQUEDATE,
							ServiceHandler.GET);

					if (responce != null) {
						try {
							JSONObject json = new JSONObject(responce);
							JSONArray jArry = json.getJSONArray("value");
							for (int i = 0; i < jArry.length(); i++) {
								JSONObject jsonObj = jArry.getJSONObject(i);
								listdates.add(jsonObj.getString("date"));
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						((Activity) ctx).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Utilities.showToast(ctx,
										"Server Not Responding");
							}
						});
					}
					return listdates;
				}

				@Override
				protected void onPostExecute(ArrayList<String> listdates) {
					super.onPostExecute(listdates);
					if (listdates != null && listdates.size() > 0) {
						MainActivity.getInstance()
								.setAdapterForDates(listdates);
					}
				}

			}.execute();
		}

	}

	/* Get All Messages(SMS) */
	public static void getAllMessages(final Context ctx, final String name, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {

			new AsyncTask<Void, Void, ArrayList<GetterSetter>>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected ArrayList<GetterSetter> doInBackground(Void... params) {

					ArrayList<GetterSetter> listMessage = new ArrayList<GetterSetter>();
					ServiceHandler sd = new ServiceHandler();
					String responce = sd.makeServiceCall(
							Config.ROOT_SERVER_CLIENT + Config.GET_MESSAGES,
							ServiceHandler.GET);
					if (responce != null) {
						try {
							JSONObject json = new JSONObject(responce);
							JSONArray jArry = json.getJSONArray("value");
							for (int i = 0; i < jArry.length(); i++) {
								JSONObject jsonObj = jArry.getJSONObject(i);
								GetterSetter getset = new GetterSetter();
								getset.setId(jsonObj.getString("id"));
								getset.setMessage(jsonObj.getString("message"));
								getset.setMode(jsonObj.getString("mode"));
								getset.setTime_hr(jsonObj.getString("time_hr"));
								getset.setTime_min(jsonObj
										.getString("time_min"));
								getset.setSun(jsonObj.getString("sun"));
								getset.setMon(jsonObj.getString("mon"));
								getset.setTues(jsonObj.getString("tue"));
								getset.setWed(jsonObj.getString("wed"));
								getset.setThur(jsonObj.getString("thu"));
								getset.setFri(jsonObj.getString("fri"));
								getset.setSat(jsonObj.getString("sat"));
								listMessage.add(getset);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						((Activity) ctx).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Utilities.showToast(ctx,
										"Server Not Responding");
							}
						});
					}
					return listMessage;
				}

				@Override
				protected void onPostExecute(ArrayList<GetterSetter> result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result != null && result.size() > 0) {
						SmsActivity.getInstance().setAdapterMessage(result);
					}
				}

			}.execute();
		}

	}

	/* Delete Messages(SMS) */
	public static void deleteMessages(final Context ctx, final String id) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {
			new AsyncTask<String, Void, JSONObject>() {

				@Override
				protected JSONObject doInBackground(String... params) {

					JSONObject jObj = null;
					HttpParams params2 = new BasicHttpParams();
					params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
							HttpVersion.HTTP_1_1);
					DefaultHttpClient mHttpClient = new DefaultHttpClient(
							params2);
					String url = Config.ROOT_SERVER_CLIENT
							+ Config.DELETE_MESSAGES;

					HttpPost httppost = new HttpPost(url);

					MultipartEntity multipartEntity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);
					try {
						multipartEntity.addPart("message_id",
								new StringBody(id));
						httppost.setEntity(multipartEntity);
						HttpResponse response = mHttpClient.execute(httppost);
						HttpEntity r_entity = response.getEntity();
						String strResponse = EntityUtils.toString(r_entity);
						jObj = new JSONObject(strResponse);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return jObj;
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					super.onPostExecute(result);
					if (result != null) {
						try {
							if (result.get("success").equals(true)) {
								Utilities.showToast(ctx, result.get("value")
										.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}.execute();
		}
	}

	/* Send Messages */
	public static void sendMessages(final Context ctx, final String classname,
			final String id, final String message, final String mode,
			final String subs_id, final String time_Hr, final String time_min,
			final String sun, final String mond, final String tues,
			final String wed, final String thur, final String fri,
			final String sat, final ProgressBar pDialog) {
		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {
			new AsyncTask<String, Void, JSONObject>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected JSONObject doInBackground(String... params) {
					JSONObject jObj = null;
					HttpParams params2 = new BasicHttpParams();
					params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
							HttpVersion.HTTP_1_1);
					DefaultHttpClient mHttpClient = new DefaultHttpClient(
							params2);
					HttpPost httppost = new HttpPost(Config.ROOT_SERVER_CLIENT
							+ Config.SEND_MESSAGES);

					MultipartEntity multipartEntity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

					try {
						if (id != null)
							multipartEntity
									.addPart("message_id", new StringBody(id,
											Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("message_id",
									new StringBody(""));

						if (!params[1].equals(""))
							multipartEntity.addPart("message", new StringBody(
									params[1], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("message", new StringBody(
									""));

						if (!params[2].equals(""))
							multipartEntity.addPart("mode", new StringBody(
									params[2], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("mode", new StringBody(""));

						if (params[3] != null) {
							if (!params[3].equals(""))
								multipartEntity.addPart(
										"subscribers_id",
										new StringBody(params[3], Charset
												.forName("UTF-8")));
							else
								multipartEntity.addPart("subscribers_id",
										new StringBody(""));
						} else {
							multipartEntity.addPart("subscribers_id",
									new StringBody(""));
						}

						if (!params[4].equals(""))
							multipartEntity.addPart("time_hr", new StringBody(
									params[4]));
						else
							multipartEntity.addPart("time_hr", new StringBody(
									""));

						if (!params[5].equals(""))
							multipartEntity.addPart("time_min", new StringBody(
									params[5]));
						else
							multipartEntity.addPart("time_min", new StringBody(
									""));

						if (!params[6].equals(""))
							multipartEntity.addPart("sun", new StringBody(
									params[6], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("sun", new StringBody(""));

						if (!params[7].equals(""))
							multipartEntity.addPart("mon", new StringBody(
									params[7], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("mon", new StringBody(""));

						if (!params[8].equals(""))
							multipartEntity.addPart("tue", new StringBody(
									params[8], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("tue", new StringBody(""));

						if (!params[9].equals(""))
							multipartEntity.addPart("wed", new StringBody(
									params[9], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("wed", new StringBody(""));

						if (!params[10].equals(""))
							multipartEntity.addPart("thu", new StringBody(
									params[10], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("thu", new StringBody(""));

						if (!params[11].equals(""))
							multipartEntity.addPart("fri", new StringBody(
									params[11], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("fri", new StringBody(""));

						if (!params[12].equals(""))
							multipartEntity.addPart("sat", new StringBody(
									params[12], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("sat", new StringBody(""));

						httppost.setEntity(multipartEntity);
						HttpResponse response = mHttpClient.execute(httppost);
						HttpEntity r_entity = response.getEntity();
						String strResponse = EntityUtils.toString(r_entity);
						try {
							jObj = new JSONObject(strResponse);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return jObj;
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result != null && result.length() > 0) {
						SmsEditPage.getInstance().setAdapter(result);
					}
				}

			}.execute(id, message, mode, subs_id, time_Hr, time_min, sun, mond,
					tues, wed, thur, fri, sat);
		}
	}

	/* Get All Sms Subscribers */
	public static void getSmsSubscribers(final Context ctx, final String name, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {

			new AsyncTask<Void, Void, ArrayList<GetterSetter>>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected ArrayList<GetterSetter> doInBackground(Void... params) {
					ArrayList<GetterSetter> listSms = new ArrayList<GetterSetter>();
					ServiceHandler sd = new ServiceHandler();
					String responce = sd.makeServiceCall(
							Config.ROOT_SERVER_CLIENT
									+ Config.GET_SMS_SUBSCRIBERS,
							ServiceHandler.GET);

					if (responce != null) {
						try {
							JSONObject json = new JSONObject(responce);
							JSONArray jArry = json.getJSONArray("value");
							for (int i = 0; i < jArry.length(); i++) {
								JSONObject jsonObj = jArry.getJSONObject(i);
								GetterSetter getset = new GetterSetter();
								getset.setId(jsonObj.getString("id"));
								getset.setName_suscriber(jsonObj
										.getString("firstname"));
								getset.setCity_suscriber(jsonObj
										.getString("city"));
								getset.setFamily_suscriber(jsonObj
										.getString("family"));
								getset.setMobile_one_suscriber(jsonObj
										.getString("mobileno1"));
								getset.setMobile_two_suscriber(jsonObj
										.getString("mobileno2"));
								getset.setMobile_three_suscriber(jsonObj
										.getString("mobileno3"));
								getset.setMobile_four_suscriber(jsonObj
										.getString("mobileno4"));
								getset.setLine_one_suscriber(jsonObj
										.getString("landline1"));
								getset.setLine_two_suscriber(jsonObj
										.getString("landline2"));
								getset.setCountry_code_suscriber(jsonObj
										.getString("country_code"));
								listSms.add(getset);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						((Activity) ctx).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Utilities.showToast(ctx,
										"Server Not Responding");
							}
						});

					}

					return listSms;
				}

				@Override
				protected void onPostExecute(ArrayList<GetterSetter> result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result != null && result.size() > 0) {
						SmsSuscribersActivity.getInstance().setAdapter(result);
					}
				}

			}.execute();
		}
	}

	/* Delete Sms Subscribers */
	public static void deleteSmsSubscribers(final Context ctx,
			final String subscriber_id) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {

			new AsyncTask<String, Void, JSONObject>() {

				@Override
				protected JSONObject doInBackground(String... params) {

					JSONObject jObj = null;
					HttpParams params2 = new BasicHttpParams();
					params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
							HttpVersion.HTTP_1_1);
					DefaultHttpClient mHttpClient = new DefaultHttpClient(
							params2);
					String url = Config.ROOT_SERVER_CLIENT
							+ Config.DELETE_SMS_SUBSCRIBERS;

					HttpPost httppost = new HttpPost(url);

					MultipartEntity multipartEntity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);
					try {
						multipartEntity.addPart("subscriber_id",
								new StringBody(params[0]));
						httppost.setEntity(multipartEntity);
						HttpResponse response = mHttpClient.execute(httppost);
						HttpEntity r_entity = response.getEntity();
						String strResponse = EntityUtils.toString(r_entity);
						jObj = new JSONObject(strResponse);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return jObj;
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					super.onPostExecute(result);
					if (result != null && result.length() > 0) {
						SmsSuscribersActivity.getInstance().setDeleteResponse(
								result);
					}
				}

			}.execute(subscriber_id);
		}
	}

	/* Send Messages Notification */
	public static void sendMessagesNotification(final Context ctx,
			final String classname, final String id, final String message,
			final String mode, final String subs_id, final String time_Hr,
			final String time_min, final String sun, final String mond,
			final String tues, final String wed, final String thur,
			final String fri, final String sat, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {
			new AsyncTask<String, Void, JSONObject>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected JSONObject doInBackground(String... params) {
					JSONObject jObj = null;
					HttpParams params2 = new BasicHttpParams();
					params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
							HttpVersion.HTTP_1_1);
					DefaultHttpClient mHttpClient = new DefaultHttpClient(
							params2);
					HttpPost httppost = new HttpPost(Config.ROOT_SERVER_CLIENT
							+ Config.SEND_MESSAGES);

					MultipartEntity multipartEntity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

					try {
						if (params[0] != null)
							multipartEntity.addPart(
									"message_id",
									new StringBody(params[0], Charset
											.forName("UTF-8")));
						else
							multipartEntity.addPart("message_id",
									new StringBody(""));

						if (!params[1].equals(""))
							multipartEntity.addPart("message", new StringBody(
									params[1], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("message", new StringBody(
									""));

						if (!params[2].equals(""))
							multipartEntity.addPart("mode", new StringBody(
									params[2], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("mode", new StringBody(""));

						if (params[3] != null) {
							if (!params[3].equals(""))
								multipartEntity.addPart(
										"subscribers_id",
										new StringBody(params[3], Charset
												.forName("UTF-8")));
							else
								multipartEntity.addPart("subscribers_id",
										new StringBody(""));
						} else {
							multipartEntity.addPart("subscribers_id",
									new StringBody(""));
						}

						if (!params[4].equals(""))
							multipartEntity.addPart("time_hr", new StringBody(
									params[4]));
						else
							multipartEntity.addPart("time_hr", new StringBody(
									""));

						if (!params[5].equals(""))
							multipartEntity.addPart("time_min", new StringBody(
									params[5]));
						else
							multipartEntity.addPart("time_min", new StringBody(
									""));

						if (!params[6].equals(""))
							multipartEntity.addPart("sun", new StringBody(
									params[6], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("sun", new StringBody(""));

						if (!params[7].equals(""))
							multipartEntity.addPart("mon", new StringBody(
									params[7], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("mon", new StringBody(""));

						if (!params[8].equals(""))
							multipartEntity.addPart("tue", new StringBody(
									params[8], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("tue", new StringBody(""));

						if (!params[9].equals(""))
							multipartEntity.addPart("wed", new StringBody(
									params[9], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("wed", new StringBody(""));

						if (!params[10].equals(""))
							multipartEntity.addPart("thu", new StringBody(
									params[10], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("thu", new StringBody(""));

						if (!params[11].equals(""))
							multipartEntity.addPart("fri", new StringBody(
									params[11], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("fri", new StringBody(""));

						if (!params[12].equals(""))
							multipartEntity.addPart("sat", new StringBody(
									params[12], Charset.forName("UTF-8")));
						else
							multipartEntity.addPart("sat", new StringBody(""));

						httppost.setEntity(multipartEntity);
						HttpResponse response = mHttpClient.execute(httppost);
						HttpEntity r_entity = response.getEntity();
						String strResponse = EntityUtils.toString(r_entity);
						try {
							jObj = new JSONObject(strResponse);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return jObj;
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					Utilities.dismissProgressDialog(pDialog);
					if (result != null && result.length() > 0) {
						SmsSuscribersActivity.getInstance()
								.setResponseForSmsMessage(result);
					}
				};

			}.execute(id, message, mode, subs_id, time_Hr, time_min, sun, mond,
					tues, wed, thur, fri, sat);
		}
	}

	/* Add Subscribers */
	public static void addSubscribers(final Context ctx,
			final String classname, final String id, final String firstname,
			final String family, final String mobileno1,
			final String mobileno2, final String mobileno3,
			final String mobileno4, final String landline1,
			final String landline2, final String city,
			final String country_code1, final String country_code2,
			final String country_code3, final String country_code4, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {
			new AsyncTask<String, String, String>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected String doInBackground(String... params) {
					String response_str = null;
					HttpClient client = new DefaultHttpClient();
					MultipartEntity reqEntity = new MultipartEntity();
					HttpPost post = new HttpPost(Config.ROOT_SERVER_CLIENT
							+ Config.ADD_SUBSCRIBERS_DETAILS);

					try {

						if (params[0] != null) {
							if (!params[0].equals("")) {
								reqEntity.addPart("id", new StringBody(
										params[0], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("id", new StringBody(""));
							}
						} else {
							reqEntity.addPart("id", new StringBody(""));
						}

						if (params[1] != null) {
							if (!params[1].equals("")) {
								reqEntity.addPart("firstname", new StringBody(
										params[1], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("firstname", new StringBody(
										""));
							}
						} else {
							reqEntity.addPart("firstname", new StringBody(""));
						}

						if (params[2] != null) {
							if (!params[2].equals("")) {
								reqEntity.addPart("family", new StringBody(
										params[2], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("family", new StringBody(""));
							}
						} else {
							reqEntity.addPart("family", new StringBody(""));
						}

						if (params[3] != null) {
							if (!params[3].equals("")) {
								reqEntity.addPart("mobileno1", new StringBody(
										params[3], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("mobileno1", new StringBody(
										""));
							}
						} else {
							reqEntity.addPart("mobileno1", new StringBody(""));
						}

						if (params[4] != null) {
							if (!params[4].equals("")) {
								reqEntity.addPart("mobileno2", new StringBody(
										params[4], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("mobileno2", new StringBody(
										""));
							}
						} else {
							reqEntity.addPart("mobileno2", new StringBody(""));
						}

						if (params[5] != null) {
							if (!params[5].equals("")) {
								reqEntity.addPart("mobileno3", new StringBody(
										params[5], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("mobileno3", new StringBody(
										""));
							}
						} else {
							reqEntity.addPart("mobileno3", new StringBody(""));
						}

						if (params[6] != null) {
							if (!params[6].equals("")) {
								reqEntity.addPart("mobileno4", new StringBody(
										params[6], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("mobileno4", new StringBody(
										""));
							}
						} else {
							reqEntity.addPart("mobileno4", new StringBody(""));
						}

						if (params[7] != null) {
							if (!params[7].equals("")) {
								reqEntity.addPart("landline1", new StringBody(
										params[7], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("landline1", new StringBody(
										""));
							}
						} else {
							reqEntity.addPart("landline1", new StringBody(""));
						}

						if (params[8] != null) {
							if (!params[8].equals("")) {
								reqEntity.addPart("landline2", new StringBody(
										params[8], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("landline2", new StringBody(
										""));
							}
						} else {
							reqEntity.addPart("landline2", new StringBody(""));
						}

						if (params[9] != null) {
							if (!params[9].equals("")) {
								reqEntity.addPart("city", new StringBody(
										params[9], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("city", new StringBody(""));
							}
						} else {
							reqEntity.addPart("city", new StringBody(""));
						}

						if (params[10] != null) {
							if (!params[10].equals("")) {
								reqEntity.addPart(
										"country_code1",
										new StringBody(params[10], Charset
												.forName("UTF-8")));
							} else {
								reqEntity.addPart("country_code1",
										new StringBody(""));
							}
						} else {
							reqEntity.addPart("country_code1", new StringBody(
									""));
						}

						if (params[11] != null) {
							if (!params[11].equals("")) {
								reqEntity.addPart(
										"country_code2",
										new StringBody(params[11], Charset
												.forName("UTF-8")));
							} else {
								reqEntity.addPart("country_code2",
										new StringBody(""));
							}
						} else {
							reqEntity.addPart("country_code2", new StringBody(
									""));
						}

						if (params[12] != null) {
							if (!params[12].equals("")) {
								reqEntity.addPart(
										"country_code3",
										new StringBody(params[12], Charset
												.forName("UTF-8")));
							} else {
								reqEntity.addPart("country_code3",
										new StringBody(""));
							}
						} else {
							reqEntity.addPart("country_code3", new StringBody(
									""));
						}

						if (params[13] != null) {
							if (!params[13].equals("")) {
								reqEntity.addPart(
										"country_code4",
										new StringBody(params[13], Charset
												.forName("UTF-8")));
							} else {
								reqEntity.addPart("country_code4",
										new StringBody(""));
							}
						} else {
							reqEntity.addPart("country_code4", new StringBody(
									""));
						}

						post.setEntity(reqEntity);
						HttpResponse response = client.execute(post);
						HttpEntity resEntity = response.getEntity();
						response_str = EntityUtils.toString(resEntity);

					} catch (ClientProtocolException e) {
						Log.e("ClientProtocolException??",
								"ClientProtocolException?? " + e.toString());
					} catch (IOException e) {
						Log.e("IOException??", "IOException?? " + e.toString());
					}
					return response_str;
				}

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result.length() > 0) {
						AddSubscribersActivity.getInstance().setResponseResult(
								result);
					}
				}
			}.execute(id, firstname, family, mobileno1, mobileno2, mobileno3,
					mobileno4, landline1, landline2, city, country_code1,
					country_code2, country_code3, country_code4);
		}
	}

	/* Send Radio Alerts Notification */
	public static void sendRadioAlertsNotification(final Context ctx,
			final String name, final String noti_id, final String id,
			final String classname, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {
			new AsyncTask<String, String, String>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected String doInBackground(String... params) {
					String response_str = null;
					HttpClient client = new DefaultHttpClient();
					MultipartEntity reqEntity = new MultipartEntity();
					HttpPost post = null;
					if (params[2].equals("RadioProgram")) {
						post = new HttpPost(Config.ROOT_SERVER_CLIENT
								+ Config.PUBLISH_BROADCAST_ALERT_PROGRAM);

					} else if (params[2].equals("ChannelManagement")) {

						post = new HttpPost(Config.ROOT_SERVER_CLIENT
								+ Config.LIVE_BROADCAST_PUBLISH);

					} else {
						post = new HttpPost(Config.ROOT_SERVER_CLIENT
								+ Config.NEWSLETTER_BROADCAST);
					}

					try {

						if (params[0] != null) {
							if (!params[0].equals("")) {
								reqEntity.addPart("id", new StringBody(
										params[0], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("id", new StringBody(""));
							}
						} else {
							reqEntity.addPart("id", new StringBody(""));
						}

						if (params[1] != null) {
							if (!params[1].equals("")) {
								reqEntity.addPart(
										"publish_notification_id",
										new StringBody(params[1], Charset
												.forName("UTF-8")));
							} else {
								reqEntity.addPart("publish_notification_id",
										new StringBody(""));
							}
						} else {
							reqEntity.addPart("publish_notification_id",
									new StringBody(""));
						}
						post.setEntity(reqEntity);
						HttpResponse response = client.execute(post);
						HttpEntity resEntity = response.getEntity();
						response_str = EntityUtils.toString(resEntity);

					} catch (ClientProtocolException e) {
						Log.e("ClientProtocolException??",
								"ClientProtocolException?? " + e.toString());
					} catch (IOException e) {
						Log.e("IOException??", "IOException?? " + e.toString());
					}
					return response_str;
				}

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result.length() > 0) {
						SmsAdapter.getInstance()
								.setRadioAlertsNotificationResponse(result);
					}
				}
			}.execute(noti_id, id, classname);
		}
	}

	/* Send Channel Alerts Notification */
	public static void sendChannelAlertsNotification(final Context ctx,
			final String name, final String playlist_id, final String video_id,
			final String publish_notification_id, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {
			new AsyncTask<String, String, String>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected String doInBackground(String... params) {
					String response_str = null;
					HttpClient client = new DefaultHttpClient();
					MultipartEntity reqEntity = new MultipartEntity();
					HttpPost post = null;
					post = new HttpPost(Config.ROOT_SERVER_CLIENT
							+ Config.VIDEO_BROADCAST);

					try {

						if (params[0] != null) {
							if (!params[0].equals("")) {
								reqEntity.addPart(
										"playlist_id",
										new StringBody(params[0], Charset
												.forName("UTF-8")));
							} else {
								reqEntity.addPart("playlist_id",
										new StringBody(""));
							}
						} else {
							reqEntity
									.addPart("playlist_id", new StringBody(""));
						}

						if (params[1] != null) {
							if (!params[1].equals("")) {
								reqEntity.addPart("video_id", new StringBody(
										params[1], Charset.forName("UTF-8")));
							} else {
								reqEntity.addPart("video_id",
										new StringBody(""));
							}
						} else {
							reqEntity.addPart("video_id", new StringBody(""));
						}

						if (params[2] != null) {
							if (!params[2].equals("")) {
								reqEntity.addPart(
										"publish_notification_id",
										new StringBody(params[2], Charset
												.forName("UTF-8")));
							} else {
								reqEntity.addPart("publish_notification_id",
										new StringBody(""));
							}
						} else {
							reqEntity.addPart("publish_notification_id",
									new StringBody(""));
						}
						post.setEntity(reqEntity);
						HttpResponse response = client.execute(post);
						HttpEntity resEntity = response.getEntity();
						response_str = EntityUtils.toString(resEntity);

					} catch (ClientProtocolException e) {
						Log.e("ClientProtocolException??",
								"ClientProtocolException?? " + e.toString());
					} catch (IOException e) {
						Log.e("IOException??", "IOException?? " + e.toString());
					}
					return response_str;
				}

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result.length() > 0) {
						SmsAdapter.getInstance()
								.setChannelAlertsNotificationResponse(result);
					}
				}
			}.execute(playlist_id, video_id, publish_notification_id);
		}
	}

	/* Get All Alerts */
	public static void getAllAlerts(final Context ctx, final String name,
			final MarananDB db, final SQLiteDatabase sqlitedb, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {
			new AsyncTask<Void, Void, ArrayList<GetterSetter>>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected ArrayList<GetterSetter> doInBackground(Void... params) {

					ArrayList<GetterSetter> listAlerts = new ArrayList<GetterSetter>();
					ServiceHandler sd = new ServiceHandler();
					String responce = sd.makeServiceCall(
							Config.ROOT_SERVER_CLIENT + Config.GET_ALERT,
							ServiceHandler.GET);

					try {
						// First of All To delete All Records from Database
						if (Utilities.doesDatabaseExist(ctx, "MarananManagement"))
							db.deleteAllRecord();

						JSONObject json = new JSONObject(responce);
						JSONArray jArry = json.getJSONArray("value");
						for (int i = 0; i < jArry.length(); i++) {
							JSONObject jsonObj = jArry.getJSONObject(i);
							GetterSetter getset = new GetterSetter();
							if (!jsonObj.getString("id").equals("")) {
								getset.setId(jsonObj.getString("id"));
							} else {
								getset.setId("");
							}

							if (!jsonObj.getString("message").equals("")) {
								getset.setAlertMessages(Utilities
										.encodeImoString(jsonObj
												.getString("message")));
							} else {
								getset.setAlertMessages("");
							}

							if (!jsonObj.getString("description").equals("")) {
								getset.setDescriptions(Utilities
										.encodeImoString(jsonObj
												.getString("description")));
							} else {
								getset.setDescriptions("");
							}

							JSONObject jsonObjImg =  jsonObj.getJSONObject("image");

							if (jsonObjImg != null && jsonObjImg.length() > 0) {
								int l = 1;
								ArrayList<String> listImage = new ArrayList<String>();
								for (int j = 0; j < jsonObjImg.length(); j++) {
									if (!jsonObjImg.getString("image" + l).equals("")) {
										listImage.add(jsonObjImg.getString("image" + l));
										getset.setListImage(listImage);
									} else {
										getset.setListImage(null);
									}
									l++;
								}

							} else {
								getset.setListImage(null);
							}

							if (!jsonObj.getString("audio_alert").equals("")) {
								getset.setAlert_audio(jsonObj.getString("audio_alert"));
							} else {
								getset.setAlert_audio("");
							}

							if (!jsonObj.getString("phone").equals("")) {
								getset.setPhone(jsonObj.getString("phone"));
							} else {
								getset.setPhone("");
							}

							if (!jsonObj.getString("mode").equals("")) {
								getset.setMode(jsonObj.getString("mode"));
							} else {
								getset.setMode("");
							}

							if (!jsonObj.getString("state").equals("")) {
								getset.setState(jsonObj.getString("state"));
							} else {
								getset.setState("");
							}

							if (!jsonObj.getString("time_hr").equals("")) {
								getset.setTime_hr(jsonObj.getString("time_hr"));
							} else {
								getset.setTime_hr("");
							}

							if (!jsonObj.getString("time_min").equals("")) {
								getset.setTime_min(jsonObj.getString("time_min"));
							} else {
								getset.setTime_min("");
							}

							if (!jsonObj.getString("sun").equals("")) {
								getset.setSun(jsonObj.getString("sun"));
							} else {
								getset.setSun("");
							}

							if (!jsonObj.getString("mon").equals("")) {
								getset.setMon(jsonObj.getString("mon"));
							} else {
								getset.setMon("");
							}

							if (!jsonObj.getString("tue").equals("")) {
								getset.setTues(jsonObj.getString("tue"));
							} else {
								getset.setTues("");
							}

							if (!jsonObj.getString("wed").equals("")) {
								getset.setWed(jsonObj.getString("wed"));
							} else {
								getset.setWed("");
							}

							if (!jsonObj.getString("thu").equals("")) {
								getset.setThur(jsonObj.getString("thu"));
							} else {
								getset.setThur("");
							}

							if (!jsonObj.getString("fri").equals("")) {
								getset.setFri(jsonObj.getString("fri"));
							} else {
								getset.setFri("");
							}

							if (!jsonObj.getString("sat").equals("")) {
								getset.setSat(jsonObj.getString("sat"));
							} else {
								getset.setSat("");
							}

							listAlerts.add(getset);

							if (Utilities.doesDatabaseExist(ctx,
									"MarananManagement")) {
								MarananDB db = new MarananDB(ctx);
								db.insertRecords(sqlitedb, getset);

							} else {
								MarananDB db = new MarananDB(ctx);
								db.insertRecords(sqlitedb, getset);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					return listAlerts;
				}

				@Override
				protected void onPostExecute(ArrayList<GetterSetter> result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result != null && result.size() > 0) {
						AlertList.getInstance().setAdapter(result);
					}
				}
			}.execute();
		}
	}

	/* Delete Alerts */
	public static void deleteAlerts(OnDismissCallback onDismissCallback,
			String simpleName, String id) {

		new AsyncTask<String, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(String... params) {

				JSONObject jObj = null;
				HttpParams params2 = new BasicHttpParams();
				params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
						HttpVersion.HTTP_1_1);
				DefaultHttpClient mHttpClient = new DefaultHttpClient(params2);
				String url = Config.ROOT_SERVER_CLIENT + Config.DELETE_ALERT;

				HttpPost httppost = new HttpPost(url);

				MultipartEntity multipartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					multipartEntity.addPart("id", new StringBody(params[0]));
					httppost.setEntity(multipartEntity);
					HttpResponse response = mHttpClient.execute(httppost);
					HttpEntity r_entity = response.getEntity();
					String strResponse = EntityUtils.toString(r_entity);
					jObj = new JSONObject(strResponse);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return jObj;
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				super.onPostExecute(result);
				if (result != null) {
					AlertList.getInstance().setDeleteResponse(result);
				}
			}
		}.execute(id);
	}

	/* Send Alerts */
	public static void sendAlert(final Context ctx, final String simpleName,
								 String stringExtra, String encodeImoString,
								 String encodeImoString2, String string, String string2,
								 final ArrayList<String> listImages, String srcPath, String mode,
								 String state, String phone, String sun, String mond, String tues,
								 String wed, String thur, String fri, String sat,
								 final ProgressBar seek_bar_radio, final int imageCount) {

		new AsyncTask<String, String, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				seek_bar_radio.setProgress(0);
			}

			@Override
			protected String doInBackground(String... params) {
				String response_str = null;
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(Config.ROOT_SERVER_CLIENT
						+ Config.SEND_ALERT);

				try {
					CustomMultiPartEntity reqEntity = new CustomMultiPartEntity(
							new ProgressListener() {

								@Override
								public void transferred(long num) {
									publishProgress(""
											+ (int) ((num / (float) totalSize) * 100));
								}
							}, Charset.forName("UTF-8"));

					if (params[0] != null) {
						if (!params[0].equals("")) {
							reqEntity.addPart("alert_id", new StringBody(
									params[0], Charset.forName("UTF-8")));
						} else {
							reqEntity.addPart("alert_id", new StringBody(""));
						}
					} else {
						reqEntity.addPart("alert_id", new StringBody(""));
					}

					if (params[1] != null) {
						if (!params[1].equals("")) {
							reqEntity.addPart("message", new StringBody(
									params[1], Charset.forName("UTF-8")));
						} else {
							reqEntity.addPart("message", new StringBody(""));
						}
					} else {
						reqEntity.addPart("message", new StringBody(""));
					}

					if (params[2] != null) {
						if (!params[2].equals("")) {
							reqEntity.addPart("description", new StringBody(
									params[2], Charset.forName("UTF-8")));
						} else {
							reqEntity
									.addPart("description", new StringBody(""));
						}
					} else {
						reqEntity.addPart("description", new StringBody(""));
					}

					if (params[3] != null) {
						if (!params[3].equals("")) {
							reqEntity.addPart("time_hr", new StringBody(
									params[3], Charset.forName("UTF-8")));
						} else {
							reqEntity.addPart("time_hr", new StringBody(""));
						}
					} else {
						reqEntity.addPart("time_hr", new StringBody(""));
					}

					if (params[4] != null) {
						if (!params[4].equals("")) {
							reqEntity.addPart("time_min", new StringBody(
									params[4], Charset.forName("UTF-8")));
						} else {
							reqEntity.addPart("time_min", new StringBody(""));
						}
					} else {
						reqEntity.addPart("time_min", new StringBody(""));
					}

//					if (params[5] != null) {
//						if (!params[5].equals("")) {
//							File imageFile = new File(params[5]);
//							FileBody bodyImage = new FileBody(imageFile, "",
//									"UTF-8");
//							reqEntity.addPart("image", bodyImage);
//						} else {
//							reqEntity.addPart("image", new StringBody("",
//									Charset.forName("UTF-8")));
//						}
//					} else {
//						reqEntity.addPart("image",
//								new StringBody("", Charset.forName("UTF-8")));
//					}

					if (params[5] != null) {
						if (!params[5].equals("")) {

							File audioFile = new File(params[5]);
							FileBody bodyAudio = new FileBody(audioFile, "",
									"UTF-8");
							reqEntity.addPart("audio_alert", bodyAudio);

						} else {
							reqEntity.addPart("audio_alert", new StringBody("",
									Charset.forName("UTF-8")));
						}
					} else {
						reqEntity.addPart("audio_alert", new StringBody("",
								Charset.forName("UTF-8")));
					}

					if (params[6] != null) {
						if (!params[6].equals("")) {
							reqEntity.addPart("mode", new StringBody(params[6],
									Charset.forName("UTF-8")));
						} else {
							reqEntity.addPart("mode", new StringBody(""));
						}
					} else {
						reqEntity.addPart("mode", new StringBody(""));
					}

					if (params[7] != null) {
						if (!params[7].equals("")) {
							reqEntity.addPart("state", new StringBody(
									params[7], Charset.forName("UTF-8")));
						} else {
							reqEntity.addPart("state", new StringBody(""));
						}
					} else {
						reqEntity.addPart("state", new StringBody(""));
					}

					if (params[8] != null) {
						if (!params[8].equals("")) {
							reqEntity.addPart("phone", new StringBody(
									params[8], Charset.forName("UTF-8")));
						} else {
							reqEntity.addPart("phone", new StringBody(""));
						}
					} else {
						reqEntity.addPart("phone", new StringBody(""));
					}

					if (!params[9].equals(""))
						reqEntity.addPart("sun", new StringBody(params[9],
								Charset.forName("UTF-8")));
					else
						reqEntity.addPart("sun", new StringBody(""));

					if (!params[10].equals(""))
						reqEntity.addPart("mon", new StringBody(params[10],
								Charset.forName("UTF-8")));
					else
						reqEntity.addPart("mon", new StringBody(""));

					if (!params[11].equals(""))
						reqEntity.addPart("tue", new StringBody(params[11],
								Charset.forName("UTF-8")));
					else
						reqEntity.addPart("tue", new StringBody(""));

					if (!params[12].equals(""))
						reqEntity.addPart("wed", new StringBody(params[12],
								Charset.forName("UTF-8")));
					else
						reqEntity.addPart("wed", new StringBody(""));

					if (!params[13].equals(""))
						reqEntity.addPart("thu", new StringBody(params[13],
								Charset.forName("UTF-8")));
					else
						reqEntity.addPart("thu", new StringBody(""));

					if (!params[14].equals(""))
						reqEntity.addPart("fri", new StringBody(params[14],
								Charset.forName("UTF-8")));
					else
						reqEntity.addPart("fri", new StringBody(""));

					if (!params[15].equals(""))
						reqEntity.addPart("sat", new StringBody(params[15],
								Charset.forName("UTF-8")));
					else
						reqEntity.addPart("sat", new StringBody(""));

					if(listImages != null && listImages.size() > 0){
						 count = (listImages.size() - imageCount);
					}else{
						 count = imageCount;
					}

					reqEntity.addPart("image_count", new StringBody(String.valueOf(count),
								Charset.forName("UTF-8")));
					if(listImages != null && listImages.size() > 0){
						int j = 1;
						for (int i = imageCount; i < listImages.size(); i++){
							if (listImages.get(i).startsWith("http://")
									|| listImages.get(i).startsWith("https://")) {

								reqEntity.addPart("image"+j, new StringBody(listImages.get(i),
										Charset.forName("UTF-8")));

							}else{
								File imageFile = new File(listImages.get(i));
								FileBody bodyImage = new FileBody(imageFile, "", "UTF-8");
								reqEntity.addPart("image"+j, bodyImage);
							}

							j++;

						}
					}

					totalSize = reqEntity.getContentLength();
					post.setEntity(reqEntity);
					HttpResponse response = client.execute(post);
					HttpEntity resEntity = response.getEntity();
					response_str = EntityUtils.toString(resEntity);

				} catch (ClientProtocolException e) {
					Log.e("ClientProtocolException??",
							"ClientProtocolException?? " + e.toString());
				} catch (IOException e) {
					Log.e("IOException??", "IOException?? " + e.toString());
				}
				return response_str;
			}

			@Override
			protected void onProgressUpdate(String... progress) {
				super.onProgressUpdate(progress);
				AlertEditPage.getInstance().isUploading(true);
				seek_bar_radio.setProgress(Integer.parseInt(progress[0]));
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (result != null && !result.equals("")) {
					AlertEditPage.getInstance().setRequestResult(result);
				}
			}
		}.execute(stringExtra, encodeImoString, encodeImoString2, string,
				string2, srcPath, mode, state, phone, sun,
				mond, tues, wed, thur, fri, sat);

	}

	/* Get Video Advertisement */
	public static void getVideoAdvertisement(final Context ctx,
			final String name, final ProgressBar pDialog) {

		cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent == false) {
			Utilities.showAlertDialog(ctx, "Internet Connection Error",
					"Please connect to working Internet connection", false);
		} else {
			new AsyncTask<Void, Void, ArrayList<GetterSetter>>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					Utilities.showProgressDialog(pDialog);
				}

				@Override
				protected ArrayList<GetterSetter> doInBackground(Void... params) {
					ServiceHandler handle = new ServiceHandler();
					ArrayList<GetterSetter> list_video_add = new ArrayList<GetterSetter>();

					String response = handle.makeServiceCall(
							Config.ROOT_SERVER_CLIENT
									+ Config.GET_VIDEO_ADVERTISEMENT,
							ServiceHandler.GET);
					if (response != null) {

						try {
							JSONObject jsonObject = new JSONObject(response);
							JSONArray jsonArrayItem = jsonObject
									.getJSONArray("value");

							if (jsonArrayItem.length() > 0) {
								for (int i = 0; i < jsonArrayItem.length(); i++) {
									GetterSetter getset = new GetterSetter();
									JSONObject jsonObj = jsonArrayItem
											.getJSONObject(i);
									getset.setPlayListId(jsonObj
											.getString("playlist_id"));
									getset.setTitle(jsonObj.getString("title"));
									getset.setDescriptions(jsonObj
											.getString("description"));
									getset.setVid_Id(jsonObj
											.getString("video_id"));
									getset.setImage(jsonObj.getString("image"));
									getset.setUnique_video_id(jsonObj
											.getString("unique_video_id"));
									getset.setDate(jsonObj.getString("date"));
									getset.setTime(jsonObj.getString("time"));

									if (!jsonObj.getString("status").equals("")
											&& jsonObj.getString("status")
													.equals("true")) {
										getset.setImageResource(R.drawable.tick_icon);
										getset.setStatus(jsonObj
												.getString("status"));

									} else {
										getset.setImageResource(R.drawable.tick_gray);
										getset.setStatus(jsonObj
												.getString("status"));

									}

									list_video_add.add(getset);
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						((Activity) ctx).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Utilities
										.showAlertDialog(
												ctx,
												"Internet Connection Error",
												"Your internet connection is too slow...",
												false);
							}
						});
					}
					return list_video_add;
				}

				@Override
				protected void onPostExecute(ArrayList<GetterSetter> result) {
					super.onPostExecute(result);
					Utilities.dismissProgressDialog(pDialog);
					if (result != null && !result.equals("")) {
						VideoAdvertisement.getInstance().setAdapter(result);
					}
				}
			}.execute();
		}
	}

	/* Delete Alert Image */
	public static void deleteAlertImage(String id, String imageUrl) {
		new AsyncTask<String, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(String... params) {

				JSONObject jObj = null;
				HttpParams params2 = new BasicHttpParams();
				params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
						HttpVersion.HTTP_1_1);
				DefaultHttpClient mHttpClient = new DefaultHttpClient(params2);
				String url = Config.ROOT_SERVER_CLIENT + Config.DELETE_ALERT_IMAGE;

				HttpPost httppost = new HttpPost(url);

				MultipartEntity multipartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					multipartEntity.addPart("id", new StringBody(params[0]));
					multipartEntity.addPart("imageurl", new StringBody(params[1]));
					httppost.setEntity(multipartEntity);
					HttpResponse response = mHttpClient.execute(httppost);
					HttpEntity r_entity = response.getEntity();
					String strResponse = EntityUtils.toString(r_entity);
					jObj = new JSONObject(strResponse);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return jObj;
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				super.onPostExecute(result);
				if (result != null) {
					AlertEditPage.getInstance().setDeleteImageResponse(result);
				}
			}
		}.execute(id, imageUrl);
	}
}
