package com.skb.course.apis.libraryapis.repository;

import com.skb.course.apis.libraryapis.entity.BookEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepository extends CrudRepository<BookEntity, Integer> { //}, QuerydslPredicateExecutor<BookEntity> {

    BookEntity findByIsbn(String isbn);
    List<BookEntity> findByTitleContaining(String name);
}
