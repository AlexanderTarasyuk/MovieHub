package com.etiennelawlor.moviehub.domain;

import com.etiennelawlor.moviehub.data.repositories.movie.MovieDataSourceContract;
import com.etiennelawlor.moviehub.data.repositories.movie.models.MoviesPage;
import com.etiennelawlor.moviehub.util.EspressoIdlingResource;
import com.etiennelawlor.moviehub.util.rxjava.SchedulerTransformer2;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by etiennelawlor on 6/26/17.
 */

public class MoviesUseCase implements MoviesDomainContract.UseCase {

    // region Member Variables
    private final MovieDataSourceContract.Repository movieRepository;
    private final SchedulerTransformer2<MoviesPage> schedulerTransformer;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    // endregion

    // region Constructors
    public MoviesUseCase(MovieDataSourceContract.Repository movieRepository, SchedulerTransformer2<MoviesPage> schedulerTransformer) {
        this.movieRepository = movieRepository;
        this.schedulerTransformer = schedulerTransformer;
    }
    // endregion

    // region MoviesDomainContract.UseCase Methods
    @Override
    public void clearSubscriptions() {
        if(compositeSubscription != null && compositeSubscription.hasSubscriptions())
            compositeSubscription.clear();

        if(compositeDisposable != null && compositeDisposable.isDisposed())
            compositeDisposable.clear();
    }

    @Override
    public void getPopularMovies(int currentPage, DisposableSingleObserver disposableSingleObserver) {
        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        Disposable disposable = movieRepository.getPopularMovies(currentPage)
                .compose(schedulerTransformer)
//        https://github.com/VisheshVadhera/PlacementApp/blob/f36e8c259cbba37c1be90409016854f8c64bb8a5/app/src/main/java/com/vishesh/placement/core/useCases/BaseUseCase.java
                .doFinally(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                })
                .subscribeWith(disposableSingleObserver);

        compositeDisposable.add(disposable);
    }
    // endregion
}
