package com.demo.ozvladrecipe.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.demo.ozvladrecipe.models.Recipe;
import com.demo.ozvladrecipe.repositories.RecipeRepository;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private String mRecipeId;
    private Boolean mDidRetrievedRecipe;

    public RecipeViewModel() {
        this.mRecipeRepository = RecipeRepository.getInstance();
        mDidRetrievedRecipe = false;
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipeRepository.getRecipe();
    }

    public void RecipeApi(String recipeId) {
        mRecipeId = recipeId;
        mRecipeRepository.RecipeApi(recipeId);
    }

    public String getRecipeId() {
        return mRecipeId;
    }

    public LiveData<Boolean> isRecipeTimeOut() {
        return mRecipeRepository.isRecipeTimeOut();
    }

    public Boolean didRetrievedRecipe() {
        return mDidRetrievedRecipe;
    }

    public void setRetrievedRecipe(Boolean mDidRetrievedRecipe) {
        this.mDidRetrievedRecipe = mDidRetrievedRecipe;
    }
}
