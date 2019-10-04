package com.skb.course.apis.libraryapis.user;

import com.skb.course.apis.libraryapis.user.UserBookEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserBookEntityRepository extends CrudRepository<UserBookEntity, Integer> {

        List<UserBookEntity> findByUserIdAndBookId(int userId, int bookId);
}
