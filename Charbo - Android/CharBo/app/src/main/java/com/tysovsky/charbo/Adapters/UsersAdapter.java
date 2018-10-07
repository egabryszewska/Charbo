package com.tysovsky.charbo.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tysovsky.charbo.Models.User;
import com.tysovsky.charbo.R;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {
    public UsersAdapter(Context context, List<User> users, ViewGroup parent){
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_view_item, parent, false);
        }

        User currentUser = getItem(position);

        TextView name = (TextView)convertView.findViewById(R.id.user_name);
        name.setText(currentUser.FirstName + " " + currentUser.LastName);

        return convertView;
    }
}
