package com.demo.ozvladrecipe.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.ozvladrecipe.AppExecutors;
import com.demo.ozvladrecipe.models.Recipe;
import com.demo.ozvladrecipe.requests.responses.RecipeResponse;
import com.demo.ozvladrecipe.requests.responses.RecipeSearchResponse;
import com.demo.ozvladrecipe.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class ApiClient {

    private static final String TAG = "ApiClient";

    private static ApiClient instance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private RetrieveRecipesRunnable mRetrieveRecipesRunnable;

    private MutableLiveData<Recipe> mRecipe;
    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;

    private MutableLiveData<Boolean> mRecipeTimeOut;

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    private ApiClient() {
        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
        mRecipeTimeOut = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    public LiveData<Boolean> isRecipeTimeOut() {
        return mRecipeTimeOut;
    }

    public void searchRecipesApi(String query, int pageNumber) {
        if (mRetrieveRecipesRunnable != null) {
            mRetrieveRecipesRunnable = null;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);

        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveRecipesRunnable);

        AppExecutors.getInstance().getNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Timeout network
                mRecipeTimeOut.postValue(true);
                handler.cancel(true);
            }
        }, Constants.TIMEOUT_LIMIT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveRecipesRunnable implements Runnable {

        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, pageNumber).execute();
                if (cancelRequest) {
                    return;
                }
                if (response.code() == 200) {
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse) response.body()).getRecipes());
                    if (pageNumber == 1) {
                        mRecipes.postValue(list);
                    } else {
                        List<Recipe> current = mRecipes.getValue();
                        current.addAll(list);
                        mRecipes.postValue(current);
                    }
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }
        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber) {
            return ServiceGenerator.getApi().searchRecipe(
                    Constants.API_KEY,
                    query,
                    String.valueOf(pageNumber)
            );
        }

        private void cancelRequest() {
            Log.d(TAG, "cancelRequest: canceling the search request");
            cancelRequest = true;
        }
    }

    public void RecipeApi(String recipeId) {
        if (mRetrieveRecipeRunnable != null) {
            mRetrieveRecipeRunnable = null;
        }
        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId);

        final Future handler = AppExecutors.getInstance().getNetworkIO().submit(mRetrieveRecipeRunnable);

        mRecipeTimeOut.setValue(false);
        AppExecutors.getInstance().getNetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // Timeout network
                mRecipeTimeOut.postValue(true);
                handler.cancel(true);
            }
        }, Constants.TIMEOUT_LIMIT, TimeUnit.MILLISECONDS);
    }


    private class RetrieveRecipeRunnable implements Runnable {

        private String recipeId;
        boolean cancelRequest;

        public RetrieveRecipeRunnable(String recipeId) {
            this.recipeId = recipeId;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipe(recipeId).execute();
                if (cancelRequest) {
                    return;
                }
                if (response.code() == 200) {
                    Recipe recipe = ((RecipeResponse) response.body()).getRecipe();
                    mRecipe.postValue(recipe);
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipe.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null);
            }
        }

        private Call<RecipeResponse> getRecipe(String recipeId) {
            return ServiceGenerator.getApi().getRecipe(
                    Constants.API_KEY,
                    recipeId
            );
        }

        private void cancelRequest() {
            Log.d(TAG, "cancelRequest: canceling the request");
            cancelRequest = true;
        }
    }


    public void cancelRequest() {
        if (mRetrieveRecipesRunnable != null) {
            mRetrieveRecipesRunnable.cancelRequest();
        }
        if (mRetrieveRecipeRunnable != null) {
            mRetrieveRecipeRunnable.cancelRequest();
        }
    }
}
