package com.evelyn.evelynaidlserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AIDL Demo Activity";
    private static final int MESSAGE_STUDENT_ADDED = 1;

    private IStudentManager mService;
    private TextView mTextView;

    private IStudentListener mListener = new IStudentListener.Stub() {
        @Override
        public void onStudentAdded(Student student) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_STUDENT_ADDED, student).sendToTarget();
        }

        @Override
        public void onStudentRemoved(Student student) throws RemoteException {
            // do nothing
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STUDENT_ADDED:
                    Log.d(TAG, "new student: " + msg.obj);
                    new StudentListAsyncTask().execute();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IStudentManager studentManager = IStudentManager.Stub.asInterface(service);
            try {
                mService = studentManager;
                studentManager.registerListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.e(TAG, "onServiceDisconnected");
        }
    };

    private class StudentListAsyncTask extends AsyncTask<Void, Void, List<Student>> {
        @Override
        protected List<Student> doInBackground(Void... params) {
            List<Student> list = null;
            try {
                list = mService.getStudentList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Student> studentList) {
            String str = "";
            for (int i = 0; i < studentList.size(); ++i) {
                str += studentList.get(i).toString() + "\n";
            }
            mTextView.setText(str);
        }
    }

    public void getStudentList(View view) {
        new StudentListAsyncTask().execute();
    }


    private class StudentNumAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int num = 0;
            if (mService != null) {
                try {
                    List<Student> studentList = mService.getStudentList();
                    num = studentList.size();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return num;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Toast.makeText(getApplicationContext(), "student count: " + integer, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.student_list);

        Intent intent = new Intent(this, StudentManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null && mService.asBinder().isBinderAlive()) {
            try {
                Log.d(TAG, "onDestroy unregisterListener");
                mService.unregisterListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(mConnection);
    }
}
