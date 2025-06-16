package com.grepp.diary.app.model.auth.token;

import com.grepp.diary.app.model.auth.token.entity.UserBlackList;
import org.springframework.data.repository.CrudRepository;

public interface UserBlackListRepository extends CrudRepository<UserBlackList, String> {

}
