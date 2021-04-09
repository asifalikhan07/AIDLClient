// IAdd.aidl
package com.example.aidlpocdt;

// Declare any non-default types here with import statements


import com.example.aidlpocdt.Person;


interface IAdd {

    int addNumbers(int num1, int num2);//2 argument method to add
    List<String> getStringList();
    List<Person> getPersonList();
}

