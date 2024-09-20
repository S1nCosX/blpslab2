package com.blps.lab1.services;

import ch.qos.logback.core.joran.sanity.Pair;
import com.blps.lab1.model.user.User;
import com.blps.lab1.repositories.UserRepository;
import com.blps.lab1.utils.UserXMLManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.swing.text.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public User create(User user) {
        userRepository.save(user);

        UserXMLManager userXMLManager = new UserXMLManager();
        userXMLManager.saveUser(user);

        return user;
    }

    public boolean checkIfUserExists(String userEmail) {
        var user = userRepository.findByEmail(userEmail);

        return user.isPresent();
    }

    public void storeUser(User user) {
        List<User> users = new ArrayList<>();
        try {
            File xmlFile = new File("users.xml");
            var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            var userList = doc.getElementsByTagName("user");

            for (int i = 0; i < userList.getLength(); i++) {
                var userNode = userList.item(i);
                var email = userNode.getAttributes().getNamedItem("email").getTextContent();
                User localUser = userRepository.findByEmail(email).get();
                users.add(localUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
