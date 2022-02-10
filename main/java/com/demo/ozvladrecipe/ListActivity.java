package com.demo.ozvladrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ozvladrecipe.adapters.OnRecipeListener;
import com.demo.ozvladrecipe.adapters.RecipeRecyclerAdapter;
import com.demo.ozvladrecipe.models.Recipe;
import com.demo.ozvladrecipe.util.VerticalSpacingItemDecorator;
import com.demo.ozvladrecipe.viewmodels.ListViewModel;

import java.util.List;

public class ListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "ListActivity";

    private ListViewModel mListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);
        mListViewModel = new ViewModelProvider(this).get(ListViewModel.class);

        initRecyclerView();
        initSearchView();
        subscribeObservers();

        if (!mListViewModel.isViewingRecipes()) {
            displaySearchCategories();
        }
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

    }

    private void initRecyclerView() {
        mAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mRecyclerView.canScrollVertically(1)) {
                    mListViewModel.searchNextPage();
                }
            }
        });
    }

    private void initSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipeApi(query, 1);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void subscribeObservers() {
        mListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null && mListViewModel.isViewingRecipes()) {
                    mAdapter.setRecipes(recipes);
                    mListViewModel.setIsPerformingQuery(false);
                }
            }
        });

        mListViewModel.isQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    mAdapter.setQueryExhausted();
                }
            }
        });
    }

    private void searchRecipeApi(String query, int pageNumber) {
        mAdapter.displayLoading();
        mListViewModel.searchRecipeApi(query, pageNumber);
    }


    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipeApi(category, 1);
        mSearchView.clearFocus();
    }

    private void displaySearchCategories() {
        mListViewModel.setIsViewingRecipes(false);
        mAdapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if (mListViewModel.onBackPressed()) {
            super.onBackPressed();
        } else {
            displaySearchCategories();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_categories) {
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }
}