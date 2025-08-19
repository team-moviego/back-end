package com.hwansol.moviego.movie.repository;

import static com.hwansol.moviego.movie.model.QMovie.movie;
import static com.hwansol.moviego.movie.model.QMovieGenre.movieGenre;

import com.hwansol.moviego.movie.model.Movie;
import com.hwansol.moviego.movie.model.OrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class MovieCustomRepositoryImpl implements MovieCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Movie> getMovieListWithOptions(String word, String genre, OrderType orderType, Pageable pageable) {
        List<Movie> movieList = jpaQueryFactory.selectFrom(movie)
                .innerJoin(movie.genres, movieGenre)
                .where(containsWord(word), eqGenre(genre), movie.deletedAt.isNull())
                .orderBy(orderSpecifier(orderType))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        int totalElements = jpaQueryFactory.selectFrom(movie)
                .where(containsWord(word), eqGenre(genre), movie.deletedAt.isNull())
                .fetch()
                .size();

        return new PageImpl<>(movieList, pageable, totalElements);
    }

    private BooleanExpression containsWord(String word) {
        if (word == null || word.isBlank()) {
            return null;
        }

        return movie.titleKo.containsIgnoreCase(word).or(movie.titleEn.containsIgnoreCase(word)).or(movie.description.containsIgnoreCase(word));
    }

    private BooleanExpression eqGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            return null;
        }

        return movieGenre.genre.name.eq(genre);
    }

    private OrderSpecifier<?> orderSpecifier(OrderType orderType) {
        if (orderType == null) {
            return null;
        }

        return switch (orderType) {
            case DATE -> movie.releaseDate.desc();
            case GRADE -> movie.gradeAverage.desc();
            case RATE -> movie.reservationRate.desc();
        };
    }
}
