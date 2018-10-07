package com.tysovsky.charbo;

import android.os.Debug;
import android.util.Log;

import com.tysovsky.charbo.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    private static String url = "http://ec2-52-39-140-122.us-west-2.compute.amazonaws.com/api/";
    private OkHttpClient httpClient;

    public NetworkManager(){
        httpClient = new OkHttpClient();
    }

    public static Request getAllUsersRequest(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url+"users").newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        return request;

    }

    public static Request getMoneyRequestRequest(String borrowerId, String lenderId, String amount, String memo, String dateDue){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url+"money_request").newBuilder();

        RequestBody body = new FormBody.Builder()
                .add("lender_id", lenderId)
                .add("borrower_id", borrowerId)
                .add("amount", amount)
                .add("memo", memo)
                .add("date_due", dateDue)
                .add("borrower_name", MainActivity.currentUserName)
                .build();


        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(body)
                .build();

        return request;
    }

    public static Request approveMoneyRequestRequest(String request_id, boolean approved){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url+"money_request_approved").newBuilder();

        RequestBody body = new FormBody.Builder()
                .add("request_id", request_id)
                .add("approved", approved?"True":"False")
                .add("lender_name", MainActivity.currentUserName)
                .build();


        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(body)
                .build();

        return request;
    }

    public static Request getUpdateFirebaseTokenRequest(String userId, String firebaseId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url+"update_firebase_token").newBuilder();

        RequestBody body = new FormBody.Builder()
                .add("user_id", userId)
                .add("firebase", firebaseId)
                .build();


        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(body)
                .build();

        return request;
    }

    public void getAllUsers(final GetAllUsersListener listener){

        httpClient.newCall(getAllUsersRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.UsersReceived(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    listener.UsersReceived(convertJsonToUserList(response.body().string()));
                }
                catch (Exception ex){

                }
            }
        });

    }

    public void RequestMoney(String borrowerId, String lenderId, String amount, String memo, String dateDue, final MoneyRequestListener listener){
        httpClient.newCall(getMoneyRequestRequest(borrowerId, lenderId, amount, memo, dateDue)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.OnMoneyRequestCompleted();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    listener.OnMoneyRequestCompleted();
                }
                catch (Exception ex){

                }
            }
        });
    }

    public void UpdateFirebaseId(String userId, String firebaseId, final NetworkResponseListener listener){
        httpClient.newCall(getUpdateFirebaseTokenRequest(userId, firebaseId)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.OnNetworkResponse("UPDATE_FIREBASE_ID", false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    listener.OnNetworkResponse("UPDATE_FIREBASE_ID", true);
                }
                catch (Exception ex){

                }
            }
        });
    }

    public void AcceptMoneyRequest(String request_id,  boolean accept, final NetworkResponseListener listener){
        httpClient.newCall(approveMoneyRequestRequest(request_id, accept)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.OnNetworkResponse("ACCEPT_MONEY_REQUEST", false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    listener.OnNetworkResponse("ACCEPT_MONEY_REQUEST", true);
                }
                catch (Exception ex){

                }
            }
        });
    }

    private static ArrayList<User> convertJsonToUserList(String json) throws JSONException{
        ArrayList<User> users = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jUser = jsonArray.getJSONObject(i);
            if(!MainActivity.currentUserId.equals(jUser.getString("_id"))){
                User user = new User();

                user.id = jUser.getString("_id");
                user.FirstName = jUser.getString("first_name");
                user.LastName = jUser.getString("last_name");
                user.AccountId = jUser.getString("acc_id");

                users.add(user);
            }



        }

        return users;
    }

}
