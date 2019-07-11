package com.skb.course.apis.libraryapis.repository;

import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import org.springframework.data.repository.CrudRepository;

public interface BookStatusRepository extends CrudRepository<BookStatusEntity, Integer> {
}
