package pt.brunoribeiro.examples.rest.service;

import pt.brunoribeiro.examples.rest.model.User;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Stateless
public class UserService {

    private static final Map<Long, User> users = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong();

    static {
        User user1 = new User(
                sequence.incrementAndGet(),
                "Bruno",
                "bruno@example.com"
        );

        User user2 = new User(
                sequence.incrementAndGet(),
                "Maria",
                "maria@example.com"
        );

        users.put(user1.getId(), user1);
        users.put(user2.getId(), user2);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public User create(String name, String email) {

        Long id = sequence.incrementAndGet();

        User user = new User(id, name, email);

        users.put(id, user);

        return user;
    }

    public User update(Long id, String name, String email) {

        User user = users.get(id);

        if (user == null) {
            return null;
        }

        user.setName(name);
        user.setEmail(email);

        return user;
    }

    public boolean delete(Long id) {
        return users.remove(id) != null;
    }
}