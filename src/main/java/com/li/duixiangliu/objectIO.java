package com.li.duixiangliu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

//创建要写入磁盘的类，这个类需要实现接口 Serializable（可系列化的）
class Student implements Serializable {

    // 在这里保证了serialVersionUID 的唯一性，防止属性变量的临时改变，从而造成写入id与读取id不同
    private static final long serialVersionUID = 1L;
    int id; //额外需要添加一个属性

    String name;
    String sex; //transient修饰属性，表示暂时的，则这个属性不会被写入磁盘
    transient int age;

    public Student(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
}

public class objectIO {
    /**
     * 31      * @param args
     * 32      * @throws IOException
     * 33      * @throws ClassNotFoundException
     * 34
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // TODO Auto-generated method stub

        createObj();
        readObj();
    }

    //（一）先写入对象
    public static void createObj() throws IOException {
        //1.创建目标路径
        File file = new File("E:\\objTest.txt");
        if(file.exists()){
            file.deleteOnExit();
        }
        //2.创建流通道
        FileOutputStream fos = new FileOutputStream(file);
        //3.创建对象输出流
        ObjectOutputStream objOP = new ObjectOutputStream(fos);
        //4.创建类对象，并初始化
        Student stu = new Student("玛丽苏", "男", 18);
        //5.向目标路径文件写入对象
        objOP.writeObject(stu);
        //6.关闭资源
        objOP.close();
    }

    //再读取对象
    public static void readObj() throws IOException, ClassNotFoundException {
        File file = new File("E:\\objTest.txt");
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream objIP = new ObjectInputStream(fis);
        //读取对象数据，需要将对象流强制转换为 要写入对象的类型
        Student stu = (Student) objIP.readObject();
        System.out.println("\n name:" + stu.name + "\n sex:" + stu.sex + "\n age:" + stu.age);
        objIP.close();
    }
}
