package ro.octa.greendaosample.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ro.octa.greendaosample.R;
import ro.octa.greendaosample.dao.DBUser;

/**
 * @author Octa
 */
public class UserListAdapter extends ArrayAdapter<DBUser> {

    private final Activity context;
    private final List<DBUser> users;

    public UserListAdapter(Activity context, List<DBUser> users) {
        super(context, R.layout.raw_layout_user, users);
        this.users = users;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.raw_layout_user, null);
            // configure view holder
            UserViewHolder viewHolder = new UserViewHolder();

            if (rowView != null) {
                viewHolder.userId = (TextView) rowView.findViewById(R.id.user_id);
                viewHolder.userEmail = (TextView) rowView.findViewById(R.id.user_email);

                rowView.setTag(viewHolder);
            }
        }

        // fill data
        if (rowView != null) {
            final UserViewHolder holder = (UserViewHolder) rowView.getTag();
            final DBUser user = users.get(position);
            if (user != null) {

                holder.userId.setText(String.valueOf(user.getId()));
                holder.userEmail.setText(user.getEmail());

            }
        }

        return rowView;
    }

    @Override
    public long getItemId(int position) {
        DBUser item = getItem(position);
        return item.getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    static class UserViewHolder {
        public TextView userId;
        public TextView userEmail;
    }

}
