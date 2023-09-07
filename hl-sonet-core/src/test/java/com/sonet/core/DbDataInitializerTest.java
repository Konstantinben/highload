package com.sonet.core;

import com.sonet.core.model.Role;
import com.sonet.core.model.entity.User;
import com.sonet.core.repository.UserRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DbDataInitializerTest {

    private int BULK_SIZE = 200;

    private AtomicInteger counter = new AtomicInteger(1);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String password = null;

    @PostConstruct
    public void initPassword() {
        this.password = passwordEncoder.encode("password");
    }

    @Test
    @Ignore
    public void readUsersFromFileTest() {
        boolean complete = readUsersFromCSV("init/people.csv");
        System.out.println(complete ? "Stored " + counter.get() + " users" : "Error occured at " + counter.get() + " user");
    }

    private boolean readUsersFromCSV(String fileName) {
        List<User> usersBulk = new ArrayList<>();

        // create an instance of BufferedReader
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(streamReader)) {

            // read the first line from the text file
            String line = br.readLine();
            // loop until all lines are read
            while (line != null) {
                String[] attributes = line.split(",");

                User user = createUser(attributes);
                usersBulk.add(user);

                // call bulk save and clear bulk list
                if (counter.get() % BULK_SIZE == 0) {
                    userRepository.saveAll(usersBulk);
                    usersBulk.clear();
                }

                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine();

                // store last bulk
                if (line == null) {
                    userRepository.saveAll(usersBulk);
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public void saveUsers(List<User> users) {
        Iterable<User> savedUsers = userRepository.saveAll(users);
        System.out.println("Stored users "
                + StreamSupport.stream(savedUsers.spliterator(), false)
                .map(User::getId)
                .map(Objects::toString)
                .collect(Collectors.joining(", "))
        );
    }

    private User createUser(String[] metadata) {
        String fullName = metadata[0];
        String[] names = fullName.split(" ");
        String lastName = names[0];
        String firstName = names[1];
        short age = Short.parseShort(metadata[1]);
        String city = metadata[2];
        int count = counter.getAndIncrement();
        String email = "generated" + count + "@test.ru";
        String gender = (firstName.endsWith("я") || firstName.endsWith("a")) ? "F" : "M";

        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .city(city)
                .age(age)
                .gender(gender)
                .email(email)
                .password(password)
                .role(Role.USER)
                .uuid(UUID.randomUUID())
                .build();
    }
}
