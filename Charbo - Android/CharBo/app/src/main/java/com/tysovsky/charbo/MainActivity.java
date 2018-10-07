package com.tysovsky.charbo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tysovsky.charbo.Adapters.UsersAdapter;
import com.tysovsky.charbo.Models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetAllUsersListener, MoneyRequestListener, NetworkResponseListener{

    ListView userListView ;
    UsersAdapter usersAdapter;
    ArrayList<User> users;
    NetworkManager networkManager;

    //Ara Carrol - 5bb91985478aecda5536e8a4
    //Dannie Little - 5bb91ccf478aecda5536e8a5
    public static String currentUserId = "5bb91985478aecda5536e8a4";
    public static String currentUserName = "Ara Carrol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        users = new ArrayList<>();
        networkManager = new NetworkManager();

        userListView = (ListView)findViewById(R.id.users_list_view);
        usersAdapter = new UsersAdapter(this, users, null);
        userListView.setAdapter(usersAdapter);


        final Intent intent = getIntent();

        if (intent.hasExtra("request_id")) {
            MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                    .title("Lend money")
                    .positiveText("Lend")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            networkManager.AcceptMoneyRequest(intent.getStringExtra("request_id"), true, MainActivity.this);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            networkManager.AcceptMoneyRequest(intent.getStringExtra("request_id"), false, MainActivity.this);

                        }
                    })
                    .build();
            dialog.show();
        } else {
            // Do something else
        }

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int index = i;
                MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .customView(R.layout.request_money, true)
                        .title("Request money from " + users.get(i).FirstName + " " + users.get(i).LastName)
                        .positiveText("Request")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                EditText amount = dialog.getView().findViewById(R.id.mr_amount);
                                EditText memo = dialog.getView().findViewById(R.id.mr_memo);
                                EditText due_date = dialog.getView().findViewById(R.id.mr_due_date);

                                if(users != null) {
                                    networkManager.RequestMoney(currentUserId, users.get(index).id, amount.getText().toString(), memo.getText().toString(), due_date.getText().toString(), MainActivity.this);
                                }
                            }
                        })
                        .build();
                EditText dateEditText = dialog.getView().findViewById(R.id.mr_due_date);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                dateEditText.setText(sdf.format(calendar.getTime()));
                dialog.show();

            }
        });

        networkManager.getAllUsers(this);
    }

    @Override
    public void UsersReceived(final List<User> users) {

        this.users = (ArrayList<User>)users;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(users != null){
                    usersAdapter.addAll(users);
                    usersAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    public void OnMoneyRequestCompleted() {

    }

    @Override
    public void OnNetworkResponse(String ResponseType, Object data) {

    }
}
