package com.demo.ozvladrecipe.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Recipe implements Parcelable {

    private String[] ingredients;
    private String image_url;
    private String _id;
    private float social_rank;
    private String publisher;
    private String source_url;
    private String recipe_id;
    private String publisher_url;
    private String title;

    public Recipe(String[] ingredients, String image_url, String _id, float social_rank, String publisher, String source_url, String recipe_id, String publisher_url, String title) {
        this.ingredients = ingredients;
        this.image_url = image_url;
        this._id = _id;
        this.social_rank = social_rank;
        this.publisher = publisher;
        this.source_url = source_url;
        this.recipe_id = recipe_id;
        this.publisher_url = publisher_url;
        this.title = title;
    }

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        ingredients = in.createStringArray();
        image_url = in.readString();
        _id = in.readString();
        social_rank = in.readFloat();
        publisher = in.readString();
        source_url = in.readString();
        recipe_id = in.readString();
        publisher_url = in.readString();
        title = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String[] getIngredients() {
        return ingredients;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public float getSocial_rank() {
        return social_rank;
    }

    public void setSocial_rank(float social_rank) {
        this.social_rank = social_rank;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "ingredients=" + Arrays.toString(ingredients) +
                ", image_url='" + image_url + '\'' +
                ", _id='" + _id + '\'' +
                ", social_rank=" + social_rank +
                ", publisher='" + publisher + '\'' +
                ", source_url='" + source_url + '\'' +
                ", recipe_id='" + recipe_id + '\'' +
                ", publisher_url='" + publisher_url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(ingredients);
        dest.writeString(image_url);
        dest.writeString(_id);
        dest.writeFloat(social_rank);
        dest.writeString(publisher);
        dest.writeString(source_url);
        dest.writeString(recipe_id);
        dest.writeString(publisher_url);
        dest.writeString(title);
    }
}
