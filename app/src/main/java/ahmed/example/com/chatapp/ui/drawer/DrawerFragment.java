package ahmed.example.com.chatapp.ui.drawer;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ahmed.example.com.chatapp.GlideApp;
import ahmed.example.com.chatapp.models.UserData;
import ahmed.example.com.chatapp.R;
import ahmed.example.com.chatapp.ui.LoginActivity;
import ahmed.example.com.chatapp.ui.Profile;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrawerFragment extends Fragment {


    @BindView(R.id.drawer_list)
    ListView listView;//navigation List
    View view;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.image)
    ImageView image;
    private DrawerLayout mDrawerLayout;
    private boolean isOpen;
    private UserData user;


    public DrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_drawer, container, false);
        ButterKnife.bind(this, view);
        setList();
        return view;
    }

    private void setList() {

        final List<DrawerData> drawerDataList = new ArrayList<>();
        drawerDataList.add(new DrawerData(R.string.profile, R.drawable.ic_person));
        drawerDataList.add(new DrawerData(R.string.sign_out, R.drawable.ic_sign_out));
//        drawerDataList.add(new DrawerData(R.string.settings, R.drawable.ic_settings));
//        drawerDataList.add(new DrawerData(R.string.about_us, R.drawable.ic_info));
//        drawerDataList.add(new DrawerData(R.string.sign_out, R.drawable.ic_info));

        DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), drawerDataList);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: {
                        startActivity(new Intent(getActivity(), Profile.class));
                        break;
                    }
                    case 1: {

                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                        break;
                    }

                }
            }
        });


    }

    /**
     * get Data from Main Map and set it to the Drawer
     *
     * @param userData :User Data
     */
    public void setUserData(UserData userData, Context context) {
        user = userData;
        if (image != null) {
            if(context==null)return;
            GlideApp.with(context)
                    .load(userData.getImage())
                    .placeholder(R.drawable.user_pic)
                    .error(R.drawable.user_pic)
                    .apply(RequestOptions.circleCropTransform())
                    .into(this.image);
        }
        this.name.setText(userData.getName());

    }

    public void setUpDrawer(DrawerLayout drawerLayout, Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        final ActionBarDrawerToggle mDrawerToggle =
                new ActionBarDrawerToggle(getActivity(),
                                          drawerLayout,
                                          toolbar,
                                          R.string.app_name,
                                          R.string.app_name
                ) {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        isOpen = true;
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        isOpen = false;
                    }
                };
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    public class DrawerData {

        int title;
        int image;

        public DrawerData(int title, int image) {
            this.title = title;
            this.image = image;
        }

    }
}