package com.sonet.core.repository;

import com.sonet.core.model.entity.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /*
    insert into friends (user_id, friend_id)
    select 1, fr.id from users fr
    where fr.uuid ='237796c0-008e-4c98-bd49-19d14f8d6785';
     */
    @Modifying
    @Query(value = """
            insert into friends (user_id, friend_id)
            select :id, fr.id from users fr
            where fr.uuid = :uuid
            """)
    boolean insertFriend(@Param("id") Integer id, @Param("uuid") UUID uuid);

    /*
    delete from friends where user_id = 1
    and friend_id = (select fr.id from users fr
    where fr.uuid ='237796c0-008e-4c98-bd49-19d14f8d6785');
     */
    @Modifying
    @Query(value = """
            delete from friends where user_id = :id
            and friend_id = (select fr.id from users fr
            where fr.uuid = :uuid);
            """)
    boolean deleteFriend(@Param("id") Integer id, @Param("uuid") UUID uuid);

    Optional<User> findByEmail(String email);
}
