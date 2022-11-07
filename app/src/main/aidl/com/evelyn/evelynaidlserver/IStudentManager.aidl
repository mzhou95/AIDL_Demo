// IBookInterface.aidl
package com.evelyn.evelynaidlserver;

import com.evelyn.evelynaidlserver.Student;
import com.evelyn.evelynaidlserver.IStudentListener;

interface IStudentManager {
    List<Student> getStudentList(); // get all students
    void addStudent(in Student student); // add a student
    void removeStudent(in Student student); // remove a student
    void registerListener(IStudentListener listener);
    void unregisterListener(IStudentListener listener);
}
