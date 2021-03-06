package com.sam007.samiyal.reg_app;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class p_home extends ListActivity {

    private Integer counter = 0;

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
    private EditText test;
    private Button l_btn,l_cnl;
    private ImageView l_out;
    private DatabaseReference mDb;
    private DatabaseReference oDb;

    private ValueEventListener mPostListener;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    ProgressDialog pd;
    ImageButton backbtn;

    public DrawerLayout d;

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){

            Toast.makeText(getApplicationContext(),"User logged in",Toast.LENGTH_SHORT).show();
        }
        else{
            startActivity(new Intent(getApplicationContext(),login.class));
            Toast.makeText(getApplicationContext(),"User not logged in",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_home);

        mAuth = FirebaseAuth.getInstance();
        packageManager = getPackageManager();

        //d =  geActivity().findViewById(R.id.drawer_layout);


        d = this.findViewById(R.id.drawer_layout);
        pd = new ProgressDialog(this);
        pd.setMessage("Loding");
        pd.show();
        new LoadApplications().execute();

        backbtn = findViewById(R.id.back_btn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SideActivity.class));
            }
        });

        /*l_out = findViewById(R.id.i_logout);


        l_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                d.openDrawer((int)Gravity.START);


                AlertDialog.Builder logOutDialog = new AlertDialog.Builder(p_home.this);
                LayoutInflater inflater = LayoutInflater.from(p_home.this);
                View loview = inflater.inflate(R.layout.logout_dialog,null);
                logOutDialog.setView(loview);
                final AlertDialog lodialog = logOutDialog.create();

                Button go = loview.findViewById(R.id.i_lout);
                Button cnl = loview.findViewById(R.id.i_lcnl);

                go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //------------------------------------------
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), login.class));

                        Toast.makeText(getApplicationContext(),"logout",Toast.LENGTH_SHORT).show();
                        //------------------------------------------

                        lodialog.dismiss();

                    }
                });

                cnl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lodialog.dismiss();
                    }
                });
                lodialog.show();

            }

        });*/

    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(getApplicationContext(),
                    R.layout.snippet_list_row, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listadaptor);
            pd.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mAuth = FirebaseAuth.getInstance();
        final String eMail = mAuth.getCurrentUser().getEmail();
        FirebaseUser user = mAuth.getCurrentUser();

       /*  mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance().getReference().child("Lapps");

        final FirebaseUser user =  mAuth.getCurrentUser();
        String s = user.getUid();
   */     //oDb = FirebaseDatabase.getInstance().getReference().child("Parents");
        //final String uid = oDb.child(s).child("parent").getKey();


        final ApplicationInfo app = applist.get(position);
        try {
            final String p_name = app.packageName;
            //startActivity(new Intent(getApplicationContext(),lock_check.class)); // ..... my working code

            //-------------------  new working code for invoking the pass dialog  ------------------- **

            AlertDialog.Builder setLockDialog = new AlertDialog.Builder(p_home.this);
            LayoutInflater inflater = LayoutInflater.from(p_home.this);
            final View myview = inflater.inflate(R.layout.set_lock,null);
            setLockDialog.setView(myview);
            final AlertDialog dialog = setLockDialog.create();

            final EditText test = myview.findViewById(R.id.c_pass);
            Button go = myview.findViewById(R.id.l_btn);
            Button cnl = myview.findViewById(R.id.l_cnl);



            go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String pass = test.getText().toString().trim();
                    String appName = p_name;
                    if(TextUtils.isEmpty(pass)) {
                        test.setError("Password required.");
                        return;
                    }else {
                        String email = mAuth.getCurrentUser().getEmail();
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    /* ----- insertng app to table ---- */
                                    String uid = mAuth.getCurrentUser().getUid();
                                    String c = FirebaseDatabase.getInstance().getReference().push().getKey();
                                    //counter++;
                                    //FirebaseDatabase.getInstance().getReference().child("Appps").child(uid).child(counter.toString()).setValue(p_name);
                                    FirebaseDatabase.getInstance().getReference().child("LockedApps").child(uid).child(c).setValue(p_name);
                                    /* -------------------------------- */
                                    Toast.makeText(getApplicationContext(),"App is locked",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Password is Wrong",Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });

                    }
                }
            });

            cnl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();

            //-------------------------------------------------------------

            // .......... default code ............
            //This code is for opening particular app
            /*Intent intent = packageManager
                    .getLaunchIntentForPackage(app.packageName);

            if (null != intent) {
                startActivity(intent);
            }*/

        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

    /*

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        switch (item.getItemId()) {
            case R.id.menu_logout: {
                displayAboutDialog();

                break;
            }
            default: {
                result = super.onOptionsItemSelected(item);

                break;
            }
        }

        return result;
    }

    private void displayAboutDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.logout));
        builder.setMessage(getString(R.string.logout));

        builder.setPositiveButton("Know More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://stacktips.com"));
                startActivity(browserIntent);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No Thanks!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    */
}


