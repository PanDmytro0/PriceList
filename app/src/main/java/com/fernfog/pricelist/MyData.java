package com.fernfog.pricelist;

public class MyData implements Cloneable {

    String photoLink;
    String count;
    String inPack;
    String description;
    String name;
    String nameOfGroup;
    boolean isOPT;
    boolean isOIOD;

    MyData(String name, String photoLink, String count, String inPack, String description) {
        this.name = name;
        this.photoLink = photoLink;
        this.count = count;
        this.inPack = inPack;
        this.description = description;
    }

    MyData(String name, String photoLink, String count, String inPack, String description, String nameOfGroup) {
        this.name = name;
        this.photoLink = photoLink;
        this.count = count;
        this.inPack = inPack;
        this.description = description;
        this.nameOfGroup = nameOfGroup;
    }

    MyData(String name, String photoLink, String count, String inPack, String description, boolean isOPT, boolean isOIOD, String nameOfGroup) {
        this.name = name;
        this.photoLink = photoLink;
        this.count = count;
        this.inPack = inPack;
        this.description = description;
        this.isOPT = isOPT;
        this.isOIOD = isOIOD;
        this.nameOfGroup = nameOfGroup;
    }
    MyData(String name, String photoLink, String count, String inPack, String description, boolean isOPT, boolean isOIOD) {
        this.name = name;
        this.photoLink = photoLink;
        this.count = count;
        this.inPack = inPack;
        this.description = description;
        this.isOPT = isOPT;
        this.isOIOD = isOIOD;
    }

    MyData(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public String getCount() {
        return count;
    }

    public String getInPack() {
        return inPack;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
