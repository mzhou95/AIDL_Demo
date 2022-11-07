package com.evelyn.evelynaidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StudentManagerService extends Service {

    private static final String TAG = "StudentManagerService";

    private int mStudentCount = 0;
    private CopyOnWriteArrayList<Student> mStudentList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IStudentListener> mListenerList = new RemoteCallbackList<>();

    private Binder mBinder = new IStudentManager.Stub() {
        @Override public List<Student> getStudentList() throws RemoteException {
            return mStudentList;
        }

        @Override public void addStudent(Student student) throws RemoteException {
            mStudentList.add(student);
        }

        @Override public void removeStudent(Student student) throws RemoteException {
            mStudentList.remove(student);
        }

        @Override
        public void registerListener(IStudentListener listener) throws RemoteException {
            mListenerList.register(listener);
            int num = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "registerListener finish, listener count: " + num);
        }

        @Override
        public void unregisterListener(IStudentListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            int num = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "unregisterListener finish, listener count: " + num);
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStudentList.add(new Student(1, "Evelyn"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int studentId = 1 + mStudentList.size();
            Student student = new Student(studentId, "TestName" + studentId);
            try {
                onStudentAdded(student);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private void onStudentAdded(Student student) throws RemoteException {
        mStudentList.add(student);
        Log.d(TAG, "current student count: " + mStudentList.size());
        int num = mListenerList.beginBroadcast();
        for (int i = 0; i < num; ++i) {
            IStudentListener listener = mListenerList.getBroadcastItem(i);
            Log.d(TAG, "sent onStudentAdded to: " + listener.toString());
            listener.onStudentAdded(student);
        }
        mListenerList.finishBroadcast();
    }

    private void onStudentRemoved(Student student) throws RemoteException {
        mStudentList.remove(student);
        Log.d(TAG, "current student count: " + mStudentList.size());
        int num = mListenerList.beginBroadcast();
        for (int i = 0; i < num; ++i) {
            IStudentListener listener = mListenerList.getBroadcastItem(i);
            Log.d(TAG, "sent onStudentRemoved to: " + listener.toString());
            listener.onStudentRemoved(student);
        }
        mListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {
        @Override public void run() {
            mStudentCount++;
            Message msg = new Message();
            mHandler.sendMessage(msg);
        }
    }
}
