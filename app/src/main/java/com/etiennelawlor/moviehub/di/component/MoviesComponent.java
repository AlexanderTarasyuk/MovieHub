package com.etiennelawlor.moviehub.di.component;

import com.etiennelawlor.moviehub.di.module.MoviesModule;
import com.etiennelawlor.moviehub.presentation.movies.MoviesFragment;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by etiennelawlor on 2/9/17.
 */

@Singleton
@Subcomponent(modules = {MoviesModule.class})
public interface MoviesComponent {
    // Setup injection targets
    void inject(MoviesFragment target);
}
