package com.sonet.core.repository;

import com.sonet.core.model.Role;
import com.sonet.core.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@AllArgsConstructor
public class UserReadOnlyRepository {

    private final JdbcDao jdbcDao;

    private final String FIND_LIKE_FIRST_AND_LAST_NAME = """
            SELECT 
            us.first_name, 
            us.last_name, 
            us.id, 
            us.age, 
            us.role, 
            us.city, 
            us.uuid, 
            us.email, 
            us.gender, 
            us.birthdate, 
            us.biography 
            FROM \\"users\\" us 
            WHERE us.first_name LIKE '%' || ? || '%'\\n
            AND us.last_name LIKE '%' || ? || '%';
            """;

    private final String FIND_FRIENDS_BY_ID = """
            select 
            us.first_name, 
            us.last_name, 
            us.id, 
            us.age, 
            us.role, 
            us.city, 
            us.uuid, 
            us.email, 
            us.gender, 
            us.birthdate, 
            us.biography  
            from friends fr
            join users us on us.id = fr.friend_id
            where fr.user_id= ?;
            """;

    private final String FIND_BY_UUID = """
            select 
            us.first_name, 
            us.last_name, 
            us.id, 
            us.age, 
            us.role, 
            us.city, 
            us.uuid, 
            us.email, 
            us.gender, 
            us.birthdate, 
            us.biography  
            from users us
            where us.uuid = ?::uuid;
            """;

    private final String FIND_BY_UUID_WITH_PASSWORD = """
            select 
            us.first_name, 
            us.last_name, 
            us.id, 
            us.age, 
            us.role, 
            us.city, 
            us.uuid, 
            us.email, 
            us.gender, 
            us.birthdate, 
            us.biography,
            us.password  
            from users us
            where us.uuid = ?::uuid;
            """;

    private final String FIND_BY_EMAIL = """
            select 
            us.first_name, 
            us.last_name, 
            us.id, 
            us.age, 
            us.role, 
            us.city, 
            us.uuid, 
            us.email, 
            us.gender, 
            us.birthdate, 
            us.biography  
            from users us
            where us.email = ?;
            """;

    private final String FIND_BY_EMAIL_WITH_PASSWORD = """
            select 
            us.first_name, 
            us.last_name, 
            us.id, 
            us.age, 
            us.role, 
            us.city, 
            us.uuid, 
            us.email, 
            us.gender, 
            us.birthdate, 
            us.biography,
            us.password  
            from users us
            where us.email = ?;
            """;

    public Optional<User> findByUuid(UUID uuid, boolean includingPassword) {
        ThrowingConsumer<PreparedStatement, SQLException> queryParamsConsumer = preparedStatement -> {
            preparedStatement.setString(1, uuid.toString());
        };

        ThrowingFunction<ResultSet, User, SQLException> resultMappingFunction = resultSet -> {
            if (resultSet.next()) {
                return includingPassword ? mapToUserWithPassword(resultSet) : mapToUser(resultSet);
            } else {
                return null;
            }
        };
        return Optional.ofNullable(jdbcDao.executeSelect(includingPassword ? FIND_BY_UUID_WITH_PASSWORD : FIND_BY_UUID, queryParamsConsumer, resultMappingFunction));
    }

    public Optional<User> findByEmail(String email, boolean includingPassword) {
        ThrowingConsumer<PreparedStatement, SQLException> queryParamsConsumer = preparedStatement -> {
            preparedStatement.setString(1, email);
        };

        ThrowingFunction<ResultSet, User, SQLException> resultMappingFunction = resultSet -> {
            if (resultSet.next()) {
                return includingPassword ? mapToUserWithPassword(resultSet) : mapToUser(resultSet);
            } else {
                return null;
            }
        };
        return Optional.ofNullable(jdbcDao.executeSelect(includingPassword ? FIND_BY_EMAIL_WITH_PASSWORD : FIND_BY_EMAIL, queryParamsConsumer, resultMappingFunction));
    }

    public List<User> findFriendsById(Integer userId) {
        ThrowingConsumer<PreparedStatement, SQLException> queryParamsConsumer = preparedStatement -> {
            preparedStatement.setInt(1, userId);
        };

        ThrowingFunction<ResultSet, List<User>, SQLException> resultMappingFunction = resultSet -> {
            List<User> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(mapToUser(resultSet));
            }
            return list;
        };
        return jdbcDao.executeSelect(FIND_FRIENDS_BY_ID, queryParamsConsumer, resultMappingFunction);
    }

    public List<User> findLikeFirstAndLastNames(String firstName, String lastName) {
        ThrowingConsumer<PreparedStatement, SQLException> queryParamsConsumer = preparedStatement -> {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
        };

        ThrowingFunction<ResultSet, List<User>, SQLException> resultMappingFunction = resultSet -> {
            List<User> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(mapToUser(resultSet));
            }
            return list;
        };
        return jdbcDao.executeSelect(FIND_LIKE_FIRST_AND_LAST_NAME, queryParamsConsumer, resultMappingFunction);
    }

    private User mapToUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .firstName(resultSet.getString(1))
                .lastName(resultSet.getString(2))
                .id(resultSet.getInt(3))
                .age(resultSet.getShort(4))
                .role(Role.valueOf(resultSet.getString(5)))
                .city(resultSet.getString(6))
                .uuid(UUID.fromString(resultSet.getString(7)))
                .email(resultSet.getString(8))
                .gender(resultSet.getString(9))
                .birthdate(resultSet.getDate(10))
                .biography(resultSet.getString(11))
                .build();

    }

    private User mapToUserWithPassword(ResultSet resultSet) throws SQLException {
        User user = mapToUser(resultSet);
        user.setPassword(resultSet.getString(12));
        return user;
    }
}
