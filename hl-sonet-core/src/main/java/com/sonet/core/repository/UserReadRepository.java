package com.sonet.core.repository;

import com.sonet.core.configuration.jdbc.ReadOnlyRepository;
import com.sonet.core.model.entity.User;
import org.springframework.data.jdbc.repository.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ReadOnlyRepository
public interface UserReadRepository extends EntityReadOnlyRepository<User, Long> {

    Optional<User> findByUuid(UUID uuid);

    Optional<User> findByEmail(String email);

/*    @Query("" +
            "SELECT " +
            "us.first_name, \n" +
            "us.last_name, \n" +
            "us.id, \n" +
            "us.age, \n" +
            "us.role, \n" +
            "us.city, \n" +
            "us.uuid, \n" +
            "us.email, \n" +
            "us.gender, \n" +
            "us.birthdate, \n" +
            "us.biography \n" +
            "FROM \"users\" us \n" +
            "WHERE us.first_name LIKE :firstName \n" +
            "AND us.last_name LIKE :lastName ")
    List<User> findByFirstAndLastNames(String firstName, String lastName);*/

    @Query("" +
            "SELECT " +
            "us.first_name, \n" +
            "us.last_name, \n" +
            "us.id, \n" +
            "us.age, \n" +
            "us.role, \n" +
            "us.city, \n" +
            "us.uuid, \n" +
            "us.email, \n" +
            "us.gender, \n" +
            "us.birthdate, \n" +
            "us.biography \n" +
            "FROM \"users\" us \n" +
            "WHERE us.first_name LIKE '%' || :firstName || '%'\n" +
            "AND us.last_name LIKE '%' || :lastName || '%'")
    List<User> findLikeFirstAndLastNames(String firstName, String lastName);

    List<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);

    /*
        select us.* from friends fr
        join users us on us.id = fr.friend_id
        where fr.user_id=1;
     */
    @Query(value = """
            select us.* from friends fr
            join users us on us.id = fr.friend_id
            where fr.user_id= :userId
            """)
    List<User> findFriendsById(Integer userId);
}
