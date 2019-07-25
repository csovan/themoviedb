package com.csovan.themoviedb.data.model.people;

import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName("birthday")
    private String dateOfBirth;
    @SerializedName("known_for_department")
    private String knownFor;
    @SerializedName("deathday")
    private String dateOfDeath;
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("gender")
    private Integer gender;
    @SerializedName("biography")
    private String biography;
    @SerializedName("popularity")
    private Double popularity;
    @SerializedName("place_of_birth")
    private String placeOfBirth;
    @SerializedName("profile_path")
    private String profilePath;

    public Person(String dateOfBirth, String knownFor, String dateOfDeath, Integer id, String name, Integer gender,
                  String biography, Double popularity, String placeOfBirth, String profilePath) {
        this.dateOfBirth = dateOfBirth;
        this.knownFor = knownFor;
        this.dateOfDeath = dateOfDeath;
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.biography = biography;
        this.popularity = popularity;
        this.placeOfBirth = placeOfBirth;
        this.profilePath = profilePath;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getKnownFor() {
        return knownFor;
    }

    public void setKnownFor(String knownFor) {
        this.knownFor = knownFor;
    }

    public String getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(String dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
