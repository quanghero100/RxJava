package quangnv4.com.myapplication;

import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

class CountriesServiceSolved implements CountriesService {

    @Override
    public Single<String> countryNameInCapitals(Country country) {
        return Single.create(subscriber -> {
            subscriber.onSuccess(country.getName().toUpperCase());
        });
    }

    public Single<Integer> countCountries(List<Country> countries) {
        return Single.create(subscriber -> {
            subscriber.onSuccess(countries.size());
        });
    }

    public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(new Function<Country, Long>() {
                    @Override
                    public Long apply(Country country) throws Exception {
                        return country.getPopulation();
                    }
                });
    }

    @Override
    public Observable<String> listNameOfEachCountry(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(new Function<Country, String>() {
                    @Override
                    public String apply(Country country) throws Exception {
                        return country.getName();
                    }
                });
    }

    @Override
    public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(new Function<Country, Country>() {
                    @Override
                    public Country apply(Country country) throws Exception {
                        return country;
                    }
                })
                .skip(2)
                .take(2);
    }

    @Override
    public Single<Boolean> isAllCountriesPopulationMoreThanOneMillion(List<Country> countries) {
        return Observable.fromIterable(countries)
                .all(country -> country.getPopulation() > 1000000); // put your solution here
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(new Function<Country, Country>() {
                    @Override
                    public Country apply(Country country) throws Exception {
                        return country;
                    }
                })
                .filter(country -> country.getPopulation() > 1000000);
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(final FutureTask<List<Country>> countriesFromNetwork) {
        return Observable.fromFuture(countriesFromNetwork, 1, TimeUnit.SECONDS)
                .timeout(1, TimeUnit.SECONDS)
                .flatMap(element -> Observable.fromIterable(element))
                .filter(country -> country.getPopulation() > 1000000)
                .onErrorResumeNext(Observable.empty());

    }

    @Override
    public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {
        Country defaultCountry = new Country("", "USD", 0);
        return Observable.fromIterable(countries)
                .filter(country -> country.getName().equals(countryName))
                .defaultIfEmpty(defaultCountry)
                .map(country -> country.getCurrency());
    }

    @Override
    public Observable<Long> sumPopulationOfCountries(List<Country> countries) {
        return Observable.fromIterable(countries)
                .map(new Function<Country, Long>() {
                    @Override
                    public Long apply(Country country) throws Exception {
                        return country.getPopulation();
                    }
                })
                .reduce(new BiFunction<Long, Long, Long>() {
                    @Override
                    public Long apply(Long aLong, Long aLong2) throws Exception {
                        return aLong + aLong2;
                    }
                })
                .toObservable();
    }

    @Override
    public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
        return Observable.fromIterable(countries)
               .toMap(country -> country.getName(), country -> country.getPopulation());

    }

    @Override
    public Observable<Long> sumPopulationOfCountries(Observable<Country> countryObservable1,
                                                     Observable<Country> countryObservable2) {
        return Observable.merge(countryObservable1, countryObservable2)
                .map(new Function<Country, Long>() {
                    @Override
                    public Long apply(Country country) throws Exception {
                        return country.getPopulation();
                    }
                })
                .reduce(new BiFunction<Long, Long, Long>() {
                    @Override
                    public Long apply(Long aLong, Long aLong2) throws Exception {
                        return aLong + aLong2;
                    }
                })
                .toObservable();
    }

    @Override
    public Single<Boolean> areEmittingSameSequences(Observable<Country> countryObservable1,
                                                    Observable<Country> countryObservable2) {
        return Observable.sequenceEqual(countryObservable1, countryObservable2);
    }
}
