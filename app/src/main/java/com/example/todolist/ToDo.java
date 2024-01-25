package com.example.todolist;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class ToDo implements Serializable {



    private String UID;
    private String title;
    private String description;
    private LocalDate date;

    private LocalTime time;

    private String creationDateTime;
    private String positionLat;
    private String positionLng;

    public ToDo(String UID, String title, String description, LocalDate date, LocalTime time, String creationDateTime) {
        this.UID = UID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.creationDateTime = creationDateTime;
    }

    public ToDo(String UID, String title, String description, LocalDate date, LocalTime time, String creationDateTime, String positionLat, String positionLng) {
        this.UID = UID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.creationDateTime = creationDateTime;
        this.positionLat = positionLat;
        this.positionLng = positionLng;
    }



    public String getDateString(String format){
        if( date == null){
            return "";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return date.format(formatter);
        }
    }

    public String getUID() {
        return UID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;

    }

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }



    public void setPositionLat(String positionLat) {
        this.positionLat = positionLat;
    }


    public void setPositionLng(String positionLng) {
        this.positionLng = positionLng;
    }

    public String getPositionLat() {
        return positionLat;
    }

    public String getPositionLng() {
        return positionLng;
    }
}