// IStudentListener.aidl
package com.evelyn.evelynaidlserver;

import com.evelyn.evelynaidlserver.Student;

interface IStudentListener {
    void onStudentAdded(in Student student);
    void onStudentRemoved(in Student student);
}
