package com.skb.course.apis.libraryapis.book;

import com.skb.course.apis.libraryapis.book.BookStatusEntity;
import org.springframework.data.repository.CrudRepository;

public interface BookStatusRepository extends CrudRepository<BookStatusEntity, Integer> {
}
